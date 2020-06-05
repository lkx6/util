package com.liukx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ配置
 *
 * @author liukx
 * @date 2019/04/26
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);


    /** 消息交换机的名字*/
    public static final String UPLOAD_CREDIT_INFO = "upload_credit_info";
    /** 同步客户授信资料队列*/
    public static final String UPDATE_CREDIT_INFO = "update_credit_info";
    /** 扫描富有请求队列*/
    public static final String SCAN_FUIOU_REQUEST = "scan_fuiou_request";

    public static final String CRM_CONTRACT = CrmConfig.CRM_CONTRACT_MQ;

    public static final String CRM_CUSTOMER = CrmConfig.CRM_CUSTOMER_MQ;

    public static final String CRM_CONTACT = CrmConfig.CRM_CONTACT_MQ;
    /** 请求amazon报表的队列 */
    public static final String REQUEST_AMAZON_REPORT = "request_amazon_report";
    /** 下载amazon报表的队列 */
    public static final String DOWNLOAD_AMAZON_REPORT = "download_amazon_report";

    /**
     * 更新轨迹追踪明细
     */
    public static final String UPDATE_TRACKING_DETAIL = "update_tracking_detail";

    /**
     * 更新运单状态
     */
    public static final String UPDATE_WAYBILL_STATUS = "update_waybill_status";

    /**
     * 更新运单状态
     */
    public static final String UPDATE_AMAZON_ACCOUNT_ROW = "update_amazon_account_row";

    /**
     * 导出借款资料包
     */
    public static final String EXPORT_LOAN_INFORMATION_KIT = "export_loan_information_kit";

    /**
     * 配置消息交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routing_key分发到指定队列
     TopicExchange:多关键字匹配
     */

    private static final List<String> queueNameList = new ArrayList<>();

    static {
        try {
            //获取当前类声明的队列
            for (Field field : RabbitMQConfig.class.getDeclaredFields()) {
                if(field.toString().startsWith("public static final java.lang.String")){
                    queueNameList.add((String) field.get(field.getName()));
                }
            }
        }catch (Exception e){
            logger.error("初始化RabbitMQConfig失败",e);
        }

    }

    /**  第一步
     * @description 默认queue,注入到容器中才会创建
     * @author liukx
     * @date 2020/6/5 0005
     */
    @Bean
    public List<Queue> defaultQueueList() {
        List<Queue> queueList = new ArrayList<>();
        for (String queueName : queueNameList) {
            queueList.add(new Queue(queueName,true));
        }
        return queueList;
    }

    /** 第二步
     * @description 默认创建直连交换机,注入到容器中才会创建
     * @author liukx
     * @date 2020/6/5 0005
     */
    @Bean
    public List<Exchange> defaultExchangeList(){
        List<Exchange> exchangeList = new ArrayList<>();

        for (String queueName : queueNameList) {
            exchangeList.add(new DirectExchange(queueName,true,false));
        }
        return exchangeList;
    }

    /**
     * @description 下面四个为样例,会在方法中
     * com.tembin.credit.config.RabbitMQConfig#bindingList(java.util.List, java.util.List, java.util.List, java.util.List)
     * 注入到otherQueueList,otherExchangeList
     * @author liukx
     * @date 2020/6/5 0005
     */
//    @Bean
//    public Queue diyQueue1() {
//        return new Queue("diyQueue1",true);
//    }
//
//    @Bean
//    public Queue diyQueue2() {
//        return new Queue("diyQueue2",true);
//    }
//
//    @Bean
//    public Exchange diyDirectExchange1() {
//        return new DirectExchange("diyQueue1",true,false);
//    }
//
//    @Bean
//    public Exchange diyDirectExchange2() {
//        return new DirectExchange("diyQueue2",true,false);
//    }


  /** 第三步
   * @description queue与交换机绑定,绑定方式,名字一样的绑定在一起
   * spring支持多注入
   * @author liukx
   * @date 2020/6/5 0005
   */
    @Bean
    public List<Binding> bindingList(@Qualifier("defaultQueueList") List<Queue> defaultQueueList,
                                     @Qualifier("defaultExchangeList") List<Exchange> defaultExchangeList,
                                     List<Queue> otherQueueList,
                                     List<Exchange> otherExchangeList) {

        Map<String,Queue> queueMap = new HashMap();
        for (Queue queue : defaultQueueList) {
            queueMap.put(queue.getName(),queue);
        }
        for (Queue queue : otherQueueList) {
            queueMap.put(queue.getName(),queue);
        }

        Map<String,Exchange> directExchangeMap = new HashMap();
        for (Exchange exchange : defaultExchangeList) {
            directExchangeMap.put(exchange.getName(),exchange);
        }
        for (Exchange exchange : otherExchangeList) {
            directExchangeMap.put(exchange.getName(),exchange);
        }

        List<Binding> bindingList = new ArrayList<>();
        for (String key : queueMap.keySet()) {
            Exchange exchange = directExchangeMap.get(key);
            if(exchange instanceof DirectExchange){
                //绑定直连交换机
                Binding binding = BindingBuilder.bind(queueMap.get(key)).to((DirectExchange)exchange).withQueueName();
                bindingList.add(binding);
            }
            //绑定其他交换机请加else if

        }
        return bindingList;
    }
}
