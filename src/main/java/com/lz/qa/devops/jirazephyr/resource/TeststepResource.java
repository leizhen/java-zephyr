package com.lz.qa.devops.jirazephyr.resource;

import com.alibaba.fastjson.JSON;
import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TeststepResource extends BaseResource {
    public TeststepResource(String jiraUrl, String jiraUser, String jiraPass){
        super(jiraUrl, jiraUser, jiraPass);
    }

    /**
     * 删除测试步骤
     * @param issueId
     * @param testStepId
     * @throws Exception
     */
    public void delete(int issueId, int testStepId) throws Exception{
        String uri = String.format("teststep/%d/%d", issueId, testStepId);
        delete(uri);
    }

    /**
     * 更新测试步骤
     * @param issueId
     * @param testStepId
     * @param teststepEntity
     * @throws Exception
     */
    public void update(int issueId, int testStepId, TeststepEntity teststepEntity) throws Exception{
        String uri = String.format("teststep/%d/%d", issueId, testStepId);
        put(uri, JSON.toJSONString(teststepEntity));
    }

    /**
     * 列出测试步骤
     * @param issueId
     * @param offset
     * @param limit
     * @return
     * @throws Exception
     */
    public List<TeststepEntity> list(int issueId, int offset, int limit) throws Exception{
        String uri = String.format("teststep/%d?offset=%d&limit=%d", issueId, offset, limit);
        String response = get(uri);
        String stepBeanCollection = JSON.parseObject(response).get("stepBeanCollection").toString();
        return JSON.parseArray(stepBeanCollection, TeststepEntity.class);
    }

    public List<TeststepEntity> list(int issueId) throws Exception{
        return list(issueId, 0, 100);
    }

    /**
     * 创建测试步骤
     * @param issueId
     * @param teststepEntity
     * @return
     * @throws Exception
     */
    public TeststepEntity create(int issueId, TeststepEntity teststepEntity) throws Exception{
        String uri = String.format("teststep/%d", issueId);
        String response = post(uri, JSON.toJSONString(teststepEntity));
        return JSON.parseObject(response, TeststepEntity.class);
    }
}
