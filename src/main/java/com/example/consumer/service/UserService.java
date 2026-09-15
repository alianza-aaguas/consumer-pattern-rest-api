package com.example.consumer.service;

import com.example.consumer.model.User;
import com.example.consumer.model.dto.UserRequest;

import java.util.List;
import java.util.function.Consumer;

/**
 * SERVICIO (interfaz) - el contrato.
 *
 * Fijate en la firma: recibe un Consumer<User> como parametro.
 * El servicio NO decide que pasa despues del registro; solo garantiza
 * que ejecutara el Consumer que le entreguen.
 */
public interface UserService {

    User register(UserRequest request, Consumer<User> postRegister);

    User register(UserRequest request);

    List<User> findAll();

    User findById(String id);
}
