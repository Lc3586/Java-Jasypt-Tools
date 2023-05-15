package top.lctr.jasypt.tools.console;

import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * jasypt工具控制台
 *
 * @author LCTR
 * @date 2023-05-12
 */
@SpringBootApplication(scanBasePackages = {
        "project.extension",
        "top.lctr.jasypt.tools.console"
})
public class ConsoleApplication {
    public static void main(String[] args) {
        System.out.printf("\033[32mConsoleApplication.main\033[0m \033[33margs\033[0m : %s\r\n%n",
                          String.join(" ",
                                      args));

        System.out.printf("\033[34mslf4j\033[0m ：%s%n",
                          LoggerFactory.class.getResource(""));
        System.out.printf("\033[34mlogback\033[0m ：%s%n",
                          ConsoleAppender.class.getResource(""));

        ConfigurableApplicationContext application = SpringApplication.run(ConsoleApplication.class,
                                                                           args);
    }
}