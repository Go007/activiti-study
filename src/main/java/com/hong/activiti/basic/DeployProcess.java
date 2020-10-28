package com.hong.activiti.basic;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;

/**
 * @Description: 部署流程
 * @Author wanghong
 * @Date 2020/10/28 11:22
 * @Version V1.0
 **/
public class DeployProcess {

    public static void main(String[] args) {
        // 流程定义部署
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addClasspathResource("bpmn/holiday.bpmn");
        Deployment deployment = deploymentBuilder.deploy();
        System.out.println(deployment.getId() + "====" + deployment.getName());

    }
}
