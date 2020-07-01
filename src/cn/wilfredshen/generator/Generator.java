package cn.wilfredshen.generator;

import cn.wilfredshen.exception.UnexpectedCharacterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class Generator {

    private static final int[] factors = {2, 5, 11, 53, 101, 503, 1009, 5003,
            10007, 50021, 100003, 500009, 1000003, 5000011, 10000019, 50000017};

    /**
     * 根据传入的 options 生成密码。采用散列算法，相同的 options 必定得到相同的结果。
     *
     * @param options 指定密码根、长度和字符集。
     * @return 返回 options 指定密码根、长度和字符集的密码。
     */
    public static @Nullable String generate(@NotNull PasswordOptions options) throws UnexpectedCharacterException {
        char[] map = genMap(options);// 散列 map
        int length = options.getLength();// 密码长度
        byte[] key = options.getKey();// 密码根
        StringBuilder sb = new StringBuilder(length);// 存储密码
        try {
            byte[] salted = addSalt(key, length);
            int size = map.length;
            long offset;// 密码中某一位对应到 map 的偏移量
            for (int i = 0; i < length; i++) {
                byte[] tmp = new byte[16];
                System.arraycopy(salted, i * 16, tmp, 0, 8);
                offset = 0;
                for (int j = 0; j < 16; j++) {
                    offset += (long) factors[j] * ((tmp[j] + 256) % 256);
                }
                sb.append(map[Math.toIntExact(offset % size)]);
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return sb.toString();
    }

    private static byte[] addSalt(@NotNull byte[] key, int length) throws NoSuchAlgorithmException {
        byte[] tmp1, tmp2, result;
        MessageDigest md5 = MessageDigest.getInstance("md5");
        // 双重 md5
        result = md5.digest(key);
        result = md5.digest(result);
        // 对密钥进行拓展
        int l = 1;
        while (length > l) {
            tmp1 = result;
            l *= 2;
            result = new byte[l * 16];
            for (int i = 0; i < l; i++) {
                tmp2 = new byte[8];
                System.arraycopy(tmp1, i * 8, tmp2, 0, 8);
                tmp2 = md5.digest(tmp2);
                System.arraycopy(tmp2, 0, result, i * 16, 16);
            }
        }
        return result;
    }

    /**
     * 判断字符类型，由于可能存在辅助字符，长度超过 Java 中的 char 型，故采用 int 类型传参。
     *
     * @param c 表示一个代码点。
     * @return 返回字符类型。
     */
    private static CharEnum charType(int c) {
        // 为大写字母
        if (c >= 'A' && c <= 'Z')
            return CharEnum.UPC;
        // 为小写字母
        if (c >= 'a' && c <= 'z')
            return CharEnum.LWC;
        // 为数字
        if (c >= '0' && c <= '9')
            return CharEnum.NUM;
        // 为特殊字符
        if ((c >= ' ' && c < '0') || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || (c > 'z' && c <= '~'))
            return CharEnum.SPE;
        // 不允许的字符
        return CharEnum.UNE;
    }

    /**
     * 根据 options 生成散列 map，用于生成密码。
     *
     * @param options 指定密码的长度和字符集（该方法只用到了字符集）。
     * @return 返回散列 map，类型为字符数组。
     * @throws UnexpectedCharacterException 字符集中如果存在不允许的字符，将抛出异常。
     *                                      允许的字符集为 ASCII 中 0x20 ~ 0x7E，除空格外均为可见字符
     */
    private static char[] genMap(@NotNull PasswordOptions options) throws UnexpectedCharacterException {
        Set<Character> upperSet = new HashSet<>(26);// 大写字母集
        Set<Character> lowerSet = new HashSet<>(26);// 小写字母集
        Set<Character> numSet = new HashSet<>(10);// 数字集
        Set<Character> speSet = new HashSet<>(33);// 特殊字符集

        int mode = options.getMode();

        // 如果启用大写字母
        if ((mode & PasswordOptions.UCL) != 0)
            for (int i = 0; i < 26; i++)
                upperSet.add((char) ('A' + i));
        // 如果启用小写字母
        if ((mode & PasswordOptions.LCL) != 0)
            for (int i = 0; i < 26; i++)
                lowerSet.add((char) ('a' + i));
        // 如果启用数字
        if ((mode & PasswordOptions.NUM) != 0)
            for (int i = 0; i < 10; i++)
                lowerSet.add((char) ('0' + i));
        // 启用额外字符
        String string = options.getEnabledCharacters();
        if (string.length() != 0) {
            int count = string.codePointCount(0, string.length());
            for (int i = 0; i < count; i++) {
                int c = string.codePointAt(i);
                switch (Generator.charType(c)) {
                    case UPC:
                        upperSet.add((char) c);
                        break;
                    case LWC:
                        lowerSet.add((char) c);
                        break;
                    case NUM:
                        numSet.add((char) c);
                        break;
                    case SPE:
                        speSet.add((char) c);
                        break;
                    case UNE:
                    default:
                        throw new UnexpectedCharacterException();
                }
            }
        }
        // 禁用字符
        string = options.getDisabledCharacters();
        if (string.length() != 0) {
            int count = string.codePointCount(0, string.length());
            for (int i = 0; i < count; i++) {
                int c = string.codePointAt(i);
                switch (Generator.charType(c)) {
                    case UPC:
                        upperSet.remove((char) c);
                        break;
                    case LWC:
                        lowerSet.remove((char) c);
                        break;
                    case NUM:
                        numSet.remove((char) c);
                        break;
                    case SPE:
                        speSet.remove((char) c);
                        break;
                    case UNE:
                    default:
                        throw new UnexpectedCharacterException();
                }
            }
        }

        // 生成字符序列
        List<Character> upperList = new ArrayList<>(upperSet);
        List<Character> lowerList = new ArrayList<>(lowerSet);
        List<Character> numList = new ArrayList<>(numSet);
        List<Character> speList = new ArrayList<>(speSet);
        StringBuilder sb = new StringBuilder(upperList.size() + lowerList.size() + numList.size() + speList.size());
        ListIterator<Character> upperIter = upperList.listIterator();
        ListIterator<Character> lowerIter = lowerList.listIterator();
        ListIterator<Character> numIter = numList.listIterator();
        ListIterator<Character> speIter = speList.listIterator();
        int flag = 0b1111;
        while (flag != 0) {
            // 大写字母迭代
            if (upperIter.hasNext())
                sb.append(upperIter.next());
            else
                flag &= 0b1110;
            // 小写字母迭代
            if (lowerIter.hasNext())
                sb.append(lowerIter.next());
            else
                flag &= 0b1101;
            // 数字迭代
            if (numIter.hasNext())
                sb.append(numIter.next());
            else
                flag &= 0b1011;
            // 特殊字符迭代
            if (speIter.hasNext())
                sb.append(speIter.next());
            else
                flag &= 0b0111;
        }

        // 返回字典
        return sb.toString().toCharArray();
    }

    /**
     * 字符类型枚举。
     * UPC, LWC, NUM, SPE, UNE 分别表示大写字母、小写字母、数字字符、特殊字符、其他字符
     */
    private enum CharEnum {
        UPC, LWC, NUM, SPE, UNE
    }
}
