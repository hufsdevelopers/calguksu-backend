package org.hufsdevelopers.calguksu.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Table(name = "subscribers")
@Entity
class Subscriber(
    key: String, email: String, calendar: Calendar
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    var calendar: Calendar? = calendar
        private set

    @Column(name = "key")
    var key: String = key

    @Column(name = "email")
    var email: String = email
}