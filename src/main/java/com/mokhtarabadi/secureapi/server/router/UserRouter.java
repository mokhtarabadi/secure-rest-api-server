package com.mokhtarabadi.secureapi.server.router;

import com.mokhtarabadi.secureapi.server.Main;
import com.mokhtarabadi.secureapi.server.controller.UserController;
import com.mokhtarabadi.secureapi.server.transformer.JacksonTransformer;
import com.mokhtarabadi.secureapi.server.utility.EncryptionUtility;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static spark.Spark.*;

@RequiredArgsConstructor
public class UserRouter {

    @NonNull private UserController userController;
    @NonNull private JacksonTransformer jacksonTransformer;

    private void publicRoutes() {
        get("/users/all", userController::getAllUsers, jacksonTransformer);
    }

    private void privateRoutes() {
        path(
                "/users",
                () -> {
                    before("/*", EncryptionUtility::checkSignature);

                    post("/", userController::createUser, jacksonTransformer);
                    put("/:id", userController::updateUser, jacksonTransformer);
                    get("/:id", userController::getUser, jacksonTransformer);
                    delete("/:id", userController::deleteUser, jacksonTransformer);
                });
    }

    public void registerRoutes(Main.ApiVersion apiVersion) {
        publicRoutes();
        privateRoutes();

        if (apiVersion == Main.ApiVersion.V2) {
            // register version 2 routes here

        }
    }
}
