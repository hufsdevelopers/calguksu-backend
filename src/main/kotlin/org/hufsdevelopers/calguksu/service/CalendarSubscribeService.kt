package org.hufsdevelopers.calguksu.service

import org.hufsdevelopers.calguksu.entities.CalendarEntity
import org.hufsdevelopers.calguksu.entities.SubscribeNewsletterEntity
import org.hufsdevelopers.calguksu.entities.SubscriberEntity
import org.hufsdevelopers.calguksu.exceptions.EmailSendTimeLimitationException
import org.hufsdevelopers.calguksu.repository.CalendarRepository
import org.hufsdevelopers.calguksu.repository.SubscribeNewsletterRepository
import org.hufsdevelopers.calguksu.repository.SubscriberRepository
import org.springframework.stereotype.Service
import java.util.*

const val ADDRESS_EMAIL_CALGUKSU_HUFS: String = "\"Calguksu.com\" <order-calguksu-noreply@hufsdevelopers.org>"

@Service
class SubscribeService(
    val subscriberRepository: SubscriberRepository,
    val calendarRepository: CalendarRepository,
    val subscribeNewsletterRepository: SubscribeNewsletterRepository,
    val hufsDevelopersMailService: HUFSDevelopersMailService
) {

    fun subscribe(calendarEntity: CalendarEntity, email: String, newsletterSubscribe: Boolean) {
        var subscriber = subscriberRepository.findByEmailAndCalendar(email, calendarEntity)

        if (subscriber == null) {
            val token = UUID.randomUUID().toString()
            subscriber = subscriberRepository.save(SubscriberEntity(token, email, calendarEntity, null, null))
        } else {
            if ((subscriber.mailsentOn != null) && (System.currentTimeMillis() - subscriber.mailsentOn!!.toEpochSecond() * 1000) < 1 * 60 * 1000) {
                return throw EmailSendTimeLimitationException((60 - ((System.currentTimeMillis() - subscriber.mailsentOn!!.toEpochSecond() * 1000)) / 1000).toInt())
            } else {
                subscriberRepository.save(subscriber.apply {
                    mailsentOn = null
                })
            }
        }

        if (newsletterSubscribe && !subscribeNewsletterRepository.existsBySubscriber(subscriber)) {
            subscribeNewsletterRepository.save(SubscribeNewsletterEntity(subscriber))
        }


        sendSubscribeMail(subscriber, calendarEntity)
    }

    fun sendSubscribeMail(subscriberEntity: SubscriberEntity, targetCalendarEntity: CalendarEntity) {
        val subscribeUrl =
            "https://calguksu.com/subscription?cn=${targetCalendarEntity.name}&ct=${subscriberEntity.token}"
        hufsDevelopersMailService.send(
            ADDRESS_EMAIL_CALGUKSU_HUFS,
            subscriberEntity.email,
            "${targetCalendarEntity.title} 캘린더가 배달되었습니다.",
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                    "        \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "<head>\n" +
                    "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
                    "    <title>HTML Email Template</title>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                    "    <style type=\"text/css\">\n" +
                    "        table {\n" +
                    "            border-collapse: separate;\n" +
                    "        }\n" +
                    "\n" +
                    "        a, a:link, a:visited {\n" +
                    "            text-decoration: underline;\n" +
                    "            color: #1A202C;\n" +
                    "        }\n" +
                    "\n" +
                    "        a:hover {\n" +
                    "            text-decoration: underline;\n" +
                    "        }\n" +
                    "\n" +
                    "        h2, h2 a, h2 a:visited, h3, h3 a, h3 a:visited, h4, h5, h6, .t_cht {\n" +
                    "            color: #1A202C !important;\n" +
                    "        }\n" +
                    "\n" +
                    "        .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td {\n" +
                    "            line-height: 100%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .ExternalClass {\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; background:#F7FAFC;\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\">\n" +
                    "            <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 650px;\">\n" +
                    "                <tbody>\n" +
                    "                <tr>\n" +
                    "                    <td align=\"center\" style=\"padding: 36px 0\">\n" +
                    "                        <a href=\"https://calguksu.com\" target=\"_blank\">\n" +
                    "                            <img src=\"https://calguksu.com/assets/ic_logo.png\" width=\"180px\" height=\"auto\"/>\n" +
                    "                        </a>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                <tr>\n" +
                    "                    <td align=\"center\">\n" +
                    "                        <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 650px; background:#FFFFFF;\">\n" +
                    "                            <tbody>\n" +
                    "                            <tr>\n" +
                    "                                <td align=\"center\" style=\"padding: 48px 0 24px 0\">\n" +
                    "                                    <p style=\"margin: 0; padding: 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 30px; font-weight: 600; line-height: 1.42; letter-spacing: -0.21px; color: #1A202C; -webkit-text-size-adjust: 100%;\">\n" +
                    "                                        주문하신 캘린더가<br>배달 완료되었습니다!\n" +
                    "                                    </p>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td align=\"center\">\n" +
                    "                                    <p style=\"margin: 0; padding: 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 16px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%;\">\n" +
                    "                                        <u>${targetCalendarEntity.title}</u></p>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td align=\"center\">\n" +
                    "                                    <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                    "                                           style=\"width: 90%; margin: 56px 0 0 0;\">\n" +
                    "                                        <tbody>\n" +
                    "                                        <tr>\n" +
                    "                                            <td width=\"20%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                            <td width=\"70%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td width=\"20%\" align=\"center\"\n" +
                    "                                                style=\"padding: 10px 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #E2E8F0;\">\n" +
                    "                                                주문\n" +
                    "                                                번호\n" +
                    "                                            </td>\n" +
                    "                                            <td width=\"5%\"></td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #E2E8F0;\"></td>\n" +
                    "                                            <td width=\"70%\"\n" +
                    "                                                style=\"padding: 10px 0;  font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #E2E8F0;\">\n" +
                    "                                                ${subscriberEntity.token}\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td width=\"20%\" align=\"center\"\n" +
                    "                                                style=\"padding: 10px 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #E2E8F0;\">\n" +
                    "                                                주문\n" +
                    "                                                일시\n" +
                    "                                            </td>\n" +
                    "                                            <td width=\"5%\"></td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #E2E8F0;\"></td>\n" +
                    "                                            <td width=\"70%\"\n" +
                    "                                                style=\"padding: 10px 0;  font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #E2E8F0;\">\n" +
                    "                                                ${Date()}\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td width=\"20%\" align=\"center\"\n" +
                    "                                                style=\"padding: 10px 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #4A5568;\">\n" +
                    "                                                주문\n" +
                    "                                                단계\n" +
                    "                                            </td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                            <td width=\"5%\" style=\"border-bottom: 1px solid #4A5568;\"></td>\n" +
                    "                                            <td width=\"70%\"\n" +
                    "                                                style=\"padding: 10px 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%; border-bottom: 1px solid #4A5568;\">\n" +
                    "                                                배달 완료\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        </tbody>\n" +
                    "                                    </table>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            <tr>\n" +
                    "                                <td align=\"center\" style=\"padding: 56px 0 48px 0;\">\n" +
                    "                                    <a href=\"${subscribeUrl}\" target=\"_blank\"\n" +
                    "                                       style=\"padding: 12px 24px; color: #FFFFFF; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 14px; font-weight: 400; line-height: 1.42; letter-spacing: -0.21px; webkit-text-size-adjust: 100%; text-decoration: none; background: #1A202C; border-radius: 8px;\">구독하기</a>\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            </tbody>\n" +
                    "                        </table>\n" +
                    "                    </td>\n" +
                    "                </tr>\n" +
                    "                </tbody>\n" +
                    "            </table>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 36px 0 12px 0\">\n" +
                    "            <p style=\"margin: 0; padding: 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 12px; font-weight: 400; line-height: 1.6; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%;\">\n" +
                    "                본 메일은 발신전용 메일이므로 회신이 되지 않습니다.<br>\n" +
                    "                문의사항은 <a href=\"https://calguksu.com\" target=\"_blank\" style=\"color: #1A202C;\">홈페이지</a>내 문의를 이용하시기 바랍니다.\n" +
                    "            </p>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 0 0 36px 0\">\n" +
                    "            <p style=\"margin: 0; padding: 0; font-family: BlinkMacSystemFont, 'Apple SD Gothic Neo', '맑은 고딕', sans-serif; font-size: 12px; font-weight: 400; line-height: 1.6; letter-spacing: -0.21px; color: #4A5568; -webkit-text-size-adjust: 100%;\">\n" +
                    "                ‘<a href=\"https://calguksu.com\" target=\"_blank\" style=\"color: #1A202C;\">칼국수닷컴</a>‘은 ‘<a\n" +
                    "                    href=\"https://hufsdevelopers.org\" target=\"_blank\" style=\"color: #1A202C;\">hufsdevelopers.org</a>‘의\n" +
                    "                제품이며, hufsdevelopers에 의해 운영되고 있습니다.\n" +
                    "            </p>\n" +
                    "        </td>\n" +
                    "    </tr>\n" +
                    "    </tbody>\n" +
                    "</table>\n" +
                    "</body>"
        )
    }
}

