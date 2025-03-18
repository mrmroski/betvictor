package com.recruitment.task.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.variables")
public class LoripsumProperties {
    private String host;
    private String type;
    private String paras;
}
