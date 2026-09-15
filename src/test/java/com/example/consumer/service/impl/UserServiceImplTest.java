package com.example.consumer.service.impl;

import com.example.consumer.model.User;
import com.example.consumer.model.dto.UserRequest;
import com.example.consumer.service.consumer.UserConsumers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * La gran ventaja del Consumer: testear sin mocks.
 * Inyectamos un Consumer que solo acumula en una lista.
 */
class UserServiceImplTest {

    @Test
    void registerDebeEjecutarElConsumerInyectado() {
        UserServiceImpl service = new UserServiceImpl(new UserConsumers());
        List<User> capturados = new ArrayList<>();

        User user = service.register(
                new UserRequest("Juan", "juan@email.com"),
                capturados::add
        );

        assertNotNull(user.getId());
        assertEquals("ACTIVE", user.getStatus());
        assertEquals(1, capturados.size());
        assertEquals("Juan", capturados.get(0).getName());
    }

    @Test
    void registerDebePermitirEncadenarVariosConsumers() {
        UserServiceImpl service = new UserServiceImpl(new UserConsumers());
        List<String> pasos = new ArrayList<>();

        Consumer<User> pipeline = ((Consumer<User>) u -> pasos.add("validar"))
                .andThen(u -> pasos.add("guardar"))
                .andThen(u -> pasos.add("notificar"));

        service.register(new UserRequest("Maria", "maria@email.com"), pipeline);

        assertEquals(List.of("validar", "guardar", "notificar"), pasos);
    }
}
