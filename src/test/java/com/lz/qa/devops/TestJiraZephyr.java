package com.lz.qa.devops;

import com.lz.qa.devops.jirazephyr.entity.ExecutionEntity;
import com.lz.qa.devops.jirazephyr.entity.ListCycleResultEntity;
import com.lz.qa.devops.jirazephyr.resource.CycleResource;
import com.lz.qa.devops.jirazephyr.resource.ExecutionResource;
import com.lz.qa.devops.jirazephyr.resource.ExecutionStatus;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class TestJiraZephyr {
    String jiraUrl = "http://192.168.10.31:8080/";
    String jiraUser = "leizhens";
    String jiraPassword = "f4go48";

    @Test
    public void f() throws Exception{
        int total = 0;
        int totalExecuted = 0;

        CycleResource cycleResource = new CycleResource(jiraUrl, jiraUser, jiraPassword);
        ExecutionResource executionResource = new ExecutionResource(jiraUrl, jiraUser, jiraPassword);
        for(ListCycleResultEntity entity : cycleResource.findCycleByVersionName(10419, "v0.4")){
            log.info(entity.getName());
            for(ExecutionEntity eentity:executionResource.executionList(entity.getProjectId(), entity.getCycleId())){
                log.info(eentity.getSummary() + " " + eentity.getExecutionStatus());
                if(eentity.getExecutionStatus().equalsIgnoreCase("1")){
                    totalExecuted ++;
                }

            }
     total += entity.getTotalCycleExecutions();


            log.info("总共{}条用例，执行{}条", total, totalExecuted);
        }
    }

    @Test
    public void f2() throws Exception{
        CycleResource cycleResource = new CycleResource(jiraUrl, jiraUser, jiraPassword);
        //log.info(cycleResource.createCycle(10418, "automation-heihei"));
        cycleResource.findCycleByVersionName(10419,"v0.4");
    }

}
