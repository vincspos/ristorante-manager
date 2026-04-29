package com.ristorante.ui.common;

public class ServiceResult {

    private final boolean success;
    private final String message;

    private ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ServiceResult ok() {
        return new ServiceResult(true, null);
    }

    public static ServiceResult fail(String message) {
        return new ServiceResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}