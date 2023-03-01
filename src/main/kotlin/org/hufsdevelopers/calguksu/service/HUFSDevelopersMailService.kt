package org.hufsdevelopers.calguksu.service

import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sesv2.SesV2Client
import software.amazon.awssdk.services.sesv2.model.*


@Service
class HUFSDevelopersMailService {

    val sesClient : SesV2Client by lazy {
        SesV2Client.builder().region(Region.AP_NORTHEAST_2).build()
    }

    fun send(
        sender: String?,
        recipient: String?,
        subject: String?,
        bodyHTML: String?
    ) {
        val destination: Destination = Destination.builder()
            .toAddresses(recipient)
            .build()
        val content: Content = Content.builder()
            .data(bodyHTML)
            .build()
        val sub: Content = Content.builder()
            .data(subject)
            .build()
        val body: Body = Body.builder()
            .html(content)
            .build()
        val msg: Message = Message.builder()
            .subject(sub)
            .body(body)
            .build()
        val emailContent: EmailContent = EmailContent.builder()
            .simple(msg)
            .build()
        val emailRequest: SendEmailRequest = SendEmailRequest.builder()
            .destination(destination)
            .content(emailContent)
            .fromEmailAddress(sender)
            .build()
        try {
            println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...")
            sesClient.sendEmail(emailRequest)
            println("email was sent")
        } catch (e: SesV2Exception) {
            System.err.println(e.awsErrorDetails().errorMessage())
            System.exit(1)
        }
    }

}