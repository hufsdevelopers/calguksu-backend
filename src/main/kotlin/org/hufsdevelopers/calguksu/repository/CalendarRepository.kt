package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.entities.CalendarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarRepository : JpaRepository<CalendarEntity, Int> {
    fun findFirstByName(calendarName: String): CalendarEntity?
}