package com.mokhtarabadi.secureapi.server.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserModel {
    @NonNull private Integer id;
    @NonNull private String username;
    @NonNull private String name;
}
