package org.hufsdevelopers.api.controller.calguksu

import jakarta.servlet.http.HttpServletResponse
import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.repository.CalendarRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.io.IOException


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


    @GetMapping(value = ["/{name}/subscribe"], produces = ["text/calendar"])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun getFile(
        response: HttpServletResponse,
        @PathVariable("name") name: String,
        @RequestParam(value = "id") id: String
    ): FileSystemResource? {
        if (name == "hufsofficial") {
            val calendarsDir = File("calendars")
            val calendarFile = File(calendarsDir, "hufsofficial.ics")

            response.setHeader("Content-Disposition", "attachment; filename=" + "calendar.ics")
            return FileSystemResource(calendarFile)
        } else {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "sorry! not supported calendar currently."
            )
        }
    }
}