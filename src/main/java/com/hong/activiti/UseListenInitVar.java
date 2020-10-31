package com.hong.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 通过监听器来初始化部门经理审批的流程变量
 *
 * @author wanghong
 * @date 2020/10/31 15:08
 **/
public class UseListenInitVar implements TaskListener {
    /**
     * 任务监听器是发生对应的任务相关事件时执行自定义 java 逻辑 或表达式。
     * 任务相当事件包括
     * create：任务创建后触发
     * assignment：任务分配后触发
     * complete：任务完成后触发
     * all：所有事件发生都触发
     */
    public void notify(DelegateTask delegateTask) {
        System.out.println("监听器执行...");
        delegateTask.setAssignee("李四");
    }
}
