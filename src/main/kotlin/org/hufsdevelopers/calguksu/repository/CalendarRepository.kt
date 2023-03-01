package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.domain.Calendar
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarRepository : JpaRepository<Calendar, Int> {
    fun findFirstByName(calendarName: String): Calendar?
}