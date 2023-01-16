package org.hufsdevelopers.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class HUFSDevelopersAPIApplication

fun main(args: Array<String>) {
    runApplication<HUFSDevelopersAPIApplication>(*args)
}
