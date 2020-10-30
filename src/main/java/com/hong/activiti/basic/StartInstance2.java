package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 开启流程实例
 * @Author wanghong
 * @Date 2020/10/28 16:43
 * @Version V1.0
 **/
public class StartInstance2 {

    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 流程启动的时候设置流程变量
        Map<String,Object> variables = new HashMap<>();
        variables.put("employee","张三");
        variables.put("deptManager","李四");
        variables.put("generalManager","王五");
        String processKey = "holidayUEL";
        String businessKey = "10";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey,businessKey,variables);
        System.out.println(processInstance.getId()  + "->" + processInstance.getDeploymentId() + "->" + processInstance.getActivityId());
    }
}
