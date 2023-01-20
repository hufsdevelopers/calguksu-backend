package org.hufsdevelopers.api.domain

import jakarta.persistence.*

@Entity
@Table(name = "calendars")
class Calendar() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val calendarId: Int? = null

    @Column(name = "name", length = 20, nullable = false)
    private var name: String? = null

    @Column(name = "title", length = 50, nullable = false)
    private var title: String? = null

    @Column(name = "description", length = 200, nullable = true)
    private var description: String? = null
}