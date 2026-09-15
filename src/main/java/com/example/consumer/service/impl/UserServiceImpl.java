package com.example.consumer.service.impl;

import com.example.consumer.model.User;
import com.example.consumer.model.dto.UserRequest;
import com.example.consumer.service.UserService;
import com.example.consumer.service.consumer.UserConsumers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * IMPLEMENTACION del servicio.
 *
 * Punto clave: la clase se encarga SOLO de la logica de registro.
 * Lo que ocurre despues (email, auditoria, cache, metricas...) llega
 * inyectado como Consumer<User> y se dispara con .accept(user).
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Map<String, User> repository = new ConcurrentHashMap<>();

    private final Consumer<User> defaultPostRegister;

    public UserServiceImpl(UserConsumers userConsumers) {
        this.defaultPostRegister = userConsumers.audit()
                .andThen(userConsumers.sendWelcomeEmail())
                .andThen(userConsumers.publishMetrics());
    }

    @Override
    public User register(UserRequest request, Consumer<User> postRegister) {
        User user = new User(UUID.randomUUID().toString(), request.getName(), request.getEmail());
        user.setStatus("ACTIVE");
        user.setRegisteredAt(LocalDateTime.now());

        repository.put(user.getId(), user);
        log.info("Usuario registrado: {}", user);

        // INYECCION DE COMPORTAMIENTO:
        // el servicio NO sabe que hace este Consumer, solo lo ejecuta.
        postRegister.accept(user);

        return user;
    }

    @Override
    public User register(UserRequest request) {
        return register(request, defaultPostRegister);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public User findById(String id) {
        User user = repository.get(id);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        }
        return user;
    }
}
