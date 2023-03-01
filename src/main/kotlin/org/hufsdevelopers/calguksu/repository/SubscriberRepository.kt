package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.domain.Calendar
import org.hufsdevelopers.calguksu.domain.Event
import org.hufsdevelopers.calguksu.domain.Subscriber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriberRepository : JpaRepository<Subscriber, Int> {
    fun findByEmailAndCalendar(email: String, calendar: Calendar): Subscriber?

    fun findByTokenAndCalendar(token: String, calendar: Calendar): Subscriber?
}