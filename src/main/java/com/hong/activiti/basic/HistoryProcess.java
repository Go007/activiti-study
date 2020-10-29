package com.hong.activiti.basic;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;

import java.util.List;

/**
 * 查询审批历史
 */
public class HistoryProcess {
    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery();
        query.processInstanceId("2501");

        List<HistoricActivityInstance> list = query.list();
        for (HistoricActivityInstance ai : list) {
            System.out.println(ai.getActivityId());
            System.out.println(ai.getActivityName());
            System.out.println(ai.getProcessDefinitionId());
            System.out.println(ai.getProcessInstanceId());
            System.out.println("==============================");
        }
    }
}
