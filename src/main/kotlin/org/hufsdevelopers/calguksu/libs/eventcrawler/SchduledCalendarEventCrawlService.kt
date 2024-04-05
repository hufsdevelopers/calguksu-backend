package org.hufsdevelopers.calguksu.libs.eventcrawler

import com.google.common.hash.Hashing
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.*
import org.hufsdevelopers.calguksu.entities.CalendarEntity
import org.hufsdevelopers.calguksu.entities.EventEntity
import org.hufsdevelopers.calguksu.entities.toUniqueKey
import org.hufsdevelopers.calguksu.libs.eventcrawler.crawlers.CalguksuEventCrawler
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.EventRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.time.ZoneOffset
import java.util.*

@Service
class SchduledCalendarEventCrawlService(
    val eventCrawlers: List<CalguksuEventCrawler>,
    val calendarRepository: CalendarRepository,
    val eventRepository: EventRepository
) {

    @Scheduled(fixedDelay = 1000 * 10)
    fun crawlEvents() = runBlocking {
        eventCrawlers.forEach {
            val calendar = calendarRepository.findFirstByName(it.calendarName) ?: return@forEach
            val crawledEvents: List<CrawledEvent> = it.getEvents()
            val eventEntities: List<EventEntity> = eventRepository.findByCalendar(calendar)

            // Convert CrawledEvent to EventEntity for comparison
            val crawledEventEntities = crawledEvents.map { crawledEvent ->
                EventEntity(
                    calendar,
                    crawledEvent.startTimestamp,
                    crawledEvent.endTimestamp,
                    crawledEvent.allday,
                    crawledEvent.description
                )
            }

            // Find new events to add
            val newEvents = crawledEventEntities.filter { crawledEvent ->
                eventEntities.none { existingEvent ->
                    existingEvent.toUniqueKey() == crawledEvent.toUniqueKey()
                }
            }

            // Find old events to remove
            val eventsToRemove = eventEntities.filter { existingEvent ->
                crawledEventEntities.none { crawledEvent ->
                    existingEvent.toUniqueKey() == crawledEvent.toUniqueKey()
                }
            }

            newEvents.forEach { event ->
                eventRepository.save(event)
            }

            eventsToRemove.forEach { event ->
                eventRepository.delete(event)
            }

            if (newEvents.isNotEmpty() || eventsToRemove.isNotEmpty())
                createIcsCalendar(calendar)
        }
    }

    fun createIcsCalendar(calendarEntity: CalendarEntity) {
        val icsCalendar = Calendar()
        icsCalendar.properties.add(ProdId("-//hufsdevelopers.org//KO"))
        icsCalendar.properties.add(Version.VERSION_2_0);
        icsCalendar.properties.add(CalScale.GREGORIAN)
        icsCalendar.properties.add(Name(calendarEntity.title))

        // 애플 캘린더 용
        icsCalendar.properties.add(XProperty("X-WR-CALNAME", calendarEntity.title))
        icsCalendar.properties.add(XProperty("X-APPLE-CALENDAR-COLOR", "#00677f"))

        val events = eventRepository.findByCalendar(calendarEntity)
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
        val calendarFile = File(calendarsDir, calendarEntity.name + ".ics")
        if (!calendarFile.isFile) calendarFile.createNewFile()
        val fout = FileOutputStream(calendarFile.path)
        val outputter = CalendarOutputter()
        outputter.output(icsCalendar, fout)
    }
}