package org.hufsdevelopers.calguksu.entities

import jakarta.persistence.*

@Table(name = "calendars")
@Entity
class CalendarEntity(name: String, title: String, description: String?) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val calendarId: Int = 0

    @Column(name = "name", length = 20, nullable = false)
    var name: String = name
        private set

    @Column(name = "title", length = 50, nullable = false)
    var title: String = title
        private set

    @Column(name = "description", length = 200, nullable = true)
    var description: String? = description
        private set
}