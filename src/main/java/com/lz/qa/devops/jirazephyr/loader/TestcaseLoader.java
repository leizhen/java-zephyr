package com.lz.qa.devops.jirazephyr.loader;

import com.lz.qa.devops.jirazephyr.entity.TeststepEntity;
import com.lz.qa.devops.jirazephyr.resource.TeststepResource;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class TestcaseLoader {
    String excelFile;
    String projectKey;
    String testcaseType = "";
    JiraClient jiraClient;
    String jiraUrl;
    String jiraUser;
    String jiraPass;

    public TestcaseLoader(String jiraUrl, String jiraUser, String jiraPass){
        this.jiraUrl = jiraUrl;
        this.jiraUser = jiraUser;
        this.jiraPass = jiraPass;
        BasicCredentials basicCredentials = new BasicCredentials(jiraUser, jiraPass);
        try {
            jiraClient = new JiraClient(jiraUrl, basicCredentials);
        } catch (JiraException e) {
            e.printStackTrace();
        }
    }


    public static TestcaseLoader createTestcaseLoader(String jiraUrl, String jiraUser, String jiraPass, String filePath){
        if(FilenameUtils.getBaseName(filePath).endsWith("-interface")){
            return new InterfaceTestcaseLoader(jiraUrl, jiraUser, jiraPass, filePath);
        }
        else{
            return new NormalTestcaseLoader(jiraUrl, jiraUser, jiraPass, filePath);
        }
    }

    public TestcaseLoader(String jiraUrl, String jiraUser, String jiraPass, String excelFile){
        this.excelFile = excelFile;
        this.jiraUrl = jiraUrl;
        this.jiraUser = jiraUser;
        this.jiraPass = jiraPass;
        BasicCredentials basicCredentials = new BasicCredentials(jiraUser, jiraPass);
        try {
            jiraClient = new JiraClient(jiraUrl, basicCredentials);
        } catch (JiraException e) {
            e.printStackTrace();
        }
        projectKey = FilenameUtils.getBaseName(excelFile);
        if(projectKey.endsWith("-app")){
            testcaseType = "app";
            projectKey = projectKey.replace("-app", "");
        }
        else if(projectKey.endsWith("-interface")){
            testcaseType = "接口";
            projectKey = projectKey.replace("-interface", "");
        }
    }

    public void doLoad() throws Exception{
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(excelFile)));
        for(int i = 0; i < workbook.getNumberOfSheets(); i ++){
            load(workbook.getSheetAt(i));
        }
    }

    /**
     * 判断是否创建过之前
     * 是的话，跳过这个用例的创建
     * @param projectKey
     * @param summary
     * @return
     */
    protected Issue issueExist(String projectKey, String summary){
        summary = summary.replace("-", "\\\\-");//jql针对-需要转义
        String jql = "PROJECT=" + projectKey + " AND issuetype = 测试 AND summary ~ " + "\"" + summary + "\"";
        log.info(jql);
        try{
            Issue.SearchResult searchResult = jiraClient.searchIssues(jql);

            if(searchResult.total == 1){
                return searchResult.issues.get(0);
            }
            else if(searchResult.total > 1){
                throw new Exception(summary + " 找到不止一个同样名称的测试用例。");
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }
        return null;
    }

    void load(XSSFSheet sheet) throws Exception{

    }

    /**
     * 删除用例的所有的步骤
     * @param issueId
     * @throws Exception
     */
    void deleteAllTeststep(int issueId) throws Exception{
        TeststepResource teststepResource = new TeststepResource(jiraUrl, jiraUser, jiraPass);
        for(TeststepEntity teststepEntity : teststepResource.list(issueId)){
            log.info("删除{}的步骤{}", issueId, teststepEntity.getId());
            teststepResource.delete(issueId, teststepEntity.getId());
        }
    }

    void deleteAllTeststep(String issueId) throws Exception{
        deleteAllTeststep(Integer.valueOf(issueId));
    }

    Issue createTestcase(String projectKey, String testcaseSummary, String component) throws Exception{
        createComponent(projectKey, component);

        Issue.FluentCreate fluentCreate = jiraClient.createIssue(projectKey.toUpperCase(), "测试")
                .field("summary", testcaseSummary)
                .field("components", Arrays.asList(new String[]{component}));
        if(!StringUtils.isEmpty(testcaseType)){
            fluentCreate.field("labels", Arrays.asList(new String[]{testcaseType}));
        }
        return fluentCreate.execute();
    }

    /**
     * 如果component name不存在，先创建component
     * @param componentName
     */
    private void createComponent(String projectKey, String componentName) throws Exception{
        for(Component component : jiraClient.getProject(projectKey).getComponents()){
            if(component.getName().equalsIgnoreCase(componentName)){
                return;
            }
        }
        jiraClient.createComponent(projectKey).name(componentName).execute();
    }

    /**
     * 获取所有的zephyr测试用例
     */
    public List<Issue> testcases(String jql) throws Exception{
        Iterator<Issue> iterator = jiraClient.searchIssues(jql).iterator();
        List<Issue> list = new ArrayList<Issue>();
        while(iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }
}
