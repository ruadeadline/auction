package com.uet.auction.common.Response;

import java.io.Serializable;

public class AuctionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String type;
    private String message;
    private Object data;

    public AuctionResponse(boolean success, String type, Object data) {
        this.success = success;
        this.type = type;
        this.data = data;
        if (data instanceof String) {
            this.message = (String) data;
        }
    }

    public AuctionResponse(boolean success, String type, String message, Object data) {
        this.success = success;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}