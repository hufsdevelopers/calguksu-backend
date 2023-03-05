package org.hufsdevelopers.calguksu.controller.calguksu

import org.apache.commons.validator.routines.EmailValidator
import org.hufsdevelopers.calguksu.data.FeedbackFormData
import org.hufsdevelopers.calguksu.data.HttpResponse
import org.hufsdevelopers.calguksu.service.FeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feedback")
class FeedbackController(val feedbackService: FeedbackService) {
    @PostMapping
    fun sendFeedback(@RequestBody feedbackFormData: FeedbackFormData): ResponseEntity<Any> {
        if (!EmailValidator.getInstance().isValid(feedbackFormData.replyEmail)) {
            return ResponseEntity.status(400).body(HttpResponse(false, "invalid email request"))
        } else {
            feedbackService.sendFeedback(feedbackFormData.content, feedbackFormData.replyEmail)
            return ResponseEntity.ok(HttpResponse(true, "문의 접수가 완료되었습니다. 문의 답장까지 평균 2일 소요됩니다."))
        }
    }
}