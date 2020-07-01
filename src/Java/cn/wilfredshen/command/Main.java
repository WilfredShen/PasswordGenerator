package cn.wilfredshen.command;

import cn.wilfredshen.exception.UnexpectedCharacterException;
import cn.wilfredshen.generator.Generator;
import cn.wilfredshen.generator.PasswordOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addRequiredOption("k", "key", true,
                "指定密码根。\n|- 如果指定 -f 参数，则将以文件内容作为 key。\n|--------");
        options.addRequiredOption("m", "mode", true,
                "启用指定的字符集。\n|- 1: 大写字母集；\n|- 2: 小写字母集；\n|- 4: 数字集。\n|- 可组合使用，例：-m 3 表示同时启用大写字母集和小写字母集。\n|--------");
        options.addRequiredOption("l", "len", true,
                "指定密码长度。\n|- 生成较长密码时请使用较长的 key，以提高安全性。\n|--------");
        options.addOption("h", "help", false,
                "[可选]获取帮助。\n|--------");
        options.addOption("f", "file", false,
                "[可选]指定 key 存储的文件\n|- 以 -k 指定的字符串作为文件名。\n|--------");
        options.addOption("E", "enable", true,
                "[可选]启用指定的字符。\n|- 允许存在重复字符。\n|- 例：-m 1 -E 3A55e，则字符集为 35A-Ze。\n|--------");
        options.addOption("D", "disable", true,
                "[可选]禁用指定的字符。\n|- 可以禁用 -E 启用的字符。\n|- 允许禁用集合中不存在的字符。\n|- 例：-m 1 -D Ae3A，则字符集为 B-Z。\n|--------");

        CommandLine cli;
        CommandLineParser clip = new DefaultParser();
        HelpFormatter hf = new HelpFormatter();

        // 解析命令行
        try {
            cli = clip.parse(options, args);
        } catch (ParseException e) {
            hf.printHelp("错误的命令行！\n合法的字符集为 ASCII 中 0x20 ~ 0x7E，即空格和所有可见字符。\n-k, -m, -l 参数是必须的。\n--------------------------------", options);
            return;
        }
        // 解析是否需要 help
        if (cli.hasOption("h")) {
            hf.printHelp("合法的字符集为 ASCII 中 0x20 ~ 0x7E，即空格和所有可见字符。\n-k, -m, -l 参数是必须的。\n--------------------------------", options);
            return;
        }
        // 解析 key
        PasswordOptions passwordOptions = new PasswordOptions();
        String path = cli.getOptionValue("k");
        byte[] key;
        if (cli.hasOption("f")) {
            File file = new File(path);
            try {
                FileInputStream fis = new FileInputStream(file);
                key = new byte[Math.toIntExact(file.length())];
                fis.read(key);
            } catch (FileNotFoundException e) {
                hf.printHelp("打开文件失败！请检查文件名。\n--------------------------------", options);
                return;
            } catch (IOException e) {
                hf.printHelp("文件读取失败！请检查文件是否被占用。\n--------------------------------", options);
                return;
            }
        } else {
            key = path.getBytes();
        }
        passwordOptions.setKey(key);
        // 解析 mode
        try {
            passwordOptions.setMode(Integer.parseInt(cli.getOptionValue("m")));
        } catch (NumberFormatException e) {
            hf.printHelp("mode 解析错误！请输入 0~7 之间的整数。\n--------------------------------", options);
            return;
        }
        // 解析 length
        try {
            passwordOptions.setLength(Integer.parseInt(cli.getOptionValue("l")));
            if (passwordOptions.getLength() <= 0) {
                hf.printHelp("length 解析错误！请输入合法的正整数。\n--------------------------------", options);
                return;
            }
        } catch (NumberFormatException e) {
            hf.printHelp("length 解析错误！请输入合法的正整数。\n--------------------------------", options);
            return;
        }
        // 解析 enabledCharacters
        if (cli.hasOption("E")) {
            passwordOptions.setEnabledCharacters(cli.getOptionValue("E"));
        }
        // 解析 disabledCharacters
        if (cli.hasOption("D")) {
            passwordOptions.setEnabledCharacters(cli.getOptionValue("D"));
        }
        try {
            String password = Generator.generate(passwordOptions);
            System.out.println(password);
        } catch (UnexpectedCharacterException e) {
            hf.printHelp("存在非法字符！请检查输入。\n--------------------------------", options);
        }
    }
}
