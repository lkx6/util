package com.liukx.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by liukx on 2020/6/3 0003.
 */
public interface EnumService {

    List<JSONObject> listEnums(String enumType,String fullEnumType);

}
