package org.hufsdevelopers.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.CorsRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer




@SpringBootApplication
@EnableScheduling
class HUFSDevelopersAPIApplication

fun main(args: Array<String>) {
    runApplication<HUFSDevelopersAPIApplication>(*args)
}