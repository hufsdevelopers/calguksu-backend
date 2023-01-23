package org.hufsdevelopers.api.controller.calguksu

import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.repository.CalendarRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/calguksu/calendars")
class CalendarController(val calendarRepository: CalendarRepository) {

    @GetMapping()
    fun getCalendars(): ResponseEntity<List<Calendar>> {
        val cal = calendarRepository.findAll()
        println(cal)
        return ResponseEntity.ok(calendarRepository.findAll())
    }
}