package org.hufsdevelopers.calguksu.entities

import jakarta.persistence.*
import java.time.ZonedDateTime


@Table(name = "events")
@Entity
class EventEntity(
    calendarEntity: CalendarEntity,
    startTimestamp: ZonedDateTime,
    endTimestamp: ZonedDateTime,
    allday: Boolean = false,
    description: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val eventId: Int = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    var calendar: CalendarEntity? = calendarEntity
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
        return if (other is EventEntity) {
            (startTimestamp.toEpochSecond() == other.startTimestamp.toEpochSecond()) && (endTimestamp?.toEpochSecond() == other.endTimestamp?.toEpochSecond()) && description == other.description
        } else {
            false
        }
    }

    override fun toString(): String {
        return "$startTimestamp ~ $endTimestamp : $description"
    }
}

fun EventEntity.toUniqueKey(): String {
    return "${this.description}-${this.startTimestamp}-${this.endTimestamp}-${this.allday}"
}