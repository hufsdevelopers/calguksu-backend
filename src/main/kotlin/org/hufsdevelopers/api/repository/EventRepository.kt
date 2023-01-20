package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Int> {
    fun findByCalendar(calendar: Calendar): List<Event>
}