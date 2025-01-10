    package com.wearltnow.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.support.ResourceBundleMessageSource;
    import org.springframework.web.servlet.LocaleResolver;
    import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

    import java.util.Locale;

    @Configuration
    public class MessageConfig {
        @Bean
        public LocaleResolver localeResolver() {
            AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
            localeResolver.setDefaultLocale(Locale.forLanguageTag("vi"));
            return localeResolver;
        }

        @Bean
        public ResourceBundleMessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("messages/message");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        }

        @Bean
        public ResourceBundleMessageSource attributeSource() {
            ResourceBundleMessageSource attributeSource = new ResourceBundleMessageSource();
            attributeSource.setBasename("messages/attributes");
            attributeSource.setDefaultEncoding("UTF-8");
            return attributeSource;
        }
    }

