package com.lz.qa.devops.jirazephyr.resource;

/**
 * 测试用例的执行结果
 */
public enum ExecutionStatus {
    PASS("1"),FAIL("2"),WIP("3"),BLOCK("4"),UNATTEMPT("-1");
    String status;
    ExecutionStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
}
