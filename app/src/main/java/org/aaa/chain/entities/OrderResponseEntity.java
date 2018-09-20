package org.aaa.chain.entities;

import java.util.List;

public class OrderResponseEntity {

    private boolean success;
    private String message;
    private String errorcode;
    private List<OrderDataEntity> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public List<OrderDataEntity> getData() {
        return data;
    }

    public void setData(List<OrderDataEntity> data) {
        this.data = data;
    }
}
