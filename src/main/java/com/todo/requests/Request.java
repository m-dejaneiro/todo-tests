package com.todo.requests;

import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;

public class Request {
    protected RequestSpecification reqSpec;

    public Request(RequestSpecification reqSpec) {
        this.reqSpec = reqSpec;
    }
}
