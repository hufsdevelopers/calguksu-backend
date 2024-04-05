package org.hufsdevelopers.calguksu.repository

import org.hufsdevelopers.calguksu.entities.SubscribeNewsletterEntity
import org.hufsdevelopers.calguksu.entities.SubscriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscribeNewsletterRepository : JpaRepository<SubscribeNewsletterEntity, Int> {
    fun existsBySubscriber(subscriber: SubscriberEntity): Boolean
}