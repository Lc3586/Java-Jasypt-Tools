package top.lctr.jasypt.tools.console.business.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import project.extension.console.extension.CommandUtils;
import project.extension.cryptography.JasyptUtils;
import project.extension.file.FileExtension;
import project.extension.resource.ScanExtension;
import top.lctr.jasypt.tools.console.config.ServiceConfig;
import top.lctr.jasypt.tools.console.dto.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * 命令处理模块
 *
 * @author LCTR
 * @date 2023-05-15
 */
@Component
public class CommandHandler {
    public CommandHandler(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    private final ServiceConfig serviceConfig;

    private static final String name = "命令处理模块";

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    /**
     * 控制台日志
     */
    private static final Logger consoleLogger = LoggerFactory.getLogger("console-log");

    private static CompletableFuture<Void> task = null;

    private static String helpFile;

    private static String password;

    /**
     * 启动
     */
    public void start(String command) {
        extractFile();

        task = CompletableFuture.runAsync(() -> processing(command),
                                          Executors.newSingleThreadExecutor());

        logger.info(String.format("%s：已启动",
                                  name));
    }

    /**
     * 关闭
     */
    public void shutdown() {
        if (task != null)
            task.cancel(true);

        logger.info(String.format("%s：已关闭",
                                  name));
    }

    /**
     * 提取文件
     */
    private void extractFile() {
        try {
            if (!FileExtension.isRunInJar()) {
                //本地调试
                helpFile = Paths.get(ScanExtension.getResource("classpath:help.txt")
                                                  .getURI())
                                .toAbsolutePath()
                                .toString();

                return;
            }

            helpFile = Paths.get("help.txt")
                            .toAbsolutePath()
                            .toString();
            FileExtension.extractFileFromJar("BOOT-INF/classes/help.txt",
                                             helpFile,
                                             true);
        } catch (Exception ex) {
            logger.error(String.format("%s：提取文件时发生异常",
                                       name),
                         ex);
            consoleLogger.error(String.format("%s：提取文件时发生异常",
                                              name),
                                ex);
        }
    }

    /**
     * 获取密码
     */
    private String getPassword() {
        if (password != null)
            return password;

        return JasyptUtils.getPassword();
    }

    /**
     * 命令
     *
     * @param firstCommand 初始命令
     */
    private void processing(String firstCommand) {
        boolean first = true;

        while (true) {
            try {
                String commandString;
                if (first) {
                    commandString = StringUtils.hasText(firstCommand)
                                    ? firstCommand
                                    : Command.帮助信息.toString();
                    first = false;
                } else {
                    consoleLogger.info("请输入命令");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    commandString = reader.readLine();
                }

                String[] commands = CommandUtils.getArgs(commandString);

                if (commands.length < 1)
                    throw new Exception(String.format("命令格式有误：%s，正确的格式为（命令和参数之间使用空格分隔）：命令 参数1 参数2 参数3",
                                                      commandString));

                Command command = Command.toEnum(commands[0]);

                switch (command) {
                    case 帮助信息:
                        List<String> helpContent = Files.readAllLines(Paths.get(helpFile));
                        consoleLogger.info(String.join("\r\n",
                                                       helpContent));
                        break;
                    case 版本信息:
                        consoleLogger.info(String.format("%s\r\n版本：%s\r\n标识：%s",
                                                         serviceConfig.getName(),
                                                         serviceConfig.getVersion(),
                                                         serviceConfig.getKey()));
                        break;
                    case 设置默认密钥:
                        if (commands.length < 2)
                            throw new Exception(String.format("命令缺少一个参数: %s,正确的格式为(命令和参数之间使用空格分隔): password \"密钥值\"",
                                                              commandString));

                        password = commands[1];

                        consoleLogger.info(String.format("默认加密密钥已更改为: %s",
                                                         password));
                        break;
                    case 查看默认密钥:
                        consoleLogger.info(String.format("当前的默认加密密钥为: %s",
                                                         getPassword()));
                        break;
                    case 加密:
                        if (commands.length < 2)
                            throw new Exception(String.format("命令缺少一个参数: %s,正确的格式为(命令和参数之间使用空格分隔): encrypt \"明文内容\"",
                                                              commandString));

                        String password4Encrypt = getPassword();
                        if (commands.length > 2 && StringUtils.hasText(commands[2]))
                            password4Encrypt = commands[2];

                        String cipherData = JasyptUtils.encrypt(commands[1],
                                                                password4Encrypt);

                        if (commands.length > 3 && "e".equals(commands[3]))
                            cipherData = String.format("ENC(%s)",
                                                       cipherData);

                        consoleLogger.info(cipherData);
                        break;
                    case 解密:
                        if (commands.length < 2)
                            throw new Exception(String.format("命令缺少一个参数: %s,正确的格式为(命令和参数之间使用空格分隔): decrypt \"密文内容\"",
                                                              commandString));

                        String password4Decrypt = getPassword();
                        if (commands.length > 2 && StringUtils.hasText(commands[2]))
                            password4Decrypt = commands[2];

                        String plainData = JasyptUtils.decrypt(commands[1],
                                                               password4Decrypt);

                        consoleLogger.info(plainData);
                        break;
                    case 校验:
                        if (commands.length < 3)
                            throw new Exception(String.format("命令缺少一个参数: %s,正确的格式为(命令和参数之间使用空格分隔): check \"明文内容\" \"密文内容\"",
                                                              commandString));

                        String password4Check = getPassword();
                        if (commands.length > 3 && StringUtils.hasText(commands[3]))
                            password4Check = commands[3];

                        try {
                            String plainData4Check = JasyptUtils.decrypt(commands[2],
                                                                         password4Check);

                            if (plainData4Check.equals(commands[1]))
                                consoleLogger.info("正确");
                            else
                                consoleLogger.error("密文和明文不匹配");
                        } catch (Exception ignore) {
                            consoleLogger.error("密文格式错误");
                        }
                        break;
                    case 退出:
                        consoleLogger.info(String.format("%s：即将退出",
                                                         name));
                        System.exit(0);
                        break;
                    default:
                        consoleLogger.warn(String.format("%s：不支持的命令：%s",
                                                         name,
                                                         commandString));
                        break;
                }
            } catch (Exception ex) {
                logger.error(String.format("%s：处理时发生异常",
                                           name),
                             ex);
                consoleLogger.error(String.format("%s：处理时发生异常",
                                                  name),
                                    ex);
            }
        }
    }
}
