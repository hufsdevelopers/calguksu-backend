package org.hufsdevelopers.api.controller

import org.hufsdevelopers.api.domain.Event
import org.hufsdevelopers.api.repository.EventRepository
import org.hufsdevelopers.api.service.CalendarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Month

@RestController
@RequestMapping("/events")
class EventController(val eventRepository: EventRepository, val calendarService: CalendarService) {
    @GetMapping()
    fun getEvents(@RequestParam year: Int?, @RequestParam month: Int?): ResponseEntity<List<Event>> {
        if (month != null && year == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        if (year != null && month != null) {
            val events = eventRepository.getEventByYearAndMonth(year, month)
            return ResponseEntity.ok(events)
        }

        if (year != null) {
            val events = eventRepository.getEventByYear(year)
            return ResponseEntity.ok(events)
        }

        val events = eventRepository.findAll()
        calendarService.createIcsCalendar(events)
        return ResponseEntity.ok(events)
    }
}