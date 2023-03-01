package org.hufsdevelopers.calguksu.service

import com.google.common.hash.Hashing
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.*
import org.hufsdevelopers.calguksu.domain.Event
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.EventRepository
import org.jsoup.Jsoup
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


@Service
class HUFSCalendarService(calendarRepository: CalendarRepository, val eventRepository: EventRepository) {
    companion object {
        const val URL_CALENDAR_HUFS_AC_KR = "https://www.hufs.ac.kr/user/indexSub.action?codyMenuSeq=37069&siteId=hufs"
    }

    val hufsofficialCalendar = calendarRepository.getReferenceById(1)

    @Scheduled(fixedRate = 1000 * 60 * 60)
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

        var calendarChanges = false

        val localFetchedEvents = eventRepository.findByCalendar(hufsofficialCalendar).toMutableList()
        sourceFetchedEvents.forEach { event ->
            if (!localFetchedEvents.remove(event)) {
                eventRepository.save(event)
                calendarChanges = true
            }
        }

        eventRepository.deleteAll(localFetchedEvents)
        if (localFetchedEvents.isNotEmpty()) calendarChanges = true

        if (calendarChanges || !File("calendars/hufsofficial.ics").exists()) {
            createIcsCalendar()
        }
    }


    fun createIcsCalendar() {
        val icsCalendar = Calendar()
        icsCalendar.properties.add(ProdId("-//hufsdevelopers.org//KO"))
        icsCalendar.properties.add(Version.VERSION_2_0);
        icsCalendar.properties.add(CalScale.GREGORIAN)
        icsCalendar.properties.add(Name("HUFS"))

        // 애플 캘린더 용
        icsCalendar.properties.add(XProperty("X-WR-CALNAME", "HUFS"))
        icsCalendar.properties.add(XProperty("X-APPLE-CALENDAR-COLOR", "#00677f"))

        val events = eventRepository.findAll()
        events.forEach { event ->
            val startUTCTime = event.startTimestamp.withZoneSameInstant(ZoneOffset.UTC)
            val endUTCTime = event.endTimestamp.withZoneSameInstant(ZoneOffset.UTC)

            val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
            val timezone: TimeZone = registry.getTimeZone("UTC")
            val tz: VTimeZone = timezone.vTimeZone

            // the "DATE" type is convert to UTC time automatically so should
            val startDate: java.util.Calendar = GregorianCalendar()
            startDate.timeZone = timezone
            startDate[java.util.Calendar.YEAR] = startUTCTime.year
            startDate[java.util.Calendar.MONTH] = startUTCTime.monthValue - 1
            startDate[java.util.Calendar.DATE] = startUTCTime.dayOfMonth
            startDate[java.util.Calendar.HOUR_OF_DAY] = startUTCTime.hour
            startDate[java.util.Calendar.MINUTE] = startUTCTime.minute
            startDate[java.util.Calendar.SECOND] = startUTCTime.second

            val hasEndDate = startUTCTime.toEpochSecond() != endUTCTime.toEpochSecond()

            var endDate: java.util.Calendar? = null
            if (hasEndDate) {
                endDate = GregorianCalendar()
                endDate.timeZone = timezone
                endDate[java.util.Calendar.YEAR] = endUTCTime.year
                endDate[java.util.Calendar.MONTH] = endUTCTime.monthValue - 1
                endDate[java.util.Calendar.DATE] = endUTCTime.dayOfMonth
                endDate[java.util.Calendar.HOUR_OF_DAY] = endUTCTime.hour
                endDate[java.util.Calendar.MINUTE] = endUTCTime.minute
                endDate[java.util.Calendar.SECOND] = endUTCTime.second
                endDate.add(java.util.Calendar.DATE, 1)
            }

            val meeting: VEvent = if (endDate != null) {
                val start = if (event.allday) Date(startDate.time) else DateTime(startDate.time).apply { isUtc = true }
                val end = if (event.allday) Date(endDate.time) else DateTime(endDate.time).apply { isUtc = true }
                VEvent(start, end, event.description)
            } else {
                val start = if (event.allday) Date(startDate.time) else DateTime(startDate.time).apply { isUtc = true }
                VEvent(start, event.description)
            }

            meeting.properties.add(tz.timeZoneId)
            icsCalendar.components.add(meeting)

            meeting.properties.add(
                Uid(Hashing.sha256().hashString(event.toString(), StandardCharsets.UTF_8).toString())
            )
        }

        val calendarsDir = File("calendars")
        if (!calendarsDir.isDirectory) calendarsDir.mkdirs()
        val calendarFile = File(calendarsDir, "hufsofficial.ics")
        if (!calendarFile.isFile) calendarFile.createNewFile()
        val fout = FileOutputStream(calendarFile.path)
        val outputter = CalendarOutputter()
        outputter.output(icsCalendar, fout)
    }
}