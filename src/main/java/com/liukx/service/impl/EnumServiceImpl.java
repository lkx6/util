package com.liukx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liukx.AppUtil;
import com.liukx.ServiceException;
import com.liukx.service.EnumService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by liukx on 2020/6/3 0003.
 */
@Service
public class EnumServiceImpl implements EnumService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(EnumServiceImpl.class);

    private static final Map<String,Class> enumMap = new HashMap<>();

    /**
     * @description 通用的匹配fullEnumType
     * @author liukx
     * @date 2020/6/3 0003
     */
    @Override
    public List<JSONObject> listEnums(String enumType,String fullEnumType) {
        Class enumClass;
        if(StringUtils.isNotEmpty(enumType)){
            enumClass = enumMap.get(enumType);
        }else{
            enumClass = enumMap.get(fullEnumType);
        }

        if(enumClass == null){
            throw new ServiceException("请传入类型/类型不存在!");
        }

        try {
            Method method = enumClass.getDeclaredMethod("values");
            Enum[] enumArray = (Enum[]) method.invoke(null);
            return AppUtil.convertEnumsToJsonList(enumArray);
        } catch (Exception e) {
            logger.error("获取枚举遇到未知异常,enumType:{},fullEnumType:{},enumClass:{}",enumType,fullEnumType,enumClass,e);
            throw new ServiceException("未知异常");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("加载枚举类...");
        Field field=ClassLoader.class.getDeclaredField("classes");
        field.setAccessible(true);
        Vector<Class> classes=(Vector<Class>) field.get(ClassLoader.getSystemClassLoader());
        for (Class aClass : classes) {
            if(aClass.getName().startsWith("com.tembin") && aClass.isEnum()){
                enumMap.put(aClass.getName(),aClass);
                enumMap.put(aClass.getSimpleName(),aClass);
            }
        }
    }
}
