package com.lz.qa.devops.jirazephyr.loader;

import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import com.lz.qa.devops.jirazephyr.resource.TeststepResource;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

@Slf4j
public class InterfaceTestcaseLoader extends TestcaseLoader {

    public InterfaceTestcaseLoader(String jiraUrl, String jiraUser, String jiraPass){
        super(jiraUrl, jiraUser, jiraPass);
    }

    public InterfaceTestcaseLoader(String jiraUrl, String jiraUser, String jiraPass, String excelFileName){
        super(jiraUrl, jiraUser, jiraPass, excelFileName);
        this.testcaseType = "接口";
    }

    /**
     * 接口测试用例loader
     * 采用的是自动化测试用例的模板
     * 过滤掉前6行的全局设置和表头
     * 表头对应teststep
     * 用例标题生成规则：接口编号-接口名称-用例名
     * @param sheet
     * @throws Exception
     */
    @Override
    void load(XSSFSheet sheet) throws Exception {
        String testcaseNo, testcaseSummary, interfaceName, interfaceCode, requestType;
        String requestUri, requestHeader, requestBody, requestParameter;
        String expect, output;
        String component = sheet.getSheetName();
        for(int i = 6; i <= sheet.getLastRowNum(); i ++){
            Row row = sheet.getRow(i);

            try{
                testcaseNo = row.getCell(1).getStringCellValue();
            }catch(Exception e){
                log.info("没有测试用例编号，跳过");
                testcaseNo = "";
            }

            try{
                testcaseSummary = row.getCell(2).getStringCellValue();
            }catch(Exception e){
                log.info("没有用例标题，跳过");
                testcaseSummary = "";
            }

            if(StringUtils.isEmpty(testcaseNo) && StringUtils.isEmpty(testcaseSummary)){
                break;
            }

            try{
                interfaceName = row.getCell(3).getStringCellValue();
            }catch(Exception e){
                log.info("没有接口名称");
                interfaceName = "";
            }

            try{
                interfaceCode = row.getCell(4).getStringCellValue();
            }catch(Exception e){
                log.info("没有接口代码");
                interfaceCode = "";
            }

            String newTestcaseSummary = generateTestcaseSummary(interfaceCode, interfaceName, testcaseNo, testcaseSummary);
            Issue issue = issueExist(projectKey, newTestcaseSummary);
            if(issue != null){
                log.info("用例{}已存在，先删掉以前的步骤", newTestcaseSummary);
                deleteAllTeststep(issue.getId());
            }
            else{
                issue = createTestcase(projectKey, newTestcaseSummary, component);
            }

            try{
                requestType = row.getCell(5).getStringCellValue();
            }catch(Exception e){
                log.info("没有请求方式");
                requestType = "";
            }

            try{
                requestUri = row.getCell(6).getStringCellValue();
            }catch(Exception e){
                log.info("没有请求地址");
                requestUri = "";
            }
            try{
                requestHeader = row.getCell(7).getStringCellValue();
            }catch(Exception e){
                log.info("没有请求头");
                requestHeader = "";
            }

            try{
                requestBody = row.getCell(8).getStringCellValue();
            }catch (Exception e){
                requestBody = "";
            }

            try{
                requestParameter = row.getCell(9).getStringCellValue();
            }catch(Exception e){
                requestParameter = "";
            }

            try{
                expect = row.getCell(10).getStringCellValue();
            }catch(Exception e){
                expect = "";
            }

            try{
                output = row.getCell(11).getStringCellValue();
            }catch(Exception e){
                output = "";
            }

            createTeststep(issue, requestType, requestUri, requestHeader, requestBody, requestParameter, expect, output);

        }
    }

    /**
     * 生成测试用例标题
     * @param interfaceCode
     * @param interfaceName
     * @param testcaseNo
     * @param testcaseSummary
     * @return
     */
    private String generateTestcaseSummary(String interfaceCode, String interfaceName, String testcaseNo, String testcaseSummary){
        StringBuilder sb = new StringBuilder();
        sb.append(interfaceCode + " - ");
        sb.append(interfaceName + " - ");
        sb.append(testcaseNo + " - ");
        sb.append(testcaseSummary);
        return sb.toString();
    }

    private void createTeststep(Issue issue, String requestType, String requestUri, String requestHeader, String requestBody, String requestParameter,
                                String expect, String output ) throws Exception{
        log.info("给{}创建步骤", issue.getSummary());
        TeststepResource teststepResource = new TeststepResource(jiraUrl, jiraUser,jiraPass);

        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("请求方式").data(requestType));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("请求地址").data(requestUri));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("请求头").data(requestHeader));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("请求体").data(requestBody));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("请求参数").data(requestParameter));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("预期值匹配").data(expect));
        teststepResource.create(Integer.valueOf(issue.getId()), new TeststepEntity().step("输出值").data(output));
    }

    /**
     * 反序列化
     * @param testcases
     * @param globalUrl
     * @param globalHeader
     * @param globalRequestParameter
     * @return
     * @throws Exception
     */
    public File unload(String testcases, String globalUrl, String globalHeader, String globalRequestParameter) throws Exception{
        String jql = "KEY in (" + testcases + ")";
        List<Issue> issueList = testcases(jql);
        File newFile = File.createTempFile("jira_zephyr_interface", ".xlsx");
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/automation/interface.xlsx"), newFile);
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(newFile));
        Sheet sheet = workbook.getSheetAt(0);
        sheet.getRow(1).createCell(2).setCellValue(globalUrl);
        sheet.getRow(2).createCell(2).setCellValue(globalHeader);
        sheet.getRow(3).createCell(2).setCellValue(globalRequestParameter);
        for(int i = 0; i < issueList.size(); i ++){
            Issue issue = issueList.get(i);
            Row row = sheet.createRow( 6 + i);
            unloadRow(row, issue);
        }
        workbook.write(new FileOutputStream(newFile));
        workbook.close();
        return newFile;
    }

    private Row unloadRow(Row row, Issue issue) throws Exception{
        String[] infos = issue.getSummary().split("-");
        TeststepResource teststepResource = new TeststepResource(jiraUrl, jiraUser, jiraPass);
        row.createCell(0).setCellValue("YES");
        row.createCell(1).setCellValue(infos[2].trim());
        row.createCell(2).setCellValue(infos[3].trim());
        row.createCell(3).setCellValue(infos[1].trim());
        row.createCell(4).setCellValue(infos[0].trim());

        List<TeststepEntity> testStepList = teststepResource.list(Integer.valueOf(issue.getId()));
        for(TeststepEntity teststepEntity : testStepList){
            if("请求方式".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(5).setCellValue(teststepEntity.getData());
            }
            else if("请求地址".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(6).setCellValue(teststepEntity.getData());
            }
            else if("请求头".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(7).setCellValue(teststepEntity.getData());
            }
            else if("请求体".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(8).setCellValue(teststepEntity.getData());
            }
            else if("请求参数".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(9).setCellValue(teststepEntity.getData());
            }
            else if("预期值匹配".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(10).setCellValue(teststepEntity.getData());
            }
            else if("输出值".equalsIgnoreCase(teststepEntity.getStep())){
                row.createCell(11).setCellValue(teststepEntity.getData());
            }
        }

        return row;
    }
}
