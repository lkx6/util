package com.liukx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 用法 在枚举类上(字段/get方法)添加
 * @JsonSerialize(using = EnumSerializer.class)
 * spring 默认使用的是jackson,避免每次需要枚举中文名的时候,都在再添加一个get方法
 * Created by liukx on 2020/4/25 0025.
 */
public class EnumSerializer extends JsonSerializer<Enum> {

    public static final List<String> ignoreList = Arrays.asList("class","declaringClass");

    @Override
    public void serialize(Enum anEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        BeanWrapper src = new BeanWrapperImpl(anEnum);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("enumName");
        jsonGenerator.writeString(anEnum.name());
        for (PropertyDescriptor pd : pds) {
            String key = pd.getName();
            if(ignoreList.contains(key)){
                continue;
            }
            Object value = src.getPropertyValue(key);
            jsonGenerator.writeFieldName(key);
            jsonGenerator.writeObject(value);
        }

        jsonGenerator.writeEndObject();
    }
}
