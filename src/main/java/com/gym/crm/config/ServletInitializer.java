package com.gym.crm.config;

import com.gym.crm.config.logging.TransactionIdFilter;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.Filter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    @NonNull
    protected Filter[] getServletFilters() {
        return new Filter[]{new TransactionIdFilter()};
    }
}
