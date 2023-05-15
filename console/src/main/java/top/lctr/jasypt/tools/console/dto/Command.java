package top.lctr.jasypt.tools.console.dto;

import java.util.Arrays;
import java.util.Optional;

/**
 * 命令
 *
 * @author LCTR
 * @date 2023-05-15
 */
public enum Command {
    /**
     * 退出
     */
    退出(-1,
       "exit"),
    /**
     * 帮助信息
     */
    帮助信息(0,
         "help"),
    /**
     * 版本信息
     */
    版本信息(1,
         "version"),
    /**
     * 设置默认密钥
     */
    设置默认密钥(2,
           "set-password"),
    /**
     * 查看默认密钥
     */
    查看默认密钥(3,
           "get-password"),
    /**
     * 加密
     */
    加密(4,
       "encrypt"),
    /**
     * 解密
     */
    解密(5,
       "decrypt"),
    /**
     * 校验
     */
    校验(6,
       "check");

    /**
     * @param index 索引
     * @param value 值
     */
    Command(int index,
            String value) {
        this.index = index;
        this.value = value;
    }

    /**
     * 索引
     */
    final int index;

    /**
     * 值
     */
    final String value;

    /**
     * 获取索引
     *
     * @return 索引
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * 获取字符串
     *
     * @return 值
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * 获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static Command toEnum(String value)
            throws
            IllegalArgumentException {
        Optional<Command> find = Arrays.stream(Command.values())
                                       .filter(x -> x.value.equals(value))
                                       .findFirst();
        if (!find.isPresent())
            throw new IllegalArgumentException(String.format("未找到符合%s此值的Command枚举中",
                                                             value));
        return find.get();
    }

    /**
     * 获取枚举
     *
     * @param index 索引
     * @return 枚举
     */
    public static Command toEnum(int index)
            throws
            IllegalArgumentException {
        for (Command value : Command.values()) {
            if (value.getIndex() == index)
                return value;
        }

        throw new IllegalArgumentException(String.format("指定索引%s无效",
                                                         index));
    }
}
