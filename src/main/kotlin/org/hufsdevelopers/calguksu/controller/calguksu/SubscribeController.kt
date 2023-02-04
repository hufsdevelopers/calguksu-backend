package org.hufsdevelopers.calguksu.controller.calguksu

import org.hufsdevelopers.calguksu.data.SubscribeFormData
import org.hufsdevelopers.calguksu.domain.Calendar
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscribe")
class SubscribeController {
    @PostMapping()
    fun getCalendar(@RequestBody form : SubscribeFormData): ResponseEntity<*> {
        return ResponseEntity.ok({})
    }
}