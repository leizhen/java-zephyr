package com.lz.qa.devops.jirazephyr.resource;

import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseResource {
    private String baseUri = "rest/zapi/latest/";
    private String url;
    private String jiraUser;
    private String jiraPass;
    private static final String CONTENT_TYPE = "application/json";

    protected BaseResource(String jiraUrl, String jiraUser, String jiraPass){
        this.url = jiraUrl;
        this.jiraUser = jiraUser;
        this.jiraPass = jiraPass;
        if(!url.endsWith("/")){
            url = url + "/";
        }
        url = url + baseUri;
    }

    /**
     * get 请求
     * @param uri
     * @return
     * @throws Exception
     */
    public String get(String uri) throws Exception{
        return Unirest.get(url + uri).basicAuth(jiraUser, jiraPass).asString().getBody();
    }

    public String delete(String uri) throws Exception{
        return Unirest.delete(url + uri).basicAuth(jiraUser, jiraPass).asString().getBody();
    }

    /**
     * POST请求
     * @param uri
     * @param body
     * @return
     * @throws Exception
     */
    public String post(String uri, String body) throws Exception{
        return Unirest.post(url + uri).basicAuth(jiraUser,jiraPass).header("content-type", CONTENT_TYPE).body(body).asString().getBody();
    }

    public String put(String uri, String body) throws Exception{
        return Unirest.put(url + uri).basicAuth(jiraUser, jiraPass).header("content-type", CONTENT_TYPE).body(body).asString().getBody();
    }
}
