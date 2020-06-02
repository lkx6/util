package com.liukx.service.impl;

import com.liukx.service.SerialNumberService;
import org.apache.commons.lang3.RandomUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by liukx on 2020/3/18 0018.
 */
@Component
public class RedisSerialNumberServiceImpl implements SerialNumberService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @description 基于redis生成不重复的流水号,参照
     * @param prefix 前缀,业务类型,保存到redis的key,可以为空
     * @param dateStr 日期格式:20200309
     * @author liukx
     * @date 2020/3/6 0006
     */
    @Override
    public String getSystemSerialNumber(String prefix, String dateStr, int suffixLength) {
        String key = prefix+dateStr;
        if(! stringRedisTemplate.hasKey(key)){
            if(suffixLength<2){
                throw new ServiceException("suffixLength不能小于2");
            }
            int bEnd = BigDecimal.valueOf(Math.pow(10,suffixLength)).intValue();

            long a = Long.parseLong(dateStr)*(bEnd);
            int b = RandomUtils.nextInt(bEnd/10,bEnd)/3;
            stringRedisTemplate.opsForValue().increment(key,a+b);
            stringRedisTemplate.expire(key,1, TimeUnit.DAYS);
        }
        return stringRedisTemplate.opsForValue().increment(key,1)+"";
    }
}
