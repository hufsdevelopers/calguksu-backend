package org.hufsdevelopers.calguksu.exceptions

class EmailSendTimeLimitationException(remainingSecond: Int) :
    HttpReponseExcetion(403, "${remainingSecond}초 뒤에 다시 시도할 수 있습니다.") {
}