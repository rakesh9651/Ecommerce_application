package com.ecommerce.OrderService.config;

import com.ecommerce.OrderService.external.decoder.CustomErrorDecoder;
import com.ecommerce.OrderService.external.response.ErrorResponse;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
