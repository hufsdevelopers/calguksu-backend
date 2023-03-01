package org.hufsdevelopers.calguksu.domain

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.CurrentTimestamp
import java.time.ZonedDateTime

@Table(name = "subscribers")
@Entity
class Subscriber(
    token: String, email: String, calendar: Calendar, registeredOn: ZonedDateTime?, lastrequestedOn: ZonedDateTime?
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
    var calendar: Calendar? = calendar
        private set
}