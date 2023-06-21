package org.nastation.data.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author John | NaChain
 * @since 12/27/2021 17:26
 */
@Component
@Data
public class AppConfig {

    @Value("${app.api.cluster.node.url}")
    private String nodeClusterUrl;

    @Value("${app.api.cluster.datacenter.url}")
    private String dataCenterUrl;

    @Value("${app.api.service.url}")
    private String serviceUrl;

    @Value("${app.api.nascan.url}")
    private String nascanUrl;

    @Value("${app.api.trace.url}")
    private String traceUrl;

    @Value("${app.project.name}")
    private String projectName;

    @Value("${app.project.version}")
    private String projectVersion;

    @Value("${app.project.versionNumber}")
    private String projectVersionNumber;

    @Value("${app.project.websiteUrl}")
    private String websiteUrl;

    @Value("${app.project.appDownloadUrl}")
    private String appDownloadUrl;

    @Value("${app.project.changeLogUrl}")
    private String changeLogUrl;

    @Value("${app.project.whitePaperUrl}")
    private String whitePaperUrl;

    @Value("${app.project.telegramUrl}")
    private String telegramUrl;

    @Value("${app.project.twitterUrl}")
    private String twitterUrl;



}
