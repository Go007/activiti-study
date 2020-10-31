package com.hong.activiti;

import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghong
 * @date 2020/10/30 23:32
 **/
public class BasicTest {

    @Test
    public void initActiviti() {
        ActivitiHelper.init();
    }

    @Test
    public void deployProcess() {
        String classPathResource = "bpmn/holiday.bpmn";
        ActivitiHelper.deployProcess(classPathResource);
    }

    @Test
    public void startProcessInstance() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "张三");
        variables.put("deptManager", "李四");
        variables.put("generalManager", "王五");
        String processKey = "holiday";
        String businessKey = "10";
        ActivitiHelper.startProcessInstance(processKey, businessKey, variables);
    }

    @Test
    public void queryTaskList() {
        String processKey = "holiday";
        String assignee = "zhangsan";
        ActivitiHelper.queryTaskList(processKey, assignee);
    }

    @Test
    public void queryProcessDefinition() {
        String processDefinitionKey = "holiday";
        ActivitiHelper.queryProcessDefinition(processDefinitionKey);
    }

    /**
     * 开启流程实例并初始化流程变量
     */
    @Test
    public void testProcessVariables() {
        ActivitiHelper.deployProcess("bpmn/holidayVariable.bpmn");

        //定义流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "张三");
        variables.put("deptManager", "李四");
        variables.put("generalManager", "王五");
        variables.put("personnel", "赵六");

        Holiday holiday = new Holiday();
        holiday.setId("1");
        holiday.setDay("5");
        //因为在bpmn图中我们使用了uel表达式的对象.属性的形式，所以我们可以直接传对象进去，但是需要注意的是业务类需要实现序列化
        variables.put("holiday", holiday);

        ActivitiHelper.startProcessInstance("holidayVariable", holiday.getId(), variables);
    }

    /**
     * 完成个人任务
     */
    @Test
    public void testFinishPersonalTask() {
        String processDefinitionKey = "holidayVariable";
        String assignee = "赵六";
        // 先查询出用户待办任务
        List<Task> taskList = ActivitiHelper.queryTaskList(processDefinitionKey, assignee);
        // 完成任务
        ActivitiHelper.completeTask(taskList);
    }

    /**
     * zhangsan 申请请假
     */
    @Test
    public void testCandidate() {
        String processKey = "holidayCondidateUsers";
        ActivitiHelper.deployProcess("bpmn/holidayCondidateUsers.bpmn", "请假流程申请(组任务相关)");
        ActivitiHelper.startProcessInstance(processKey);
        // zhangsan 申请请假，完成填写请假单节点任务
        ActivitiHelper.completeTask(ActivitiHelper.queryTaskList(processKey, "zhangsan"));
    }

    /**
     * 用户组（xiaowang,xiaozhao） 中 xiaowang 拾取任务
     */
    @Test
    public void testClaimTask() {
        String processKey = "holidayCondidateUsers";
        List<Task> taskList = ActivitiHelper.queryUnAssignedTask(processKey);
        // zhangsan 填写请假单节点 -> 部门经理审批节点（处理人：xiaowang,xiaozhao）
        // xiaowang 拾取任务, 分配任务负责人给到 xiaowang
        ActivitiHelper.claimTask(taskList, "xiaowang");
    }

    /**
     * xiaowang 放弃执行任务
     * 归还组任务，如果个人不想办理该组任务，可以归还组任务，归还后该用户不再是该任务的负责人，这样组内其他人才能拾取任务
     */
    @Test
    public void testRestitutionGroupTask() {
        String processKey = "holidayCondidateUsers";
        String userId = "xiaowang";
        // 获取 xiaowang 待办任务
        List<Task> taskList = ActivitiHelper.queryTaskList(processKey, userId);
        ActivitiHelper.restitutionGroupTask(processKey, userId, taskList);
    }

    /**
     * xiaowang 将任务转交给其他候选人来进行处理
     */
    @Test
    public void testTurnTaskToOtherCandidateUsers() {
        String processKey = "holidayCondidateUsers";
        String userId = "xiaowang";
        // 获取 xiaowang 待办任务
        List<Task> taskList = ActivitiHelper.queryTaskList(processKey, userId);
        // 将此任务交给其它候选人办理该 任务
        String candidateUser = "xiaozhao";
        ActivitiHelper.turnTaskToOtherCandidateUsers(processKey, userId, candidateUser, taskList);
    }

    ///////////////////////////////////流程监听器////////////////////////////////////////
    @Test
    public void testListener() {
        ActivitiHelper.deployProcess("bpmn/holidayListen.bpmn", "请假流程申请(Listen)");
    }

    @Test
    public void testListener1() {
        String processKey = "holidayListen";
        //流程启动的时候设置流程变量
        //定义流程变量
        Map<String, Object> variables = new HashMap<>();
        //设置流程变量 原理是因为startProcessInstanceByKey存在重载方法
        variables.put("employee", "张三");
        variables.put("generalManager", "王五");
        ActivitiHelper.startProcessInstance(processKey, "10", variables);
    }

}
