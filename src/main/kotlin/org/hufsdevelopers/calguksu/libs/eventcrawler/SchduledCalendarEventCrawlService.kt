package org.hufsdevelopers.calguksu.libs.eventcrawler

import kotlinx.coroutines.runBlocking
import org.hufsdevelopers.calguksu.libs.eventcrawler.crawlers.CalguksuEventCrawler
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.EventRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SchduledCalendarEventCrawlService(
    val eventCrawlers: List<CalguksuEventCrawler>,
    val calendarRepository: CalendarRepository,
    val eventRepository: EventRepository
) {

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    fun crawlEvents() = runBlocking {
        eventCrawlers.forEach {
            println(it.calendarName)
            println(it.getEvents())
        }
    }
}