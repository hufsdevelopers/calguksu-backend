package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.domain.Event
import org.hufsdevelopers.calguksu.domain.Subscriber
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberRepository : JpaRepository<Subscriber, Int> {
}