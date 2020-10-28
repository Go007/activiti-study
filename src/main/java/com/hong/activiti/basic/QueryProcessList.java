package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * @Description: 用户查询待办任务
 * @Author wanghong
 * @Date 2020/10/28 17:06
 * @Version V1.0
 **/
public class QueryProcessList {

    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey("myProcess").taskAssignee("zhangsan").list();
        for (Task task:taskList){
            System.out.println("流程实例ID:" + task.getProcessDefinitionId());
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("任务名称:" + task.getName());
        }
    }
}
