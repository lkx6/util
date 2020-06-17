package com.liukx.swagger;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LoadIfProdProfileCondition implements ConfigurationCondition {

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.PARSE_CONFIGURATION;
    }

    /**
     * @description 生产环境才加载注入
     * @author liukx
     * @date 2019/12/27 0027
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        for(String profile:activeProfiles){
            if(profile.equalsIgnoreCase("prod")){
                return true;
            }
        }
        return false;
    }
}
