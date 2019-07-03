package com.lz.qa.devops.jirazephyr.resource;

/**
 * 测试用例的执行结果
 */
public enum ExecutionStatus {
    PASS(1),FAIL(2),WIP(3),BLOCK(4),UNATTEMPT(-1);
    int status;
    ExecutionStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public String toString(){
        switch (status){
            case 1:
                return "测试通过";
            case 2:
                return "失败";
            case 3:
                return "测试中";
            case 4:
                return "被阻碍";
            default:
                return "未开始";
        }
    }
}
