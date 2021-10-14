package com.mokhtarabadi.secureapi.server.model.response;

import com.mokhtarabadi.secureapi.server.model.base.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateUserResponse extends BaseResponse {

    private int id;
}
