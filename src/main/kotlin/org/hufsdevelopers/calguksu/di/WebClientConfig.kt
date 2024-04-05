package org.hufsdevelopers.calguksu.di

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
internal class WebClientConfig {
    @Bean
    @Qualifier("hufsWebClient")
    fun hufsWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://hufs.ac.kr")
            .build()
    }
}