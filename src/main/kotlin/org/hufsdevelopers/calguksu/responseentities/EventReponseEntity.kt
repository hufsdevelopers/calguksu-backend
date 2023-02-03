package org.hufsdevelopers.calguksu.responseentities

data class EventReponseEntity(
    val calendarName: String,
    val start: String,
    val end: String,
    val allday: Boolean,
    val description: String
) {

}