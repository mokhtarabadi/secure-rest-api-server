package com.mokhtarabadi.secureapi.server.transformer;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import spark.ResponseTransformer;

@RequiredArgsConstructor
public class JacksonTransformer implements ResponseTransformer {

    @NonNull private JsonMapper mapper;

    @Override
    public String render(Object model) throws Exception {
        return mapper.writeValueAsString(model);
    }
}
