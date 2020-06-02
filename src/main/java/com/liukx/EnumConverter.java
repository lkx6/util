package com.liukx;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的枚举类转换器
 * Created by liukx on 2020/2/28 0024.
 */
@Converter
public class EnumConverter implements AttributeConverter<Enum,String> {

    private static final Map<String,Enum> map = new HashMap<>();

    static {
        putEnumValues(Education.values());
        //...
    }

    private static void putEnumValues(Enum[] values){
        for (Enum value : values) {
            String key = value.name();
            if(map.containsKey(key)){
                //不同枚举的名字不能相同
                throw new ServiceException("枚举类型:'"+key+"'已存在");
            }
            map.put(key,value);
        }
    }

    @Override
    public String convertToDatabaseColumn(Enum anEnum) {
        if(anEnum == null){
            return null;
        }
        return anEnum.name();
    }

    @Override
    public Enum convertToEntityAttribute(String s) {
        if(StringUtils.isEmpty(s)){
            return null;
        }
        return map.get(s);
    }

}
