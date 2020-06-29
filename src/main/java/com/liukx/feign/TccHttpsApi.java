package com.liukx.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * version2
 * 在Application上 @EnableFeignClients(basePackages = {"com.tembin.loms.web.api"})
 * Created by liukx on 2020/6/22 0022.
 * value 必须定义,如果是自定义的链接调用,可以定义一个不存在的,就会使用url,url为了简化,url可以只定义为http协议头(https://)(http://),就可以和下面的链接拼接在一起了,可以把不同域名调用的接口都放在一起
 */
@org.springframework.cloud.openfeign.FeignClient(value = "test123456",url = "https://")
public interface TccHttpsApi {

//    https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15850781443

    @GetMapping("tcc.taobao.com/cc/json/mobile_tel_segment.htm")
    String mobile_tel_segment(@RequestParam("tel") String tel);

    /**
     * @description 支持PathVariable
     * @author liukx
     * @date 2020/6/29 0029
     */
    @GetMapping("tcc.taobao.com/cc/json/mobile_tel_segment{test}")
    String mobile_tel_segment11(@PathVariable("test") String test,
                                @RequestParam("tel") String tel);
}
