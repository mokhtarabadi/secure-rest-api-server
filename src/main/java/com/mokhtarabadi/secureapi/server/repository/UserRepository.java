package com.mokhtarabadi.secureapi.server.repository;

import com.mokhtarabadi.secureapi.server.model.UserModel;
import lombok.val;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepository {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    private static final ConcurrentHashMap<Integer, UserModel> DATASET = new ConcurrentHashMap<>();

    private boolean isExists(String username) {
        return DATASET.values().stream()
                .anyMatch(userModel -> userModel.getUsername().equals(username));
    }

    private int generateId() {
        return ID_COUNTER.incrementAndGet();
    }

    public int createUser(String username, String name) {
        if (isExists(username)) {
            return 0;
        }

        val user = new UserModel(generateId(), username, name);
        DATASET.put(user.getId(), user);

        return user.getId();
    }

    public UserModel getUser(int id) {
        return DATASET.get(id);
    }

    public boolean updateUser(int id, String name) {
        val user = getUser(id);
        if (user == null) {
            return false;
        }

        user.setName(name);
        DATASET.replace(user.getId(), user);

        return true;
    }

    public boolean deleteUser(int id) {
        val user = getUser(id);
        if (user == null) {
            return false;
        }

        DATASET.remove(user.getId());

        return true;
    }

    public ArrayList<UserModel> getAllUsers() {
        return new ArrayList<>(DATASET.values());
    }
}
