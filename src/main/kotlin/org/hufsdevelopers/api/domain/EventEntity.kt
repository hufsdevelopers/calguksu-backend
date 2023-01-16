package org.hufsdevelopers.api.domain

import jakarta.persistence.*


@Entity
@Table(name = "events")
data class EventEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private val eventId: Int? = null,

        @Column(name = "startYear")
        private var startYear: Int = 0,

        @Column(name = "startMonth")
        private var startMonth: Int = 0,

        @Column(name = "startDay")
        private var startDay: Int = 0,

        @Column(name = "endYear")
        private var endYear: Int = 0,

        @Column(name = "endMonth")
        private var endMonth: Int = 0,

        @Column(name = "endDay")
        private var endDay: Int = 0,

        @Column(name = "description")
        private var description: String = ""
) {}