package org.hufsdevelopers.calguksu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class HUFSDevelopersAPIApplication

fun main(args: Array<String>) {
    runApplication<HUFSDevelopersAPIApplication>(*args)
}