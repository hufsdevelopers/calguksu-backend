package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Calendar

@Repository
interface CalendarRepository : JpaRepository<org.hufsdevelopers.api.domain.Calendar, Int>