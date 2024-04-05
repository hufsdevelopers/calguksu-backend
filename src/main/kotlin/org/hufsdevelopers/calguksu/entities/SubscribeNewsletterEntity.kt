package org.hufsdevelopers.calguksu.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.ZonedDateTime

@Entity
@Table(name = "subscribes_newsletter")
class SubscribeNewsletterEntity(
    subscriberEntity: SubscriberEntity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    var subscriber: SubscriberEntity = subscriberEntity
        private set

    @CreationTimestamp
    @Column(name = "subscribe_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    var subscribeDate: ZonedDateTime? = null
}