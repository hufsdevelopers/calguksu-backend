package org.hufsdevelopers.api.service

import org.hufsdevelopers.api.data.DateHolder
import org.hufsdevelopers.api.domain.Event
import org.hufsdevelopers.api.repository.CalendarRepository
import org.hufsdevelopers.api.repository.EventRepository
import org.jsoup.Jsoup
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class CalendarService(private final val calendarRepository: CalendarRepository, val eventRepository: EventRepository) {
    companion object {
        const val URL_CALENDAR_HUFS_AC_KR = "https://www.hufs.ac.kr/user/indexSub.action?codyMenuSeq=37069&siteId=hufs"
    }

    val hufsofficialCalendar = calendarRepository.getReferenceById(1)

    @Scheduled(fixedRate = 10000)
    fun getUpdates() {
        val wholeSource = Jsoup.connect(URL_CALENDAR_HUFS_AC_KR).get()

        // 캘린더 우측에서 각 이벤트 선택
        val sourceFetchedEvents = mutableListOf<Event>()

        wholeSource.getElementsByClass("sch02_box").forEach { calendar ->
            // 캘린더 좌측에서 캘린더 제목 선택 ex - "2022.00"
            val calendarName = calendar.getElementsByClass("calendar3").first()
                ?.getElementsByTag("strong")?.first()?.text() ?: return@forEach

            // 캘린더 제목에서 year, month 추출 ex - (2022, 00)
            val calendarDate: Pair<Int, Int> = calendarName.split('.').let {
                it[0].toInt() to it[1].toInt()
            }

            calendar.getElementsByClass("calendar4").first()?.getElementsByTag("tr")?.forEach {
                val eventDates = it.firstElementChild()?.text()?.split("~") ?: return@forEach
                val description = it.getElementsByTag("span").first()?.text() ?: return@forEach
                if (description.isBlank()) return@forEach

                val startDate: DateHolder = eventDates[0].split(".").let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second >= month) calendarDate.first else calendarDate.first - 1
                    DateHolder(year, month, day)
                }

                val endDate: DateHolder? = eventDates.getOrNull(1)?.split(".")?.let { date ->
                    val month = date[0].toInt()
                    val day = date[1].toInt()
                    val year = if (calendarDate.second <= month) calendarDate.first else calendarDate.first + 1
                    DateHolder(year, month, day)
                }

                sourceFetchedEvents.add(
                    Event(
                        calendar = hufsofficialCalendar,
                        startYear = startDate.year,
                        startMonth = startDate.month,
                        startDay = startDate.day,
                        endYear = endDate?.year ?: startDate.year,
                        endMonth = endDate?.month ?: startDate.month,
                        endDay = endDate?.day ?: startDate.day,
                        description = description
                    )
                )
            } ?: return@forEach
        }

        val localFetchedEvents = eventRepository.findByCalendar(hufsofficialCalendar).toMutableList()
        sourceFetchedEvents.forEach { event ->
            if (!localFetchedEvents.remove(event)) {
                eventRepository.save(event)
                System.out.println("event saved ${event}")
            }
        }

        eventRepository.deleteAll(localFetchedEvents)
        System.out.println("${localFetchedEvents.size} events removed")
    }
}