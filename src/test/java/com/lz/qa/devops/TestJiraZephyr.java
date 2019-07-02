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
    String jiraUser = "leizhen";
    String jiraPassword = "f4go48";

    @Test
    public void f() throws Exception{


        CycleResource cycleResource = new CycleResource(jiraUrl, jiraUser, jiraPassword);
        ExecutionResource executionResource = new ExecutionResource(jiraUrl, jiraUser, jiraPassword);
        for(ListCycleResultEntity entity : cycleResource.listUnreleasedCycle(10418)){
            log.info(entity.getName());
            for(ExecutionEntity eentity:executionResource.executionList(entity.getProjectId(), entity.getCycleId())){
                log.info(eentity.getSummary());
                int executionId = eentity.getId();
                executionResource.update(executionId, ExecutionStatus.PASS);
                break;
            }
        }
    }

    @Test
    public void f2() throws Exception{
        CycleResource cycleResource = new CycleResource(jiraUrl, jiraUser, jiraPassword);
        log.info(cycleResource.createCycle(10418, "automation-heihei"));
    }

}
