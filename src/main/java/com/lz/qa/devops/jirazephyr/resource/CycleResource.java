package com.lz.qa.devops.jirazephyr.resource;

import com.alibaba.fastjson.JSON;
import com.lz.qa.devops.jirazephyr.entity.CycleEntity;

public class CycleResource extends BaseResource {
    private String uri = "cycle/";

    public CycleResource(String jiraUrl, String jiraUser, String jiraPass){
        super(jiraUrl, jiraUser, jiraPass);
    }

    /**
     * 获取cycle的信息
     * @param id
     * @return
     * @throws Exception
     */
    public CycleEntity getCycleInformation(int id) throws Exception{
        String uri = String.format("cycle/%d", id);
        return JSON.parseObject(get(uri), CycleEntity.class);

    }

    public void createNewCycle() throws Exception{

    }

    public void deleteCycle() throws Exception{

    }

    public void listCycle() throws Exception{

    }
}
