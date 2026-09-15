package com.example.consumer.controller;

import com.example.consumer.model.User;
import com.example.consumer.model.dto.UserRequest;
import com.example.consumer.model.dto.UserResponse;
import com.example.consumer.service.UserService;
import com.example.consumer.service.consumer.UserConsumers;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Consumer;

/**
 * CONTROLADOR - capa REST.
 *
 * Su unica responsabilidad es traducir HTTP <-> dominio.
 * Ademas, aqui se ve como el CLIENTE decide que Consumer usar.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserConsumers userConsumers;

    public UserController(UserService userService, UserConsumers userConsumers) {
        this.userService = userService;
        this.userConsumers = userConsumers;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    /**
     * Aqui se ve el desacoplamiento real: el controlador COMPONE el Consumer
     * segun los parametros de la peticion y el servicio lo ejecuta a ciegas.
     */
    @PostMapping("/custom")
    public ResponseEntity<UserResponse> createCustom(
            @Valid @RequestBody UserRequest request,
            @RequestParam(defaultValue = "false") boolean notify,
            @RequestParam(defaultValue = "false") boolean silent) {

        Consumer<User> pipeline;

        if (silent) {
            pipeline = userConsumers.noOp();
        } else {
            pipeline = userConsumers.audit()
                    .andThen(userConsumers.sendWelcomeEmail());

            if (notify) {
                pipeline = pipeline.andThen(userConsumers.pushNotification());
            }
        }

        User user = userService.register(request, pipeline);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(UserResponse.from(userService.findById(id)));
    }
}
