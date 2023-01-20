package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.Calendar
import org.hufsdevelopers.api.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Int> {
    fun findByCalendar(calendar: Calendar): List<Event>

    @Query(value = "select * from calendar.events where start_year = :year or end_year = :year", nativeQuery = true)
    fun getEventByYear(year : Int) : List<Event>

    @Query(value = "select * from calendar.events where (start_year = :year and start_month = :month) or (end_year = :year and end_month = :month)", nativeQuery = true)
    fun getEventByYearAndMonth(year : Int, month : Int) : List<Event>
}