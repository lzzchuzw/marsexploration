package com.exploration;

import com.exploration.config.ConstantConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarsexplorationApplicationTests {
    
    @Autowired
    ConstantConfig constantConfig;

    @Test
    void contextLoads() {
        System.out.println(constantConfig);
    }



}
