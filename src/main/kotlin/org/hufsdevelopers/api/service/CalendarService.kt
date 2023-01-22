package org.hufsdevelopers.api.service

import com.google.common.hash.Hashing
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.parameter.Cn
import net.fortuna.ical4j.model.parameter.Role
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.util.FixedUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import org.hufsdevelopers.api.data.DateHolder
import org.hufsdevelopers.api.domain.Event
import org.hufsdevelopers.api.repository.CalendarRepository
import org.hufsdevelopers.api.repository.EventRepository
import org.jsoup.Jsoup
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


@Service
class CalendarService(calendarRepository: CalendarRepository, val eventRepository: EventRepository) {
    companion object {
        const val URL_CALENDAR_HUFS_AC_KR = "https://www.hufs.ac.kr/user/indexSub.action?codyMenuSeq=37069&siteId=hufs"
    }

    val hufsofficialCalendar = calendarRepository.getReferenceById(1)

    @Scheduled(fixedRate = 10000)
    fun getUpdates() {
        val wholeSource = Jsoup.connect(URL_CALENDAR_HUFS_AC_KR).get()

        // 캘린더 우측에서 각 이벤트 선택
        val sourceFetchedEvents = mutableListOf<Event>()

        wholeSource.getElementsByClass("sch02_box").forEach { calendar ->
            // 캘린더 좌측에서 캘린더 제목 선택 ex - "2022.00"
            val calendarName = calendar.getElementsByClass("calendar3").first()
                ?.getElementsByTag("strong")?.first()?.text() ?: return@forEach

            // 캘린더 제목에서 year, month 추출 ex - (2022, 00)
            val calendarDate: Pair<Int, Int> = calendarName.split('.').let {
                it[0].toInt() to it[1].toInt()
            }

            calendar.getElementsByClass("calendar4").first()?.getElementsByTag("tr")?.forEach {
                val eventDates = it.firstElementChild()?.text()?.split("~") ?: return@forEach
                val description = it.getElementsByTag("span").first()?.text() ?: return@forEach
                if (description.isBlank()) return@forEach

                val startDate: ZonedDateTime = eventDates[0].split(".").let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second >= month) calendarDate.first else calendarDate.first - 1

                    ZonedDateTime.of(
                        year,
                        month,
                        day,
                        9,
                        0,
                        0,
                        0,
                        ZoneId.of("Asia/Seoul")
                    )
                }

                val endDate: ZonedDateTime? = eventDates.getOrNull(1)?.split(".")?.let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second <= month) calendarDate.first else calendarDate.first + 1

                    ZonedDateTime.of(
                        year,
                        month,
                        day,
                        21,
                        0,
                        0,
                        0,
                        ZoneId.of("Asia/Seoul")
                    )
                }

                sourceFetchedEvents.add(
                    Event(
                        calendar = hufsofficialCalendar,
                        startTimestamp = startDate,
                        endTimestamp = endDate ?: startDate,
                        description = description,
                        allday = true
                    )
                )
            } ?: return@forEach
        }

        val localFetchedEvents = eventRepository.findByCalendar(hufsofficialCalendar).toMutableList()
        sourceFetchedEvents.forEach { event ->
            if (!localFetchedEvents.remove(event)) {
                eventRepository.save(event)
                System.out.println("event saved ${event}")
            }
        }

        eventRepository.deleteAll(localFetchedEvents)
        System.out.println("${localFetchedEvents.size} events removed")
    }
}