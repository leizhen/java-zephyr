package com.lz.qa.devops.jirazephyr.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeststepEntity {
    private Integer id;
    private Integer orderId;
    private String step;
    private String data;
    private String result;
    private String createdBy;
    private String modifiedBy;
    private String htmlStep;
    private String htmlResult;
    private Object[] attachmentsMap;
    private Object customFields;
    private Integer totalStepCount;
    private Object customFieldValuesMap;

    public TeststepEntity step(String step){
        this.step = step;
        return this;
    }

    public TeststepEntity data(String data){
        this.data = data;
        return this;
    }

    public TeststepEntity result(String result){
        this.result = result;
        return this;
    }
}
