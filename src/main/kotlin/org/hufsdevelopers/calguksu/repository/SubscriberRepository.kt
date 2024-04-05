package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.entities.CalendarEntity
import org.hufsdevelopers.calguksu.entities.SubscriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriberRepository : JpaRepository<SubscriberEntity, Int> {
    fun findByEmailAndCalendar(email: String, calendarEntity: CalendarEntity): SubscriberEntity?

    fun findByTokenAndCalendar(token: String, calendarEntity: CalendarEntity): SubscriberEntity?
}
