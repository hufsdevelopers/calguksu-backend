package org.hufsdevelopers.api.domain

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.*


@Table(name = "events")
@Entity
class Event(
    calendar: Calendar,
    startTimestamp: ZonedDateTime,
    endTimestamp: ZonedDateTime,
    allday: Boolean = false,
    description: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val eventId: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    var calendar: Calendar? = calendar
        private set

    @Column(name = "start_timestamp")
    var startTimestamp: ZonedDateTime = startTimestamp

    @Column(name = "end_timestamp")
    var endTimestamp: ZonedDateTime = endTimestamp

    @Column(name = "allday")
    var allday: Boolean = allday

    @Column(name = "description", length = 500)
    var description: String = description
        private set

    override fun equals(other: Any?): Boolean {
        return if (other is Event) {
            (startTimestamp.toEpochSecond() == other.startTimestamp.toEpochSecond()) && (endTimestamp?.toEpochSecond() == other.endTimestamp?.toEpochSecond()) && description == other.description
        } else {
            false
        }
    }

    override fun toString(): String {
        return "$startTimestamp ~ $endTimestamp : $description"
    }
}