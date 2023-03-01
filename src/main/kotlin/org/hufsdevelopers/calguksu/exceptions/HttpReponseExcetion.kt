package org.hufsdevelopers.calguksu.exceptions

open class HttpReponseExcetion(val httpErrorCode: Int, override val message: String) : Exception()