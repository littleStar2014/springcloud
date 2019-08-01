package com.andychan.springcloud.client.impl;

import com.andychan.springcloud.client.service.FeignService;
import org.springframework.stereotype.Component;

@Component
public class FeignServiceFallback implements FeignService {
    @Override
    public String say() {
        return "I'm a backup!";
    }
}
