package com.mokhtarabadi.secureapi.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mokhtarabadi.secureapi.server.model.UserModel;
import com.mokhtarabadi.secureapi.server.model.base.BaseResponse;
import com.mokhtarabadi.secureapi.server.model.response.CreateUserResponse;
import com.mokhtarabadi.secureapi.server.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

@RequiredArgsConstructor
public class UserController {

    @NonNull private JsonMapper jsonMapper;

    @NonNull private UserRepository userRepository;

    private int parseId(Request request) {
        return Integer.parseInt(request.params(":id"));
    }

    @SneakyThrows
    public CreateUserResponse createUser(Request request, Response response) {
        val object = jsonMapper.readValue(request.attribute("data").toString(), JsonNode.class);
        val username = object.get("username").asText();
        val name = object.get("name").asText();

        val id = userRepository.createUser(username, name);

        val result = new CreateUserResponse();
        result.setId(id);

        if (id == 0) {
            response.status(HttpStatus.CONFLICT_409);
            result.setErrorMessage("user exists");
        } else {
            response.status(HttpStatus.CREATED_201);
            result.setSuccess(true);
        }

        return result;
    }

    public Object getUser(Request request, Response response) {
        val id = parseId(request);
        val user = userRepository.getUser(id);

        if (user == null) {
            response.status(HttpStatus.NOT_FOUND_404);
            return new BaseResponse();
        }

        return user;
    }

    @SneakyThrows
    public BaseResponse updateUser(Request request, Response response) {
        val object = jsonMapper.readValue(request.attribute("data").toString(), JsonNode.class);
        val name = object.get("name").asText();
        val id = parseId(request);
        val result = new BaseResponse();

        if (!userRepository.updateUser(id, name)) {
            response.status(HttpStatus.NOT_FOUND_404);
            result.setErrorMessage("user not found");
        } else {
            response.status(HttpStatus.NO_CONTENT_204);
            result.setSuccess(true);
        }

        return result;
    }

    @SneakyThrows
    public BaseResponse deleteUser(Request request, Response response) {
        val id = parseId(request);
        val result = new BaseResponse();

        if (!userRepository.deleteUser(id)) {
            response.status(HttpStatus.NOT_FOUND_404);
            result.setErrorMessage("user not found");
        } else {
            response.status(HttpStatus.NO_CONTENT_204);
            result.setSuccess(true);
        }

        return result;
    }

    public ArrayList<UserModel> getAllUsers(Request request, Response response) {
        return userRepository.getAllUsers();
    }
}
