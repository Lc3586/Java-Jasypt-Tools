package top.lctr.jasypt.tools.console.configures;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.lctr.jasypt.tools.console.business.handler.CommandHandler;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * 服务配置
 *
 * @author LCTR
 * @date 2023-05-15
 */
@Component
@Order(value = 1)
public class ServiceConfigure
        implements ApplicationRunner {
    public ServiceConfigure(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    private final CommandHandler commandHandler;

    /**
     * 运行各个服务
     *
     * @param args 参数
     */
    @Override
    public void run(ApplicationArguments args) {
        CompletableFuture.runAsync(() -> commandHandler.start(String.join(" ",
                                                                          args.getSourceArgs())),
                                   Executors.newSingleThreadExecutor());
    }

    /**
     * 关闭各个服务
     */
    @PreDestroy
    public void shutDown() {
        try {
            commandHandler.shutdown();
        } catch (Exception ignore) {

        }
    }
}
