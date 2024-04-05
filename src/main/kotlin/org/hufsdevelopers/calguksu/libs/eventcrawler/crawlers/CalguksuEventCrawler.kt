package org.hufsdevelopers.calguksu.libs.eventcrawler.crawlers

import org.hufsdevelopers.calguksu.libs.eventcrawler.CrawledEvent

abstract class CalguksuEventCrawler {
    abstract val calendarName: String
    abstract suspend fun getEvents(): List<CrawledEvent>
}