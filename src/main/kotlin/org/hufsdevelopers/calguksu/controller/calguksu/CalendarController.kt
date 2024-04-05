package org.hufsdevelopers.calguksu.controller.calguksu

import jakarta.servlet.http.HttpServletResponse
import org.hufsdevelopers.calguksu.data.HttpResponse
import org.hufsdevelopers.calguksu.entities.CalendarEntity
import org.hufsdevelopers.calguksu.exceptions.CalendarNotFoundException
import org.hufsdevelopers.calguksu.exceptions.HttpReponseExcetion
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.SubscriberRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.io.IOException
import java.time.ZonedDateTime


@RestController
@RequestMapping("/calendars")
class CalendarController(val calendarRepository: CalendarRepository, val subscriberRepository: SubscriberRepository) {

    @GetMapping()
    fun getCalendars(): ResponseEntity<HttpResponse<List<CalendarEntity>>> {
        val cal = calendarRepository.findAll()
        return ResponseEntity.ok(HttpResponse(true, calendarRepository.findAll()))
    }

    @GetMapping("/{name}")
    fun getCalendar(@PathVariable("name") name: String): ResponseEntity<Any> {
        return try {
            calendarRepository.findFirstByName(name)?.let {
               return ResponseEntity.ok(HttpResponse(true, it))
            }
            ResponseEntity.status(404).body(HttpResponse(false, CalendarNotFoundException().message))
        } catch (e: HttpReponseExcetion) {
            ResponseEntity.status(e.httpErrorCode).body(HttpResponse(false, e.message))
        } catch (e: Exception) {
            ResponseEntity.status(404).body(HttpResponse(false, CalendarNotFoundException().message))
        }
    }


    @GetMapping(value = ["/{name}/subscribe"], produces = ["text/calendar"])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun getFile(
        response: HttpServletResponse,
        @PathVariable("name") name: String?,
        @RequestParam(value = "token") token: String
    ): FileSystemResource? {
        // 현재 hufsofficial 만 지원함.
        if (name != "hufsofficial") {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "sorry! not supported calendar currently."
            )
        } else {
            val calendar = calendarRepository.findFirstByName("hufsofficial")
            val subscriber = subscriberRepository.findByTokenAndCalendar(token, calendar!!)
                ?: throw ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "잘못된 접근입니다."
                )

            subscriberRepository.save(subscriber.apply {
                lastrequestedOn = ZonedDateTime.now()
            })

            val calendarsDir = File("calendars")
            val calendarFile = File(calendarsDir, "hufsofficial.ics")

            response.setHeader("Content-Disposition", "attachment; filename=" + "calendar.ics")
            return FileSystemResource(calendarFile)
        }
    }
}