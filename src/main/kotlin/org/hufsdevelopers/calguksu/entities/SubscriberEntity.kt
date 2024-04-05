package org.hufsdevelopers.calguksu.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime

    @Table(name = "subscribers")
@Entity
class SubscriberEntity(
    token: String, email: String, calendarEntity: CalendarEntity, registeredOn: ZonedDateTime?, lastrequestedOn: ZonedDateTime?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null

    @Column(name = "token")
    var token: String = token

    @Column(name = "email")
    var email: String = email

    @CreationTimestamp
    @Column(name = "mailsent_on", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    var mailsentOn: ZonedDateTime? = registeredOn

    @Column(name = "lastrequested_on", nullable = true)
    var lastrequestedOn: ZonedDateTime? = lastrequestedOn

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    var calendar: CalendarEntity? = calendarEntity
        private set
}