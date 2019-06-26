package com.lz.qa.devops.jirazephyr.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionEntity {
    int id;
    int orderId;
    String executionStatus;
    Object executionWorkflowStatus;
    String comment;
    String htmlComment;
    int cycleId;
    String cycleName;
    int versionId;
    String versionName;
    int projectId;
    String createdBy;
    String createdByDisplay;
    String createdByUserName;
    String modifiedBy;
    String createdOn;
    long createdOnVal;
    long issueId;
    String issueKey;
    String summary;
    String label;
    String component;
    String projectKey;
    boolean canViewIssue;
    boolean isIssueEstimateNil;
    boolean isExecutionWorkflowEnabled;
    boolean isTimeTrackingEnabled;
    int executionDefectCount;
    int stepDefectCount;
    int totalDefectCount;
    String customFields;
}
