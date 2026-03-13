package com.gym.crm;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.scan("com.gym.crm");

        DispatcherServlet dispatcher = new DispatcherServlet(webContext);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        Wrapper servlet = Tomcat.addServlet(ctx, "dispatcher", dispatcher);
        servlet.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        tomcat.getServer().await();
    }
}

