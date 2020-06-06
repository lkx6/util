package com.liukx.web;

import com.tembin.common.config.ConstantConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 通用的web端数据转换
 * 使用ConditionalOnMissingBean原因:目前各个项目中都拷贝了一个ConversionServiceConfig.class,里面使用了对前端不太友好的时间字符串(yyyy-MM-dd HH:mm:ss,SSS X),
 * 加上是为了做兼容,不报错,建议删除具体项目中的ConversionServiceConfig.class
 */
@ConditionalOnMissingBean(name = "conversionServiceConfig")
@Configuration
public class CommonConversionServiceConfig {

    /**
     * @description 暂时没有使用
     * @author liukx
     * @date 2020/6/6 0006
     */
//    @Bean
//    public FormattingConversionServiceFactoryBean formattingConversionService(){
//        FormattingConversionServiceFactoryBean bean = new FormattingConversionServiceFactoryBean();
//        return bean;
//    }

    /**
     * @description Jackson2ObjectMapperBuilder用途:用户格式化接口返回的对象包含的date
     * @see org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration.MappingJackson2HttpMessageConverterConfiguration#mappingJackson2HttpMessageConverter(com.fasterxml.jackson.databind.ObjectMapper)
     * @see JacksonAutoConfiguration.JacksonObjectMapperConfiguration#jacksonObjectMapper(Jackson2ObjectMapperBuilder)
     * @see JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration#jacksonObjectMapperBuilder(java.util.List)
     * @author liukx
     * @date 2020/6/6 0006
     */
    @Bean
    public Jackson2ObjectMapperBuilder dateFormatBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat(ConstantConfig.DATETIME_FORMAT)).timeZone("GMT+8");
        return b;
    }

    /**
     * @description 用途:注入到容器,用于将前端传入的date string 转换为时间
     * @see FormattingConversionService.ParserConverter#convert(Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
     *  该方法处理调用 result = this.parser.parse(text, LocaleContextHolder.getLocale());
     * @author liukx
     * @date 2020/6/6 0006
     */
    @Bean
    public Formatter<Date> dateFormatter() {
        return new Formatter<Date>() {
            @Override
            public String print(Date date, Locale locale) {
                return DateFormatUtils.format(date,ConstantConfig.DATE_FORMAT,locale);
            }

            @Override
            public Date parse(String text, Locale locale) throws ParseException {
                return DateUtils.parseDate(text,locale,ConstantConfig.DATE_FORMAT,ConstantConfig.DATETIME_FORMAT);
            }

        };
    }


}
