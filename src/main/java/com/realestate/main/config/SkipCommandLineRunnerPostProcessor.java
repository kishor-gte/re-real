package com.realestate.main.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

@Configuration
public class SkipCommandLineRunnerPostProcessor {

    @Bean
    public static BeanFactoryPostProcessor skipCommandLineRunners(ConfigurableEnvironment env) {
        return beanFactory -> {
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                if (beanFactory instanceof ConfigurableListableBeanFactory) {
                    ConfigurableListableBeanFactory cfb = (ConfigurableListableBeanFactory) beanFactory;
                    String[] names = cfb.getBeanNamesForType(CommandLineRunner.class, true, false);
                    for (String n : names) {
                        if (cfb.containsBeanDefinition(n)) {
                            if (cfb instanceof org.springframework.beans.factory.support.DefaultListableBeanFactory) {
                                org.springframework.beans.factory.support.DefaultListableBeanFactory dlbf = (org.springframework.beans.factory.support.DefaultListableBeanFactory) cfb;
                                dlbf.removeBeanDefinition(n);
                            }
                        }
                    }
                }
            }
        };
    }
}
