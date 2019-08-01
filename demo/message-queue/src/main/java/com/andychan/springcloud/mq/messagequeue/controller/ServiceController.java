package com.andychan.springcloud.mq.messagequeue.controller;

import com.andychan.springcloud.mq.messagequeue.provider.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class ServiceController {

    @Autowired
    KafkaSender kafkaSender;

    @GetMapping("/sendMessage")
    public String say(HttpServletRequest request) {
        kafkaSender.send();
        return "Just send a message to kafka topic";
    }
}
