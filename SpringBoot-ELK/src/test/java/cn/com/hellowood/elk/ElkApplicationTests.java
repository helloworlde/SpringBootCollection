package cn.com.hellowood.elk;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElkApplicationTests {

    private final Logger logger = Logger.getLogger(getClass());

    @Test
    public void contextLoads() {
    }

    @Test
    public void generateLogs() {
        for (int i = 0; i < 20; i++) {
            logger.info("log " + Math.random());
        }
    }

}
