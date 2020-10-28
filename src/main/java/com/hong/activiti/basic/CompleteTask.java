package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;

/**
 * @Description: 处理待办任务
 * @Author wanghong
 * @Date 2020/10/28 17:12
 * @Version V1.0
 **/
public class CompleteTask {

    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String taskId = "7502";
        taskService.complete(taskId);
    }
}
