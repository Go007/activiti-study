package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

/**
 * @Description: 初始化Activiti数据库
 * @Author wanghong
 * @Date 2020/10/28 10:52
 * @Version V1.0
 **/
public class InitActiviti {
    public static void main(String[] args) {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine processEngine = configuration.buildProcessEngine(); //此时会创建数据库
        System.out.println(processEngine);
    }
}
