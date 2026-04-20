package com.aprendemosya.aprendemosya_api.domain.sala.websocket;

import com.aprendemosya.aprendemosya_api.domain.sala.dto.SalaMensaje;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SalaWebSocketController {

    @MessageMapping("/mensaje")
    @SendTo("/topic/sala")
    public SalaMensaje enviarMensaje(SalaMensaje mensaje) {
        return mensaje;
    }
}
