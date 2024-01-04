package com.ou.journal.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@PropertySources({
        @PropertySource(value = "classpath:countries.properties", encoding = "UTF-8")
})
@ConfigurationProperties("")
public class CountryProperties {
    private Map<String, String> countries = new HashMap<>();
}
