package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AllureApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.allure.AllureResults;
import guru.qa.niffler.model.allure.Project;
import io.qameta.allure.Step;

public class AllureApiClient extends RestClient {

    private final AllureApi allureApi;

    public AllureApiClient() {
        super(CFG.allureDockerServiceUrl());
        this.allureApi = create(AllureApi.class);
    }

    @Step("Create allure project")
    public void createProject(String projectId) {
        execute(allureApi.createProject(new Project(projectId)), 201);
    }

    @Step("Send allure results")
    public void sendResults(String projectId, AllureResults allureResults) {
        execute(allureApi.sendResults(projectId, allureResults), 200);
    }

    @Step("Generate allure report")
    public void generateReport(String projectId,
                               String executionName,
                               String executionFrom,
                               String executionType) {
        execute(allureApi.generateReport(projectId, executionName, executionFrom, executionType), 200);
    }
}