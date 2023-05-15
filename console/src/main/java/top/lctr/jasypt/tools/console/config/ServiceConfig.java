package top.lctr.jasypt.tools.console.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * 服务配置
 *
 * @author LCTR
 * @date 2023-05-15
 */
@Primary
@Component
@ConfigurationProperties("service")
@Data
public class ServiceConfig {
    /**
     * 服务标识
     */
    private String key;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 版本号
     */
    private String version;

    /**
     * jar包存储目录
     */
    private String jarDir;

    public String getJarDir() {
        return Paths.get(jarDir)
                    .toAbsolutePath()
                    .toString();
    }
}
