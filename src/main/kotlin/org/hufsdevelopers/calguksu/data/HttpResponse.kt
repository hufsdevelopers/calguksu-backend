package org.hufsdevelopers.calguksu.data

data class HttpResponse<T>(val successful: Boolean, val result: T) {
}