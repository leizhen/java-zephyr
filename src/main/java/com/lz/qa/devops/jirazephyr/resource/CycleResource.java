package com.lz.qa.devops.jirazephyr.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lz.qa.devops.jirazephyr.entity.CycleEntity;
import com.lz.qa.devops.jirazephyr.entity.ListCycleResultEntity;

import java.util.ArrayList;
import java.util.List;

public class CycleResource extends BaseResource {
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

    /**
     * 返回cycle的数据
     * @param id
     * @return
     * @throws Exception
     */
    public String exportCycleData(int id) throws Exception{
        String uri = String.format("cycle/%dexport?versionId=&projectId=&folderId=", id);
        String content = get(uri);
        return JSON.parseObject(content).getString("url");
    }

    /**
     * 创建一个cycle
     * @param cycleEntity
     * @return
     * @throws Exception
     */
    public String createNewCycle(CycleEntity cycleEntity) throws Exception{
        String uri = "cycle";
        return post(uri, JSON.toJSONString(cycleEntity));

    }

    /**
     * 更新cycle
     * @param cycleEntity
     * @return
     * @throws Exception
     */
    public String updateCycle(CycleEntity cycleEntity) throws Exception{
        String uri = "cycle";
        return put(uri, JSON.toJSONString(cycleEntity));
    }

    /**
     * 删除cycle
     * @throws Exception
     */
    public String deleteCycle(int id) throws Exception{
        String uri = String.format("cycle/%s?isFolderCycleDelete=", id);
        return delete(uri);
    }

    /**
     * 获取某个项目的cycle
     *
     * @param projectId 必填
     * @param versionId -1的时候，对应的是unreleased；每个version，都会有一个cycle=-1的adhoc
     * @return
     * @throws Exception
     */
    public List<ListCycleResultEntity> listCycle(int projectId, int versionId) throws Exception{
        String uri = String.format("cycle?projectId=%d&versionId=%d&id=&offset=&issueId=&expand=", projectId, versionId);
        String content = get(uri);
        JSONObject jsonObject = JSON.parseObject(content);
        List<ListCycleResultEntity> list = new ArrayList<ListCycleResultEntity>();
        for(String key : jsonObject.keySet()){
            if("recordsCount".equalsIgnoreCase(key)){//不是cycle的信息
                continue;
            }
            else{
                String cycleInfo = jsonObject.getString(key);
                ListCycleResultEntity entity = JSON.parseObject(cycleInfo, ListCycleResultEntity.class);
                entity.setCycleId(Integer.valueOf(key));
                list.add(entity);
            }
        }
        return list;
    }
}
