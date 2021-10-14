package com.mokhtarabadi.secureapi.server;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mokhtarabadi.secureapi.server.controller.UserController;
import com.mokhtarabadi.secureapi.server.model.APIAccessDetails;
import com.mokhtarabadi.secureapi.server.model.base.BaseResponse;
import com.mokhtarabadi.secureapi.server.repository.UserRepository;
import com.mokhtarabadi.secureapi.server.router.UserRouter;
import com.mokhtarabadi.secureapi.server.transformer.JacksonTransformer;
import com.mokhtarabadi.secureapi.server.utility.EncryptionUtility;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;

import static spark.Spark.*;

@Slf4j
public class Main {

    public static final HashMap<String, APIAccessDetails> API_ACCESS_DETAILS = new HashMap<>();

    public static void main(String[] args) {

        API_ACCESS_DETAILS.put(
                "android",
                APIAccessDetails.builder()
                        .key("1234567890asdfghjkl")
                        .secret("zxcvbnm,asdfghjklwqertyuiol")
                        .build());

        val jsonMapper = new JsonMapper();
        val jacksonTransformer = new JacksonTransformer(jsonMapper);

        val userRepository = new UserRepository();
        val userController = new UserController(jsonMapper, userRepository);
        val userRouter = new UserRouter(userController, jacksonTransformer);

        path(
                "/api/v1/",
                () -> {
                    before(
                            "/*",
                            "application/json",
                            (request, response) -> {
                                EncryptionUtility.checkApiKey(request, response);
                                log.info("received api call: {}" + request.pathInfo());
                            });

                    notFound(
                            (request, response) -> {
                                val result = new BaseResponse();
                                result.setErrorMessage("api call not found");
                                return result;
                            });

                    internalServerError(
                            (request, response) -> {
                                val result = new BaseResponse();
                                result.setErrorMessage("an error occurred in backend");
                                return result;
                            });

                    userRouter.registerRoutes(ApiVersion.V1);

                    after(
                            "/*",
                            (request, response) ->
                                    response.type("application/json; charset=UTF-8"));
                });
    }

    public enum ApiVersion {
        V1,
        V2
    }
}
