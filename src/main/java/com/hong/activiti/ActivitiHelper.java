package com.hong.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author wanghong
 * @date 2020/10/30 23:18
 **/
public class ActivitiHelper {

    /**
     * 初始化Activiti数据库
     *
     * @return
     */
    public static ProcessEngine init() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine processEngine = configuration.buildProcessEngine(); //此时会创建数据库
        return processEngine;
    }

    /**
     * 流程定义部署
     *
     * @param classpathResource
     * @return
     */
    public static Deployment deployProcess(String classpathResource) {
        return deployProcess(classpathResource, null);
    }

    public static void deployProcess2(String classpathResource, String name) {
        //  流程制作出来后要上传到服务器 zip文件更便于上传
        //1.创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.转化出ZipInputStream流对象
        InputStream is = ActivitiHelper.class.getClassLoader().getResourceAsStream("diagram/holidayBPMN.zip");
        //将 inputStream流转化为ZipInputStream流
        ZipInputStream zipInputStream = new ZipInputStream(is);
        //3.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name(name)
                .deploy();
        //4.输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    public static Deployment deployProcess(String classpathResource, String name) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addClasspathResource(classpathResource).name(name);
        Deployment deployment = deploymentBuilder.deploy();
        System.out.println(deployment.getId() + "====" + deployment.getName());
        return deployment;
    }

    public static ProcessInstance startProcessInstance(String processKey) {
        return startProcessInstance(processKey, null, null);
    }

    /**
     * 开启流程实例
     *
     * @param processKey  流程定义key
     * @param businessKey 业务主键
     * @param variables   流程变量
     * @return
     */
    public static ProcessInstance startProcessInstance(String processKey, String businessKey, Map<String, Object> variables) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 流程启动的时候设置流程变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey, variables);
        System.out.println(processInstance.getId() + "->" + processInstance.getDeploymentId() + "->" + processInstance.getActivityId());
        return processInstance;
    }

    /**
     * @param processKey
     * @return
     */
    private static TaskQuery buildTaskQuery(String processKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processKey);
        return taskQuery;
    }

    private static TaskQuery buildTaskQueryByTaskId(String taskId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery().taskId(taskId);
        return taskQuery;
    }

    /**
     * 查询 用户或用户组任务
     *
     * @param processKey
     * @param assignee      任务处理人
     * @param candidateUser 任务候选人
     * @return
     */
    private static List<Task> queryTaskList(String processKey, String assignee, String candidateUser) {
        TaskQuery taskQuery = buildTaskQuery(processKey);
        List<Task> taskList = null;
        if (StringUtils.isNotEmpty(assignee)) {
            taskList = taskQuery.taskAssignee(assignee).list();
        } else {
            taskList = taskQuery.taskCandidateUser(candidateUser).list();
        }
        for (Task task : taskList) {
            System.out.println("流程实例ID:" + task.getProcessDefinitionId());
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("任务名称:" + task.getName());
        }

        return taskList;
    }

    /**
     * 查询流程实例无处理人（或有多个处理人但还未分配）的任务
     *
     * @param processKey
     * @return
     */
    public static List<Task> queryUnAssignedTask(String processKey) {
        return buildTaskQuery(processKey).taskUnassigned().list();
    }

    /**
     * 查询用户组任务，注意任务负责人是null，代表当前无处理人，需要先进行拾取操作
     *
     * @param processKey
     * @param candidateUser
     * @return
     */
    public static List<Task> queryUsersGroupTaskList(String processKey, String candidateUser) {
        return queryTaskList(processKey, null, candidateUser);
    }

    /**
     * 查询用户待办任务
     *
     * @param processKey
     * @param assignee
     * @return
     */
    public static List<Task> queryTaskList(String processKey, String assignee) {
        return queryTaskList(processKey, assignee, null);
    }

    /**
     * 处理待办任务
     *
     * @param taskList 通过 queryTaskList 查询出来
     */
    public static void completeTask(List<Task> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskList.stream().forEach(task -> taskService.complete(task.getId()));
    }

    /**
     * 流程定义查询,比如请假流程被定义了一次，但是部署了多次
     *
     * @return
     */
    public static List<ProcessDefinition> queryProcessDefinition(String processDefinitionKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 查询流程定义
        org.activiti.engine.repository.ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //遍历查询结果
        List<ProcessDefinition> list = processDefinitionQuery.processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println("------------------------");
            System.out.println(" 流 程 部 署 id ： " + processDefinition.getDeploymentId());
            System.out.println("流程定义id：" + processDefinition.getId());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义key：" + processDefinition.getKey());
            System.out.println("流程定义版本：" + processDefinition.getVersion());
        }
        return list;
    }

    /**
     * 查询流程的运行状态，当前运行节点等信息
     *
     * @param processDefinitionKey
     * @return
     */
    public static List<ProcessInstance> queryProcessInstance(String processDefinitionKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
        for (ProcessInstance processInstance : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + processInstance.getProcessInstanceId());
            System.out.println("所属流程定义id：" + processInstance.getProcessDefinitionId());
            System.out.println("是否执行完成：" + processInstance.isEnded());
            System.out.println("是否暂停：" + processInstance.isSuspended());
            System.out.println(" 当 前 活 动 标 识 ： " + processInstance.getActivityId());
            //businessKey在工作流中通常翻译为业务主键，比如我们的请假工作流，activiti是没有存储具体请假事项的，
            // 这就需要我们自己建立业务表来存储。而businessKey就是业务表的主键
            String businessKey = processInstance.getBusinessKey();
            System.out.println("流程实例：businessKey:" + businessKey);
        }

        return list;
    }

    /**
     * 挂起或激活流程实例, 当流程挂起的时候再去执行流程会抛出异常
     * org.activiti.engine.ActivitiException: Cannot complete a suspend task
     *
     * @param processDefinitionKey 流程定义key
     * @param processInstanceId    流程实例ID
     */
    public static void suspendOrActivateProcessInstance(String processDefinitionKey, String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        boolean suspend;
        if (StringUtils.isEmpty(processInstanceId)) {
            // 挂起全部流程,通过流程定义的唯一key操作
            RepositoryService repositoryService = processEngine.getRepositoryService();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
            suspend = processDefinition.isSuspended();
            if (suspend) {
                //如果暂停则激活，这里将流程定义下的所有流程实例全部激活
                repositoryService.activateProcessDefinitionByKey(processDefinitionKey, true, null);
                System.out.println("流程key为" + processDefinitionKey + "激活");
            } else {
                //如果激活则挂起，这里将流程定义下的所有流程实例全部挂起
                repositoryService.suspendProcessDefinitionByKey(processDefinitionKey, true, null);
                System.out.println("流程key为" + processDefinitionKey + "挂起");
            }
        } else { // 既然有全部流程挂起，那么必然有单个流程挂起
            // 获取RunTimeService
            RuntimeService runtimeService = processEngine.getRuntimeService();
            //根据流程实例id查询流程实例
            ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult();
            suspend = processInstance.isSuspended();
            if (suspend) {
                //如果暂停则激活
                runtimeService.activateProcessInstanceById(processInstanceId);
                System.out.println("流程实例：" + processInstanceId + "激活");
            } else {
                //如果激活则挂起
                runtimeService.suspendProcessInstanceById(processInstanceId);
                System.out.println("流程实例：" + processInstanceId + "挂起");
            }
        }
    }

    /**
     * 查询审批历史
     *
     * @param processInstanceId
     * @return
     */
    public static List<HistoricActivityInstance> queryHistoryProcess(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery();
        query.processInstanceId(processInstanceId);

        List<HistoricActivityInstance> list = query.list();
        for (HistoricActivityInstance ai : list) {
            System.out.println(ai.getActivityId());
            System.out.println(ai.getActivityName());
            System.out.println(ai.getProcessDefinitionId());
            System.out.println(ai.getProcessInstanceId());
            System.out.println("==============================");
        }

        return list;
    }

    /**
     * 拾取任务，这样任务才有了审批人，如我们的任务是 xiaowang 和 xiaozhao 都可以处理，可是必须先拾取，才能进行处理
     *
     * @param taskList 要拾取的任务集合
     * @param userId   任务候选人id
     */
    public static List<Task> claimTask(List<Task> taskList, String userId) {
        if (CollectionUtils.isEmpty(taskList)) {
            return taskList;
        }
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        /**
         * 拾取任务，即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
         * 校验该用户有没有拾取任务的资格
         */
        for (Task task : taskList) {
            if (checkTaskByCandidateUser(task.getId(), userId)) {
                taskService.claim(task.getId(), userId);
                //拾取成功 act_ru_task 表的 assign 字段会变成我们的任务候选人id xiaowang
                System.out.println("任务拾取成功");
            }
        }

        return taskList;
    }

    public static boolean checkTaskByCandidateUser(String taskId, String candidateUser) {
        // 根据候选人查询
        return buildTaskQueryByTaskId(taskId).taskCandidateUser(candidateUser).singleResult() != null;
    }

    public static boolean chceckTaskByAssignee(String taskId, String assignee) {
        // 根据处理人查询任务
        return buildTaskQueryByTaskId(taskId).taskAssignee(assignee).singleResult() != null;
    }

    /**
     * @param processKey
     * @param userId
     */
    public static void restitutionGroupTask(String processKey, String userId, List<Task> taskList) {
        // 如果设置为null，归还组任务, 任务没有负责人,哈哈，看见代码是不是很假，只是把act_ru_task的assign字段设置为null而已，并不是什么骚操作
        setAssignee(processKey, userId, taskList, null);
    }

    /**
     * 任务转交给其他候选人来进行处理
     *
     * @param processKey
     * @param userId
     * @param turnToUserId
     * @param taskList
     */
    public static void turnTaskToOtherCandidateUsers(String processKey, String userId, String turnToUserId, List<Task> taskList) {
        setAssignee(processKey, userId, taskList, turnToUserId);
    }

    private static void setAssignee(String processKey, String userId, List<Task> taskList, String assignee) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        // 校验 userId 是否是 taskId 的负责人，如果是负责人才可以归还组任务
        for (Task task : taskList) {
            if (ActivitiHelper.chceckTaskByAssignee(task.getId(), userId)) {
                taskService.setAssignee(task.getId(), assignee);
            }
        }
    }

}
