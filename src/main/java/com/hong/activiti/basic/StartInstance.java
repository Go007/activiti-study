package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

/**
 * @Description: 开启流程实例
 * @Author wanghong
 * @Date 2020/10/28 16:43
 * @Version V1.0
 **/
public class StartInstance {

    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday");
        System.out.println(processInstance.getId() + "->" + processInstance.getDeploymentId() + "->" + processInstance.getActivityId());
    }
}
