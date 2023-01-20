package org.hufsdevelopers.api.repository

import org.hufsdevelopers.api.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Calendar

interface CalendarRepository : JpaRepository<org.hufsdevelopers.api.domain.Calendar, Int>