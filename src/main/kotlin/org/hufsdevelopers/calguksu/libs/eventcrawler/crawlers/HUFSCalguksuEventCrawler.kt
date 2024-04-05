package org.hufsdevelopers.calguksu.libs.eventcrawler.crawlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.hufsdevelopers.calguksu.libs.eventcrawler.CrawledEvent
import org.hufsdevelopers.calguksu.libs.eventcrawler.distinctCrawledEvents
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class HUFSCalguksuEventCrawler : CalguksuEventCrawler() {

    @Autowired
    @Qualifier("hufsWebClient")
    private lateinit var webClient: WebClient
    override val calendarName: String
        get() = "hufsofficial"

    override suspend fun getEvents(): List<CrawledEvent> {
        val events = mutableListOf<CrawledEvent>()

        val startMonth = ZonedDateTime.now().minusMonths(12)
        val endMonth = ZonedDateTime.now().plusMonths(12)

        var currentMonth = startMonth
        while (currentMonth <= endMonth) {
            val year = currentMonth.year.toString()
            val month = currentMonth.monthValue.toString().padStart(2, '0')

            val response = withContext(Dispatchers.IO) {
                webClient.post()
                    .uri("/schdulMain/hufs/6/jsonYearSchdul.do")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue("year=$year&month=$month")
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
            } ?: continue

            events.addAll(parseJsonToCrawledEvents(response))

            currentMonth = currentMonth.plusMonths(1)
        }
        return events.distinctCrawledEvents()
    }

    @Serializable
    data class EventDetail(
        val bgnde: String,
        val sj: String,
        val endde: String
    )
    fun parseJsonToCrawledEvents(jsonString: String): List<CrawledEvent> {
        val json = Json { ignoreUnknownKeys = true }
        val element = json.parseToJsonElement(jsonString).jsonObject

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val zoneId = ZoneId.of("Asia/Seoul")

        return element.map { (_, eventJson) ->
            val event = json.decodeFromJsonElement(EventDetail.serializer(), eventJson)
            CrawledEvent(
                description = event.sj,
                startTimestamp = ZonedDateTime.of(LocalDate.parse(event.bgnde, formatter).atStartOfDay(), zoneId).plusHours(9),
                endTimestamp = ZonedDateTime.of(LocalDate.parse(event.endde, formatter).atTime(23, 59), zoneId).plusHours(9),
                allday = true
            )
        }
    }
}