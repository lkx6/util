package com.liukx.web.api;

import com.alibaba.fastjson.JSONObject;
import com.liukx.service.EnumService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by liukx on 2020/6/3 0003.
 */
@RestController("commonCommonApi")
@RequestMapping("/api/common")
public class CommonApi {

    @Autowired
    private EnumService enumService;

    @GetMapping("list-enums")
    @ApiOperation("放在common模块,所有的应用都会有这个接口,优先匹配enumType(枚举名称),如果enumType为空,则匹配fullEnumType(枚举全路径)")
    public List<JSONObject> listEnums(String enumType,String fullEnumType){
        return enumService.listEnums(enumType, fullEnumType);
    }

}
