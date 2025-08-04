package com.cicd.generator.lib;

import com.cicd.generator.CiCdExecutionResult;
import com.cicd.generator.CiCdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class CiCdGeneratorIT {

    @Test
    void testDockerfileCreation() {
        log.info("testDockerfileCreation");

        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute();

        if (result.success()) {
            log.info("testDockerfileCreation success");
        } else {
            log.info("testDockerfileCreation failed");
        }
    }
}