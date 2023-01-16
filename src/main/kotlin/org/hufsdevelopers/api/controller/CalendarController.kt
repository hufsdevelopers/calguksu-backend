package org.hufsdevelopers.api.controller

import org.apache.coyote.Response
import org.hufsdevelopers.api.domain.EventEntity
import org.hufsdevelopers.api.repository.EventRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/events")
class CalendarController(val eventRepository: EventRepository) {
    @GetMapping()
    fun getEvents(): ResponseEntity<*> {
        val events = eventRepository.findAll()
        return ResponseEntity.ok(events)
    }
}