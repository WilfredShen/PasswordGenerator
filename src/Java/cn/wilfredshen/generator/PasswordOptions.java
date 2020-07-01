package cn.wilfredshen.generator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class PasswordOptions {

    public static final int UCL = 0b0001;// 启用大写字母
    public static final int LCL = 0b0010;// 启用小写字母
    public static final int NUM = 0b0100;// 启用数字
    public static final int ALL = 0b0111;// 全部启用

    private int mode;// 模式选择: [UCL, LCL, NUM] 的组合
    private byte[] key;// 密码根
    private int length;// 密码长度
    private String enabledCharacters = "";// 额外启用的字符
    private String disabledCharacters = "";// 禁用的字符（会覆盖额外启用的字符）

    /**
     * 空构造函数
     */
    public PasswordOptions() {
    }

    /**
     * 不指定额外的字符以及禁用的字符。
     *
     * @param mode   模式：[UCL, LCL, NUM] 的组合。
     * @param key    密码根：将根据密码根生成密码。
     * @param length 密码长度：生成指定长度的密码。
     */
    public PasswordOptions(byte @NotNull [] key, int mode, int length) {
        this.key = key.clone();
        this.mode = mode;
        this.length = length;
    }

    /**
     * 指定额外的字符以及禁用的字符。额外的字符必须在 ASCII 码表中。所有字符除空格外必须为可见字符。
     *
     * @param mode               模式：[UCL, LCL, NUM] 的组合。
     * @param key                密码根：将根据密码根生成密码。
     * @param length             密码长度：生成指定长度的密码。
     * @param enabledCharacters  启用额外字符：启用除 mode 指定字符以外的字符。允许存在重复字符。
     * @param disabledCharacters 禁用字符：禁用指定字符。会禁用 enabledCharacters 指定的字符。允许存在重复或不在集合中的字符。
     */
    public PasswordOptions(byte @NotNull [] key, int mode, int length, String enabledCharacters, String disabledCharacters) {
        this.key = key.clone();
        this.mode = mode;
        this.length = length;
        this.enabledCharacters = enabledCharacters;
        this.disabledCharacters = disabledCharacters;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte @NotNull [] key) {
        this.key = key.clone();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getEnabledCharacters() {
        return enabledCharacters;
    }

    public void setEnabledCharacters(String enabledCharacters) {
        this.enabledCharacters = enabledCharacters;
    }

    public String getDisabledCharacters() {
        return disabledCharacters;
    }

    public void setDisabledCharacters(String disabledCharacters) {
        this.disabledCharacters = disabledCharacters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordOptions)) return false;
        PasswordOptions that = (PasswordOptions) o;
        return mode == that.mode &&
                length == that.length &&
                Arrays.equals(key, that.key) &&
                enabledCharacters.equals(that.enabledCharacters) &&
                disabledCharacters.equals(that.disabledCharacters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, key, length, enabledCharacters, disabledCharacters);
    }
}
