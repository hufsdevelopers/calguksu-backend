package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Repository
interface EventRepository : JpaRepository<Event, Int> {
    fun findByCalendar(calendar: Calendar): List<Event>
    @Query(value = "select * from calendar.events where (events.start_timestamp between :startDate and :endDate) or (events.end_timestamp between :startDate and :endDate)", nativeQuery = true)
    fun getEvents(startDate : ZonedDateTime, endDate : ZonedDateTime) : List<Event>
}