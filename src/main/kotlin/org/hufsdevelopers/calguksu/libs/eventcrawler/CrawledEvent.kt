package org.hufsdevelopers.calguksu.libs.eventcrawler

import java.time.ZonedDateTime

data class CrawledEvent(
    val description: String,
    val startTimestamp: ZonedDateTime,
    val endTimestamp: ZonedDateTime,
    val allday: Boolean = false,
)

fun List<CrawledEvent>.distinctCrawledEvents(): List<CrawledEvent> {
    return this.distinctBy { event ->
        listOf(event.description, event.startTimestamp.toString(), event.endTimestamp.toString(), event.allday.toString())
    }
}
