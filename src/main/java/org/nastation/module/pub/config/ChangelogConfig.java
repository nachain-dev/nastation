package org.nastation.module.pub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@PropertySource("classpath:changelog.properties")
@ConfigurationProperties(prefix = "log")
public class ChangelogConfig {

    private List<String> list = new ArrayList<>();

}
