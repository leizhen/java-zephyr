package com.lz.qa.devops.jirazephyr.loader;

import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import com.lz.qa.devops.jirazephyr.resource.TeststepResource;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.JiraClient;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import net.rcarz.jiraclient.Issue;
@Slf4j
public class NormalTestcaseLoader extends TestcaseLoader{
    public NormalTestcaseLoader(String jiraUrl, String jiraUser, String jiraPass){
        super(jiraUrl, jiraUser, jiraPass);
    }

    public NormalTestcaseLoader(String jiraUrl, String jiraUser, String jiraPass, String excelFileName){
        super(jiraUrl, jiraUser, jiraPass, excelFileName);
    }

    private TeststepEntity createTeststepEntity(Row row){
        String step, data, result;
        try {
            step = row.getCell(1).getStringCellValue();
            if(StringUtils.isEmpty(step)){
                return null;
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
        try{
            data = row.getCell(2).getStringCellValue();
        }catch(Exception e){
            data = "";
        }
        try{
            result = row.getCell(3).getStringCellValue();
        }catch(Exception e) {
            result = "";
        }
        TeststepEntity entity = new TeststepEntity();
        entity.setStep(step);
        entity.setData(data);
        entity.setResult(result);
        return entity;

    }

    /**
     * 导入一个sheet表中的测试用例，即一个模块的
     * excel模板: 测试用例名称 | 测试步骤 | 测试数据 | 期望结果
     * @param sheet
     * @throws Exception
     */
    void load(XSSFSheet sheet) throws Exception{
        TeststepResource teststepResource = new TeststepResource(jiraUrl, jiraUser, jiraPass);
        String componentName = sheet.getSheetName();
        for(int i = 1; i <= sheet.getLastRowNum();){
            String testCaseSummary = sheet.getRow(i).getCell(0).toString();
            String priority, assignee;
            try{
                priority = sheet.getRow(i).getCell(4).getStringCellValue();
            }catch(Exception e){
                log.info("没有指定优先级，采用默认优先级P2");
                priority = "p2";
            }
            try{
                assignee = sheet.getRow(i).getCell(5).getStringCellValue();
            }catch(Exception e){
                log.info("没有指定用例编写人员，采用默认人员");
                assignee = "qa";
            }
            Issue issue;
            if(!StringUtils.isEmpty(testCaseSummary)){//一个新的用例
                try{
                    issue = issueExist(projectKey, testCaseSummary);
                }catch(Exception e){//如果不能准确搜索issue结果，则跳过
                    continue;
                }

                if(issue != null){
                    log.info("{}已经存在，更新测试用例.", testCaseSummary);
                    deleteAllTeststep(Integer.valueOf(issue.getId()));
                }
                else{
                    issue = createTestcase(projectKey, testCaseSummary, componentName);
                    log.info("创建测试用例:{}", testCaseSummary);
                }

                //下面开始创建测试步骤
                TeststepEntity teststepEntity;
                for(;i <= sheet.getLastRowNum();){
                    if(sheet.getRow(i).getCell(0) == null || StringUtils.isEmpty(sheet.getRow(i).getCell(0).getStringCellValue()) || sheet.getRow(i).getCell(0).getStringCellValue().equalsIgnoreCase(testCaseSummary)){//测试步骤里面的测试名称为空
                        teststepEntity = createTeststepEntity(sheet.getRow(i));
                        if(teststepEntity != null) {
                            teststepResource.create(Integer.valueOf(issue.getId()), teststepEntity);
                        }

                        i++;
                    }
                    else{
                        break;
                    }
                }
            }
        }
    }
}
