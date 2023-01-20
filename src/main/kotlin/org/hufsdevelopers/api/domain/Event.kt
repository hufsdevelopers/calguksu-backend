package org.hufsdevelopers.api.domain

import jakarta.persistence.*


@Table(name = "events")
@Entity
class Event(
    calendar: Calendar, startYear: Int, startMonth: Int, startDay: Int,
    endYear: Int, endMonth: Int, endDay: Int, description: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val eventId: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    var calendar: Calendar? = calendar
        private set

    @Column(name = "startYear")
    var startYear: Int = startYear
        private set

    @Column(name = "startMonth")
    var startMonth: Int = startMonth
        private set

    @Column(name = "startDay")
    var startDay: Int = startDay
        private set

    @Column(name = "endYear")
    var endYear: Int = endYear
        private set

    @Column(name = "endMonth")
    var endMonth: Int = endMonth
        private set

    @Column(name = "endDay")
    var endDay: Int = endDay
        private set

    @Column(name = "description", length = 500)
    var description: String = description
        private set

    override fun equals(other: Any?): Boolean {
        return if (other is Event) {
            startDay == other.startDay && startMonth == other.startMonth && startYear == other.startYear &&
                    endDay == other.endDay && endMonth == other.endMonth && endYear == other.endYear &&
                    description == other.description
        } else {
            false
        }
    }

    override fun toString(): String {
        return "$startYear.$startMonth.$startDay ~ $endYear.$endMonth.$endDay : $description"
    }
}