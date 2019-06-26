package com.lz.qa.devops.jirazephyr.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListCycleResultEntity {
    @JSONField(serialize = false)
    int cycleId;
    int totalExecutions;
    String endDate;
    String description;
    int totalExecuted;
    String started;
    String versionName;
    boolean isExecutionWorkflowEnabledForProject;
    String expand;
    String projectKey;
    int versionId;
    String environment;
    String createdDate;
    int totalCycleExecutions;
    boolean isTimeTrackingEnabled;
    String build;
    String ended;
    String name;
    String modifiedBy;
    int projectId;
    String startDate;
    String executionSummaries;
}
