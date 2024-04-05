package org.hufsdevelopers.calguksu.data

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class SubscribeFormData(
    val email: String,
    val calendarName: String,
    val subscribeNewsletter: Boolean?
) {
}