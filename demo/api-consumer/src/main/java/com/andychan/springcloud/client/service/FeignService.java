package com.andychan.springcloud.client.service;

import com.andychan.springcloud.client.impl.FeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "api-provider", fallback = FeignServiceFallback.class)
@Component
public interface FeignService {

    @RequestMapping(value = "/say")
    public String say();

}
