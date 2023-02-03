package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.domain.Calendar
import org.hufsdevelopers.calguksu.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface EventRepository : JpaRepository<Event, Int> {
    fun findByCalendar(calendar: Calendar): List<Event>
    @Query(value = "select * from calendar.events where (events.calendar_id = :calendarId) and ((events.start_timestamp between :startDate and :endDate) or (events.end_timestamp between :startDate and :endDate)) ", nativeQuery = true)
    fun getEvents(calendarId : Int, startDate : ZonedDateTime, endDate : ZonedDateTime) : List<Event>
}