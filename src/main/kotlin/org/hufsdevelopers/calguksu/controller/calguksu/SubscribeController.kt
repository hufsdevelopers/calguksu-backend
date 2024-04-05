package org.hufsdevelopers.calguksu.controller.calguksu

import org.apache.commons.validator.routines.EmailValidator
import org.hufsdevelopers.calguksu.data.HttpResponse
import org.hufsdevelopers.calguksu.data.SubscribeFormData
import org.hufsdevelopers.calguksu.exceptions.CalendarNotFoundException
import org.hufsdevelopers.calguksu.exceptions.HttpReponseExcetion
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.SubscriberRepository
import org.hufsdevelopers.calguksu.service.SubscribeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscribe")
class SubscribeController(
    val subscribeService: SubscribeService,
    val subscriberRepository: SubscriberRepository,
    val calendarRepository: CalendarRepository
) {
    @PostMapping()
    fun subscribe(
        @RequestBody form: SubscribeFormData
    ): ResponseEntity<Any> {
        if (!EmailValidator.getInstance().isValid(form.email)) {
            return ResponseEntity.status(400).body(HttpResponse(false, "invalid email request"))
        }
        try {
            val calendar =
                calendarRepository.findFirstByName(form.calendarName) ?: throw CalendarNotFoundException()

            subscribeService.subscribe(calendar, form.email, form.subscribeNewsletter ?: false)

            return ResponseEntity.ok(HttpResponse(true, "SUCCESS"))
        } catch (e: HttpReponseExcetion) {
            return ResponseEntity.status(e.httpErrorCode).body(HttpResponse(false, e.message))
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(500).body(HttpResponse(false, "unknown"))
        }
    }


}