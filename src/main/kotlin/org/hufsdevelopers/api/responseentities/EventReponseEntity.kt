package org.hufsdevelopers.api.responseentities

data class EventReponseEntity(
    val calendarName: String,
    val start: String,
    val end: String,
    val allday: Boolean,
    val description: String
) {

}