package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;

/**
 * @Description: 挂起或激活流程实例,当流程挂起的时候再去执行流程会抛出异常
 *  org.activiti.engine.ActivitiException: Cannot complete a suspend task
 * @Author wanghong
 * @Date 2020/10/28 18:30
 * @Version V1.0
 **/
public class SuspendOrActivateProcessInstance {

    public static void main(String[] args) {
        // 挂起全部流程,通过流程定义的唯一key操作
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processDifinitionKey = "myProcess";
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDifinitionKey).singleResult();
        boolean suspend = processDefinition.isSuspended();
        if (suspend){
            // 如果暂停则激活


        }

    }
}
