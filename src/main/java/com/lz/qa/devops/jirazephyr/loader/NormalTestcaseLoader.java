package com.lz.qa.devops.jirazephyr.loader;

import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import com.lz.qa.devops.jirazephyr.resource.TeststepResource;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.JiraClient;
import org.apache.commons.lang.StringUtils;
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
                i ++;
                for(;i <= sheet.getLastRowNum();){
                    if(sheet.getRow(i).getCell(0) == null || StringUtils.isEmpty(sheet.getRow(i).getCell(0).getStringCellValue())){//测试步骤里面的测试名称为空
                        String step, data, result;
                        step = data = result = "";
                        try{
                            step = sheet.getRow(i).getCell(1).getStringCellValue();
                        }catch(Exception e){
                            log.error("测试步骤没有");
                            log.error(e.getMessage(), e);
                        }
                        try{
                            data = sheet.getRow(i).getCell(2).getStringCellValue();
                        }catch(Exception e){
                            log.info("没有测试数据");
                        }
                        try{
                            result = sheet.getRow(i).getCell(3).getStringCellValue();
                        }catch(Exception e) {
                            log.info("没有测试验证");
                        }

                        TeststepEntity entity = new TeststepEntity();
                        entity.setStep(step);
                        entity.setData(data);
                        entity.setResult(result);


                        teststepResource.create(Integer.valueOf(issue.getId()), entity);

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
