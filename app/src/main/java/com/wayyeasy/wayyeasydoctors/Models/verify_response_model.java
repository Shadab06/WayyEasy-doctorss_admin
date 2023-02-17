package com.wayyeasy.wayyeasydoctors.Models;

public class verify_response_model {
    private String token, message;
    private verify_response_model_sub result;

    public verify_response_model() {
    }

    public verify_response_model(String token, String message, verify_response_model_sub verify_response_model_sub) {
        this.token = token;
        this.message = message;
        result = verify_response_model_sub;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public verify_response_model_sub getResult() {
        return result;
    }
}
