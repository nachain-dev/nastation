package org.nastation.web.config;


import org.nastation.data.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("rest-api")
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/station/api/**"))
                .build()
                .pathMapping("/")
                ;
    }

    private ApiInfo apiInfo() {
        String projectName = appConfig.getProjectName();
        String projectVersion = appConfig.getProjectVersion();
        String websiteUrl = appConfig.getWebsiteUrl();

        return new ApiInfoBuilder()
                .title(projectName + " REST API Document")
                .description(projectName + " REST API Document")
                //.termsOfServiceUrl(projectName)
                .contact(new Contact(projectName, websiteUrl, "support@nachain.org"))
                .version(projectVersion)
                .build();
    }

}