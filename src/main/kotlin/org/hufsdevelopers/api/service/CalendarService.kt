package org.hufsdevelopers.api.service

import com.google.common.hash.Hashing
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
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

                val startDate: DateHolder = eventDates[0].split(".").let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second >= month) calendarDate.first else calendarDate.first - 1
                    DateHolder(year, month, day)
                }

                val endDate: DateHolder? = eventDates.getOrNull(1)?.split(".")?.let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second <= month) calendarDate.first else calendarDate.first + 1
                    DateHolder(year, month, day)
                }

                sourceFetchedEvents.add(
                    Event(
                        calendar = hufsofficialCalendar,
                        startYear = startDate.year,
                        startMonth = startDate.month,
                        startDay = startDate.day,
                        endYear = endDate?.year ?: startDate.year,
                        endMonth = endDate?.month ?: startDate.month,
                        endDay = endDate?.day ?: startDate.day,
                        description = description
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

    fun createIcsCalendar(events: List<Event>) {
        val icsCalendar = Calendar()
        icsCalendar.properties.add(ProdId("-//hufsdevelopers.org//KO"))
        icsCalendar.properties.add(Version.VERSION_2_0);
        icsCalendar.properties.add(CalScale.GREGORIAN)
        icsCalendar.properties.add(Name("HUFS"))

        events.forEach { event ->
            val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
            val timezone: TimeZone = registry.getTimeZone("Asia/Seoul")
            val tz: VTimeZone = timezone.vTimeZone

            val startDate: java.util.Calendar = GregorianCalendar()
            startDate.timeZone = timezone
            startDate[java.util.Calendar.MONTH] = event.startMonth - 1
            startDate[java.util.Calendar.DAY_OF_MONTH] = event.startDay
            startDate[java.util.Calendar.YEAR] = event.startYear

            val hasEndDate =
                !(event.startYear == event.endYear && event.startMonth == event.endMonth && event.startDay == event.endDay)

            var endDate: java.util.Calendar? = null
            if (hasEndDate) {
                endDate = GregorianCalendar()
                endDate.timeZone = timezone
                endDate[java.util.Calendar.MONTH] = event.endMonth - 1
                endDate[java.util.Calendar.DAY_OF_MONTH] = event.endDay
                endDate[java.util.Calendar.YEAR] = event.endYear
            }

            val meeting: VEvent = if (endDate != null) {
                val start = DateTime(startDate.time)
                val end = DateTime(endDate.time)
                VEvent(start, end, event.description)
            } else {
                val start = DateTime(startDate.time)
                VEvent(start, event.description)
            }

            meeting.properties.add(tz.timeZoneId)
            icsCalendar.components.add(meeting)

            /* val ug = FixedUidGenerator("uidGen")
             val uid = ug.generateUid()*/

            meeting.properties.add(
                Uid(
                    Hashing.sha256().hashString(event.toString(), StandardCharsets.UTF_8)
                        .toString() + "@hufsdevelopers.org"
                )
            )
            /*   val dev1 = Attendee(URI.create("mailto:dev1@mycompany.com"))
               dev1.parameters.add(Role.REQ_PARTICIPANT)
               dev1.parameters.add(Cn("Developer 1"))
               meeting.properties.add(dev1)

               val dev2 = Attendee(URI.create("mailto:dev2@mycompany.com"))
               dev2.parameters.add(Role.OPT_PARTICIPANT)
               dev2.parameters.add(Cn("Developer 2"))
               meeting.properties.add(dev2)*/

        }

        print(icsCalendar)
        val fout = FileOutputStream("mycalendar.ics")

        val outputter = CalendarOutputter()
        outputter.output(icsCalendar, fout)
    }
}