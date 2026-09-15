package com.example.consumer.service.consumer;

import com.example.consumer.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Catalogo de Consumers reutilizables.
 *
 * Cada metodo devuelve un Consumer<User> que encapsula UNA responsabilidad.
 * Se pueden combinar libremente con andThen() sin tocar el servicio.
 */
@Component
public class UserConsumers {

    private static final Logger log = LoggerFactory.getLogger(UserConsumers.class);

    public Consumer<User> audit() {
        return user -> log.info("[AUDIT] Usuario creado id={} name={}", user.getId(), user.getName());
    }

    public Consumer<User> sendWelcomeEmail() {
        return user -> log.info("[EMAIL] Bienvenida enviada a {}", user.getEmail());
    }

    public Consumer<User> publishMetrics() {
        return user -> log.info("[METRICS] user.registered status={}", user.getStatus());
    }

    public Consumer<User> pushNotification() {
        return user -> log.info("[PUSH] Notificacion enviada a {}", user.getName());
    }

    public Consumer<User> noOp() {
        return user -> {
            // intencionalmente vacio
        };
    }
}
