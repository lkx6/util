package com.liukx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liukx on 2020/6/12 0012.
 */
@Configuration
public class AddSystemPropertyConfig {

    private static final Logger logger = LoggerFactory.getLogger(AddSystemPropertyConfig.class);

    /**
     * @description 考虑把一些公共的,默认的不变的配置加载到系统环境变量,配置到common,就不用再在每个项目的application.yml中配置了
     * 比如hibernate方言,由于MySQL5InnoDBDialect作者已经标注废弃,建议使用环境变量,但是需要环境变量在执行以下源码前加载好,
     * Use "hibernate.dialect.storage_engine=innodb" environment variable or JVM system property instead.
     * @see org.hibernate.dialect.MySQLDialect#MySQLDialect() 先从读取环境变量
     * 使用spring的@Configuration加static {}就可以保证优先加载
     * @author liukx
     * @date 2020/6/12 0012
     */
    static {
        logger.info("手动加载系统环境变量");
//           hibernate方言:核心源码
//           1 org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl.buildDialect
//           2 org.hibernate.dialect.MySQLDialect.MySQLDialect
//        System.setProperty("spring.jpa.properties.hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
        System.setProperty("hibernate.dialect.storage_engine","innodb");
    }

}
