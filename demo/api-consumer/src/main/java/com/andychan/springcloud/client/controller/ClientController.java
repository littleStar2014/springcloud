package com.andychan.springcloud.client.controller;

import com.andychan.springcloud.client.service.FeignService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class ClientController {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeignService feignService;

    /**
     * 从所有服务中选择一个服务（轮询）
     */
    @RequestMapping("/discover")
    public Object discover() {
        return loadBalancer.choose("micro-service").getUri().toString();
    }

    /**
     * 获取所有服务
     */
    @RequestMapping("/services")
    public Object services() {
        return discoveryClient.getInstances("micro-service");
    }


    @GetMapping(value = "/ribbon-say")
    public String ribbonCall() {
        return restTemplate.getForEntity("http://micro-service/say", String.class).getBody();
    }

    @GetMapping(value = "/feign-say")
    public String feignCall() {
        String message = feignService.say();
        return message;
    }

    @GetMapping(value = "/feign-back-say")
    public String feignCallBack() {
        String message = feignService.say();
        if (message.contains("andychan")) {
            throw new HystrixBadRequestException(message);
        }
        return message;
    }
}
