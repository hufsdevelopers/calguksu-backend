package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<EventEntity, Long>