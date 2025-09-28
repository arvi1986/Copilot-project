package ind.arvind.config;

import ind.arvind.filter.CorrelationIdFilter;
import ind.arvind.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final CorrelationIdFilter correlationIdFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthorizationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthorizationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthorizationFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration() {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(correlationIdFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }
}

