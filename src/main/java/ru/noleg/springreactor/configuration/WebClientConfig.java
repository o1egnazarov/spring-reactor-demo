package ru.noleg.springreactor.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    public static final String URL_JSON = "https://jsonplaceholder.typicode.com";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(URL_JSON)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(5))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                ))
                .build();
    }
}
