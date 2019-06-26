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
    private Integer versionId;
    private String environment;
    private String build;
    private String createdBy;
    private String name;
    private String modifiedBy;
    private Integer id;
    private Integer projectId;
    private String startDate;
}
