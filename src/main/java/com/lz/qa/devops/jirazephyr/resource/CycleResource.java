package com.lz.qa.devops.jirazephyr.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lz.qa.devops.jirazephyr.entity.CycleEntity;
import com.lz.qa.devops.jirazephyr.entity.ListCycleResultEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
     * @return 新创建的cycle的id
     * @throws Exception
     */
    public String createNewCycle(CycleEntity cycleEntity) throws Exception{
        String uri = "cycle";
        String content = post(uri, JSON.toJSONString(cycleEntity));
        return JSON.parseObject(content).getString("id");
    }

    public String createCycle(int projectId, String name) throws Exception{
        CycleEntity cycleEntity = new CycleEntity();
        cycleEntity.setName(name);
        cycleEntity.setProjectId(projectId);
        return createNewCycle(cycleEntity);
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

    public List<ListCycleResultEntity> listUnreleasedCycle(int projectId) throws Exception{
        return listCycle(projectId, -1);
    }

    public List<ListCycleResultEntity> findCycleByVersionName(int projectId, String versionName) throws Exception{
        List<ListCycleResultEntity> list = new ArrayList<ListCycleResultEntity>();
        String uri = String.format("cycle?projectId=%d&versionId=&id=&offset=&issueId=&expand=", projectId);
        String content = get(uri);
        JSONObject jsonObject = JSON.parseObject(content);
        for(String key : jsonObject.keySet()){
            String subContent = jsonObject.getString(key);
            for(String s : JSON.parseArray(subContent, String.class)){
                JSONObject j = JSON.parseObject(s);
                for(String k : j.keySet()){
                    if("recordsCount".equalsIgnoreCase(k)){
                        continue;
                    }
                    else{
                        ListCycleResultEntity entity = JSON.parseObject(j.getString(k), ListCycleResultEntity.class);
                        entity.setCycleId(Integer.valueOf(k));
                        if(entity.getVersionName().equalsIgnoreCase(versionName)){
                            log.info("添加{}到{}", entity.getName(), versionName);
                            list.add(entity);
                        }
                    }
                }
            }
        }
        return list;
    }
}
