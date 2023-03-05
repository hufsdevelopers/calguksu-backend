package org.hufsdevelopers.calguksu.controller.calguksu

import org.hufsdevelopers.calguksu.data.HttpResponse
import org.hufsdevelopers.calguksu.domain.Event
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.EventRepository
import org.hufsdevelopers.calguksu.responseentities.EventReponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.time.ZonedDateTime


@RestController
@RequestMapping("/events")
class EventController(val calendarRepository: CalendarRepository, val eventRepository: EventRepository) {
    @GetMapping()
    fun getEvents(
        @RequestParam calendarName: String,
        @RequestParam year: Int?,
        @RequestParam month: Int?
    ): ResponseEntity<Any> {
        if (month != null && year == null) {
            return ResponseEntity.badRequest().body(HttpResponse(false, "field 'year' required"))
        }

        val calendar = calendarRepository.findFirstByName(calendarName) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val events: List<Event> = if (year != null) {
            if (month != null) {
                eventRepository.getEvents(
                    calendar.calendarId!!,
                    ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul")),
                    ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul")).plusMonths(1).minusNanos(1)
                )
            } else {
                eventRepository.getEvents(
                    calendar.calendarId!!,
                    ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneId.of("Asia/Seoul")),
                    ZonedDateTime.of(year, 12, 31, 23, 59, 59, 0, ZoneId.of("Asia/Seoul"))
                )
            }
        } else {
            eventRepository.findByCalendar(calendar)
        }

        return ResponseEntity.ok(HttpResponse(true, events.map {
            EventReponseEntity(
                it.calendar!!.name,
                it.startTimestamp.toString(),
                it.endTimestamp.toString(),
                it.allday,
                it.description
            )
        }))
    }
}