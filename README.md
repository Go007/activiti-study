# activiti-study
Activiti7 Demo
# 参考文章 https://www.jianshu.com/p/e8c956ab9959

# 操作步骤
0-使用Eclipse Activiti BPMN制作流程图
1-将制作好的流程图 .bpmn 放入 resources/bpmn 目录下
2-初始化Activiti数据库 InitActiviti 产生25张表
3-流程定义部署  DeployProcess 
select * from act_ge_bytearray; bpmn 二进制信息
select * from act_ge_property;  bpmn相关属性
select * from act_re_deployment; 流程定义部署表,记录流程部署信息
select * from act_re_procdef; 流程定义表,记录流程定义信息
4-开启流程实例 StartInstance
select * from act_ru_execution; 流程实例执行表
select * from act_ru_task; 任务执行表
select * from act_ru_identitylink; 任务参与者,记录当前参与任务的用户或组

select * from act_hi_actinst; 活动历史表,记录所有活动,包括 流程执行过程的其他活动,如开始事件,结束事件
select * from act_hi_identitylink; 任务参与者,记录历史参与任务的用户或组
select * from act_hi_procinst; 流程实例历史表,流程实例启动,会在此表插入一条记录,流程实例运行完成也不会删除
select * from act_hi_taskinst; 任务历史表,开始一个任务,不仅会在 act_ru_task 插入记录,也会在历史任务表插入记录,其主键就是任务ID,任务完成词表记录不删除
5-查询用户待办任务 QueryProcessList
6-处理待办任务
表变化情况: act_ru_task会删除已经完成的操作,并且插入下一个节点的相关信息,自动递交;
            act_hi_taskinst 在操作完成后,end_time_字段会更新
act_ru_identitylink act_hi_identitylink  会插入下一个节点的参与者
7-结束任务 CompleteTask
重复执行第5,6步,流程会自动结束
表变化情况: 所有 act_ru_* 数据会被清空,act_hi_actinst 会存储所有审批节点操作

# 流程变量：global变量和local变量
act_ru_variable #当前流程变量表
act_hi_varinst #历史流程变量表

# 监听器

# 网关
排他网关，并行网关，包含网关
