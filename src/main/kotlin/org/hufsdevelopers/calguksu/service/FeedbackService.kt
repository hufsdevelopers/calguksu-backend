package org.hufsdevelopers.calguksu.service

import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service

const val ADDRESS_EMAIL_CALGUKSU_NOREPLY: String = "\"Calguksu.com\" <support-calguksu-noreply@hufsdevelopers.org>"

@Service
class FeedbackService(val hufsDevelopersMailService: HUFSDevelopersMailService) {
    fun sendFeedback(content: String, replyEmail: String) {
        hufsDevelopersMailService.send(
            ADDRESS_EMAIL_CALGUKSU_NOREPLY,
            "support@hufsdevelopers.org",
            "칼국수 서비스에서 발신하는 문의입니다",
            "${content}<br><br>회신 이메일 : $replyEmail"
        )

        hufsDevelopersMailService.send(
            ADDRESS_EMAIL_CALGUKSU_NOREPLY,
            replyEmail,
            "문의가 접수되었습니다.",
            "<h1>문의가 접수되었습니다</h1>최대한 빠른 시일내에 답장을 드릴 수 있도록 하겠습니다.<br><br>감사합니다.<br>Calguksu.com"
        )
    }
}