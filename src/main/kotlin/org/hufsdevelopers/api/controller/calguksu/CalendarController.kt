package org.hufsdevelopers.api.controller.calguksu

import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.repository.CalendarRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController
@RequestMapping("/calguksu/calendars")
class CalendarController(val calendarRepository: CalendarRepository) {

    @GetMapping()
    fun getCalendars(): ResponseEntity<List<Calendar>> {
        val cal = calendarRepository.findAll()
        println(cal)
        return ResponseEntity.ok(calendarRepository.findAll())
    }

    @GetMapping("/{name}")
    fun getCalendar(@PathVariable("name") name: String): ResponseEntity<Calendar> {
        return try {
            ResponseEntity.ok(calendarRepository.findFirstByName(name))
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}