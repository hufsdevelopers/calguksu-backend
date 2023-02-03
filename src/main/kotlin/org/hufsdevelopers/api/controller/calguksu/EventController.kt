package org.hufsdevelopers.api.controller.calguksu

import com.google.common.io.ByteStreams.toByteArray
import jakarta.servlet.http.HttpServletResponse
import org.hufsdevelopers.api.domain.Event
import org.hufsdevelopers.api.repository.CalendarRepository
import org.hufsdevelopers.api.repository.EventRepository
import org.hufsdevelopers.api.responseentities.EventReponseEntity
import org.hufsdevelopers.api.service.HUFSCalendarService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime


@RestController
@RequestMapping("/calguksu/events")
class EventController(val calendarRepository: CalendarRepository, val eventRepository: EventRepository) {
    @GetMapping()
    fun getEvents(
        @RequestParam calendarName: String,
        @RequestParam year: Int?,
        @RequestParam month: Int?
    ): ResponseEntity<List<EventReponseEntity>> {
        if (month != null && year == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val calendar = calendarRepository.findFirstByName(calendarName)

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

        return ResponseEntity.ok(events.map {
            EventReponseEntity(
                it.calendar!!.name,
                it.startTimestamp.toString(),
                it.endTimestamp.toString(),
                it.allday,
                it.description
            )
        })
    }
}