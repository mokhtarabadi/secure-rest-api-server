package com.mokhtarabadi.secureapi.server.model.base;

import lombok.Data;

@Data
public class BaseResponse {
    private boolean success;
    private String errorMessage;
}
