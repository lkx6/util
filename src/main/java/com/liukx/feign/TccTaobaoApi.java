package com.liukx.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 在Application上 @EnableFeignClients(basePackages = {"com.tembin.loms.web.api"})
 * Created by liukx on 2020/6/22 0022.
 * value 必须定义,如果是自定义的链接调用,可以定义一个不存在的,就会使用url
 */
@FeignClient(value = "test123456",url = "https://tcc.taobao.com")
public interface TccTaobaoApi {

//    https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15850781443

    @GetMapping("cc/json/mobile_tel_segment.htm")
    String mobile_tel_segment(@RequestParam("tel") String tel);

}
