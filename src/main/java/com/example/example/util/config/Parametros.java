package com.example.example.util.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

@Component
@ConfigurationProperties("reports")
@Getter
@Setter
public class Parametros {


    private String reportPath;

    @Autowired
    ServletContext context;

    public Parametros()
    {}

    @PostConstruct
    public void init()
    {
        System.out.println("Path report "+reportPath);
    }
}
