package com.tampan.common.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Date;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket service() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(servicePaths())
                .build()
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(Date.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET,
                        newArrayList(new ResponseMessageBuilder()
                                .code(500)
                                .message("We encountered error processing  your request. Please try again")
                                .responseModel(new ModelRef("Error"))
                                .build()))
                .apiInfo(apiInfo())
                ;

    }

    private Predicate<String> servicePaths() {
        return and(
                not(regex("/health.*")),
                not(regex("/error.*")),
                not(regex("/autoconfig.*")),
                not(regex("/beans.*")),
                not(regex("/configprops.*")),
                not(regex("/dump.*")),
                not(regex("/info.*")),
                not(regex("/mappings.*")),
                not(regex("/trace.*")),
                not(regex("/env.*")),
                not(regex("/auditevents.*")),
                not(regex("/flyway.*")),
                not(regex("/heapdump.*")),
                not(regex("/loggers.*")),
                not(regex("/restart.*")),
                not(regex("/pause.*")),
                not(regex("/refresh.*")),
                not(regex("/resume.*")),
                not(regex("/features.*")),
                not(regex("/logfile.*")),
                not(regex("/metrics.*"))
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .build();
    }
}
