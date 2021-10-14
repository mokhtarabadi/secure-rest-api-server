package com.mokhtarabadi.secureapi.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class APIAccessDetails {
    @NonNull private String key;
    @NonNull private String secret;
}
