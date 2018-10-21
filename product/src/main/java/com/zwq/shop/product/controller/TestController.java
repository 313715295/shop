package com.zwq.shop.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by zwq on 2018/10/21
 */
@RestController
public class TestController {
    @GetMapping("test")
    public String test() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "product";
    }
}
