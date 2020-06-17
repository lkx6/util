package com.liukx.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by liukx on 2020/5/29 0029.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "partners") //只能从spring environment加载,不能和@PropertySource配合从指定yml文件加载bean
public class PartnersConfig {

    /**
     * @description 可以通过PropertySourcePlaceholderConfigurer来加载yml文件，暴露yml文件到spring environment
     * 需要静态方法
     * @author liukx
     * @date 2020/5/29 0029
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer logisticsCompanyProperties(Environment environment) {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();

        //通过环境来匹配
        SpringProfileDocumentMatcher matcher = new SpringProfileDocumentMatcher();
        matcher.addActiveProfiles(environment.getActiveProfiles());
        yaml.setDocumentMatchers(matcher);

        yaml.setResources(new ClassPathResource("partners.yml"));
        configurer.setProperties(yaml.getObject());
        return configurer;
    }

    private List<FundProvider> fundProviders = new ArrayList<>();

    private List<LogisticsCompany> logisticsCompanies = new ArrayList<>();

    private List<InsuranceCompany> insuranceCompanies = new ArrayList<>();


    /**
     * 参考文档
     * https://stackoverflow.com/questions/47717871/spring-boot-profiles-ignored-in-propertysourcesplaceholderconfigurer-loaded-fil
     * https://stackoverflow.com/questions/33525951/spring-yaml-profile-configuration/37147351?r=SearchResults#37147351
     * Created by liukx on 2020/6/6 0006.
     */
    private static class SpringProfileDocumentMatcher implements YamlProcessor.DocumentMatcher {

        private List<String> activeProfiles;

        @Override
        public YamlProcessor.MatchStatus matches(Properties properties) {
            String profile = properties.getProperty("spring.profiles");
            if(StringUtils.isEmpty(profile) || activeProfiles.contains(profile)){
                return YamlProcessor.MatchStatus.FOUND;
            }
            return YamlProcessor.MatchStatus.NOT_FOUND;
        }


        public void addActiveProfiles(String[] activeProfiles) {
            this.activeProfiles = Arrays.asList(activeProfiles);
        }
    }

}