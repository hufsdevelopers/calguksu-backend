package org.hufsdevelopers.api.controller

import com.google.common.io.ByteStreams.toByteArray
import jakarta.servlet.http.HttpServletResponse
import org.hufsdevelopers.api.domain.Event
import org.hufsdevelopers.api.repository.EventRepository
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
@RequestMapping("/events")
class EventController(val eventRepository: EventRepository, val HUFSCalendarService: HUFSCalendarService) {
    @GetMapping()
    fun getEvents(@RequestParam year: Int?, @RequestParam month: Int?): ResponseEntity<List<Event>> {
        if (month != null && year == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        if (year != null && month != null) {
            val events = eventRepository.getEvents(
                ZonedDateTime.of(year, month, 1, 0, 0, 0 ,0, ZoneId.of("Asia/Seoul")),
                ZonedDateTime.of(year, month, 31, 23, 59, 59 ,0, ZoneId.of("Asia/Seoul"))
            )
            return ResponseEntity.ok(events)
        }

        if (year != null) {
            val events = eventRepository.getEvents(
                ZonedDateTime.of(year, 1, 1, 0, 0, 0 ,0, ZoneId.of("Asia/Seoul")),
                ZonedDateTime.of(year, 12, 31, 23, 59, 59 ,0, ZoneId.of("Asia/Seoul"))
            )
            return ResponseEntity.ok(events)
        }

        val events = eventRepository.findAll()
        return ResponseEntity.ok(events)
    }


    @GetMapping(value = ["/subscribe"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun getFile(response : HttpServletResponse): FileSystemResource? {
        val calendarsDir = File("calendars")
        val calendarFile = File(calendarsDir, "hufs.ics")

        response.setHeader("Content-Disposition", "attachment; filename=" + "calendar.ics");
        return FileSystemResource(calendarFile)
    }
}