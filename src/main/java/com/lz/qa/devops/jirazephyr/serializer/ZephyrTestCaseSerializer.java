package com.lz.qa.devops.jirazephyr.serializer;

import com.lz.qa.api.Bean.TestCase;
import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import com.lz.qa.devops.jirazephyr.resource.TeststepResource;
import net.rcarz.jiraclient.Issue;

/**
 * jira接口测试用例与qa-autotest的TestCase映射
 */
public class ZephyrTestCaseSerializer {
    String jiraUrl;
    String jiraUser;
    String jiraPass;
    public ZephyrTestCaseSerializer(String jiraUrl, String jiraUser, String jiraPass){
        this.jiraUrl = jiraUrl;
        this.jiraUser =  jiraUser;
        this.jiraPass = jiraPass;
    }
    public void serializeToZephyr(Issue issue, TestCase testCase){

    }

    public TestCase serializeToTestCase(Issue issue) throws Exception{
        TeststepResource teststepResource = new TeststepResource(jiraUrl, jiraUser, jiraPass);
        TestCase testCase = new TestCase();
        for(TeststepEntity teststepEntity : teststepResource.list(issue.getId())){
            if("请求方式".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setRequestMethod(teststepEntity.getData());
            }
            else if("请求地址".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setUri(teststepEntity.getData());
                testCase.setApiURL(teststepEntity.getData());
            }
            else if("请求头".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setHeader(teststepEntity.getData());
            }
            else if("请求体".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setRequestBody(teststepEntity.getData());
            }
            else if("请求参数".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setRequestParameter(teststepEntity.getData());
            }
            else if("预期值匹配".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setCheckPoint(teststepEntity.getData());
            }
            else if("输出值".equalsIgnoreCase(teststepEntity.getStep())){
                testCase.setOutPutParameter(teststepEntity.getData());
            }
        }
        return testCase;
    }
}
