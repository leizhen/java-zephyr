package com.lz.qa.devops.jirazephyr.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lz.qa.devops.jirazephyr.entity.ExecutionEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ExecutionResource extends BaseResource {
    public ExecutionResource(String jiraUrl, String jiraUser, String jiraPass){
        super(jiraUrl, jiraUser, jiraPass);
    }

    /**
     *
     * @param cycleId
     * @param issues
     * @param projectId
     * @param versionId
     * @return
     * @throws Exception
     */
    public String addTestToCycle(int projectId, int cycleId, String[] issues, int versionId) throws Exception{
        String uri = "execution/addTestsToCycle";
        IndividualTest individualTest = new IndividualTest();
        individualTest.setCycleId(cycleId);
        individualTest.setIssues(issues);
        individualTest.setProjectId(projectId);
        individualTest.setVersionId(versionId);

        return post(uri, JSON.toJSONString(individualTest));
    }

    /**
     * @param projectId
     * @param cycleId
     * @param issues
     * @return
     * @throws Exception
     */
    public String addTestToCycle(int projectId, int cycleId, String[] issues) throws Exception{
        return addTestToCycle(projectId, cycleId, issues, -1);
    }

    /**
     * 获取某个项目和cycle里面的所有用例
     * @param projectId
     * @param cycleId
     * @return
     * @throws Exception
     */
    public List<ExecutionEntity> executionList(int projectId, int cycleId) throws Exception{
        String uri = String.format("execution?issueId=&projectId=%d&versionId=&cycleId=%d&offset=&action=&sorter=&expand=&limit=&folderId=", projectId, cycleId);
        String content = get(uri);
        JSONObject jsonObject = JSON.parseObject(content);
        String executions = jsonObject.getString("executions");
        return JSON.parseArray(executions, ExecutionEntity.class);
    }

    /**
     * 删除一个execution
     * @param executionId
     * @return
     * @throws Exception
     */
    public String delete(int executionId) throws Exception{
        String uri = String.format("execution/%d", executionId);
        return delete(uri);
    }

    /**
     * 更改execution的状态: -1 - 未执行， 1 - 通过， 2 - 失败， 3 - 测试进行中， 4 - 阻止
     * @param executionId
     * @param status
     * @return
     * @throws Exception
     */
    public String update(int executionId, String status) throws Exception{
        String uri = String.format("execution/%d/execute", executionId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        return put(uri, jsonObject.toJSONString());
    }

    public String update(int executionId, ExecutionStatus executionStatus) throws Exception{
        return update(executionId, executionStatus.getStatus());
    }


    /**
     * {
     *     "issues": ["DEMO-89"],
     *     "versionId": -1,
     *     "cycleId": 7,
     *     "projectId": 10418,
     *     "method": "1"
     * }
     */
    @Getter
    @Setter
    static class IndividualTest{
        int cycleId;
        String[] issues;
        String method = "1";
        int projectId;
        int versionId = -1;//-1是unreleased

    }
}
