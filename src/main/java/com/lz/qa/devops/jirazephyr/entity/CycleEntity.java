package com.lz.qa.devops.jirazephyr.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CycleEntity {
    private String endDate;
    private String description;
    private String versionName;
    private Integer sprintId;
    private Integer versionId = -1;//-1是unscheduled
    private String environment;
    private String build;
    private String createdBy;
    private String name;//必填
    private String modifiedBy;
    private Integer id;
    private Integer projectId;//必填
    private String startDate;
}
