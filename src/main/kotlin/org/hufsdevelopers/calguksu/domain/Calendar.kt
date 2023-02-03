package org.hufsdevelopers.calguksu.domain

import jakarta.persistence.*

@Table(name = "calendars")
@Entity
class Calendar(name: String, title: String, description: String?) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val calendarId: Int? = null

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