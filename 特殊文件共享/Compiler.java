/**
 * @Author       : NieFire planet_class@foxmail.com
 * @Date         : 2023-09-20 00:42:58
 * @LastEditors  : NieFire planet_class@foxmail.com
 * @LastEditTime : 2023-09-30 23:29:31
 * @FilePath     : \Computer-Graduate-Examination-Database\特殊文件共享\Compiler.java
 * @Description  : 编译原理第一次实验词法分析器ac代码java版本
 * @( ﾟ∀。)只要加满注释一切都会好起来的( ﾟ∀。)
 * @Copyright (c) 2023 by NieFire, All Rights Reserved. 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
    // 便于bw按顺序写入结果，毕竟用map储存最终输出顺序不一定
    static class Token {
        String category;
        String value;

        Token(String category, String value) {
            this.category = category;
            this.value = value;
        }
    }

    // 该部分静态变量来自教材所附编译器java语言版本中词法分析器部分的设计，在处理注释符号（//,/*）时发挥了重要作用

    // 符号所允许的最大长度
    private static final int MAX_ID_LEN = 10;

    // 数值所允许的最大位数
    private static final int MAX_NUM_LEN = 14;

    // 刚刚读入的字符
    private static char ch = ' ';

    // 当前读入的行
    private static String line;

    // 当前读入的行的字符数组
    private static char[] lineCharArr;

    // 当前行的长度
    public static int lineLength = 0;

    // 当前字符在当前行中的位置
    public static int charCounter = 0;

    // 标识符名称
    public static String id;

    // 数值
    public static int num;

    // 将词与词的种别码对应起来，使用map数据结构
    private static final Map<String, String> KEYWORD_OR_IDENT = new HashMap<String, String>();
    private static final Map<String, String> NUMBER = new HashMap<String, String>();
    private static final Map<String, String> OPERATOR = new HashMap<String, String>();

    private static BufferedReader br;
    private static BufferedWriter bw;

    // 所得结果键值对
    static List<Token> result = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        KEYWORD_OR_IDENT.put("main", "MAINTK");
        KEYWORD_OR_IDENT.put("const", "CONSTTK");
        KEYWORD_OR_IDENT.put("int", "INTTK");
        KEYWORD_OR_IDENT.put("break", "BREAKTK");
        KEYWORD_OR_IDENT.put("continue", "CONTINUETK");
        KEYWORD_OR_IDENT.put("if", "IFTK");
        KEYWORD_OR_IDENT.put("else", "ELSETK");
        KEYWORD_OR_IDENT.put("while", "WHILETK");
        KEYWORD_OR_IDENT.put("getint", "GETINTTK");
        KEYWORD_OR_IDENT.put("printf", "PRINTFTK");
        KEYWORD_OR_IDENT.put("return", "RETURNTK");
        KEYWORD_OR_IDENT.put("void", "VOIDTK");
        KEYWORD_OR_IDENT.put("Ident", "IDENFR");
        KEYWORD_OR_IDENT.put("FormatString", "STRCON");

        NUMBER.put("IntConst", "INTCON");

        OPERATOR.put("!", "NOT");
        OPERATOR.put("&&", "AND");
        OPERATOR.put("||", "OR");
        OPERATOR.put(">=", "GEQ");
        OPERATOR.put("==", "EQL");
        OPERATOR.put("<=", "LEQ");
        OPERATOR.put("+", "PLUS");
        OPERATOR.put("-", "MINU");
        OPERATOR.put("!=", "NEQ");
        OPERATOR.put("*", "MULT");
        OPERATOR.put("=", "ASSIGN");
        OPERATOR.put("/", "DIV");
        OPERATOR.put(";", "SEMICN");
        OPERATOR.put("%", "MOD");
        OPERATOR.put(",", "COMMA");
        OPERATOR.put("<", "LSS");
        OPERATOR.put("(", "LPARENT");
        OPERATOR.put(")", "RPARENT");
        OPERATOR.put(">", "GRE");
        OPERATOR.put("[", "LBRACK");
        OPERATOR.put("]", "RBRACK");
        OPERATOR.put("{", "LBRACE");
        OPERATOR.put("}", "RBRACE");

        // 读取文件testfile.txt，文件在当前目录下
        String source = "testfile.txt";
        // 目标文件output.txt，文件在当前目录下
        String target = "output.txt";
        
        br = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(source)), "utf-8"));
        bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(target)), "utf-8"));

        line = "";
        Analysis();

        System.out.println(result);
        // 输出结果
        for (Token token : result) {
            bw.write(token.category + " " + token.value + "\n");
        }

        br.close();
        bw.close();

    }

    // 读取下一个字符
    private static void getch() throws IOException {

        // 如果当前行已经读完，就读入下一行
        if (charCounter == lineLength) {
            line = br.readLine();
            if (line == null) {
                return;
            }

            // 如果行为空或者全是空格，就继续读入下一行
            while (line.equals("") || line.matches("\\s*")) {
                line = br.readLine();
                if (line == null) {
                    return;
                }
            }

            //确定当前行的长度以确定换行时机，将字符串转换为字符数组
            lineLength = line.length();
            charCounter = 0;
            lineCharArr = line.toCharArray();
        }

        //初始化首个字符
        ch = lineCharArr[charCounter];
        charCounter++;
    }

    private static void Analysis() throws IOException {

        while (true) {
            while (Character.isWhitespace(ch)) {
                // 跳过所有空白字符
                getch();
            }

            if (line == null) {
                return;
            }

            //对注释的处理，核心规则为：优先处理/**/,无视中间所有字符（由于跨行的问题不得不遍历）；在保证/**/全部处理完毕后，再处理//，无视//之后的本行所有字符（依靠将charCounter直接指向行末）
            //如此，解决了两符号的嵌套问题
            if (ch == '/') {
                getch();
                if (ch == '*') {
                    while (true) {
                        getch();
                        if (ch == '*') {
                            getch();
                            if (ch == '/') {
                                getch();
                                break;
                            }
                        }
                    }
                } else if (ch == '/') {
                    charCounter = lineLength;
                    getch();
                } else {
                    result.add(new Token(OPERATOR.get("/"), "/"));
                }
            }

            // 分四类处理：关键字或者一般标识符、字符串、数字、操作符
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                // 关键字或者一般标识符
                matchKeywordOrIdentifier();
            } else if (ch == '"') {
                matchString();
            } else if (ch >= '0' && ch <= '9') {
                // 数字
                matchNumber();
            } else {
                // 操作符
                matchOperator();
            }
        }

    }

    private static void matchString() throws IOException {
        // 首先，将"读入
        String str = "";
        str += ch;
        ch = lineCharArr[charCounter];
        System.out.println(ch);
        charCounter++;
        while (ch != '"') {

            str += ch;
            ch = lineCharArr[charCounter];
            System.out.println(ch);
            charCounter++;

            if (ch == '\\') {
                str += ch;
                ch = lineCharArr[charCounter];
                System.out.println(ch);
                charCounter++;

                str += ch;
                ch = lineCharArr[charCounter];
                System.out.println(ch);
                charCounter++;

            }
        }
        str += ch;
        ch = lineCharArr[charCounter];
        System.out.println(ch);
        charCounter++;

        result.add(new Token(KEYWORD_OR_IDENT.get("FormatString"), str));

    }

    private static void matchKeywordOrIdentifier() throws IOException {
        StringBuilder sb = new StringBuilder(MAX_ID_LEN);

        // 首先把整个单词读出来
        do {
            sb.append(ch);
            getch();
        } while (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9');

        id = sb.toString();

        // 搜索是否是保留字
        if (KEYWORD_OR_IDENT.containsKey(id)) {
            result.add(new Token(KEYWORD_OR_IDENT.get(id), id));
        } else {
            // 是一般标识符
            result.add(new Token(KEYWORD_OR_IDENT.get("Ident"), id));
        }
    }

    private static void matchNumber() throws IOException {
        int i = 0;
        num = 0;
        do {
            num = num * 10 + ch - '0';
            i++;
            getch();
        } while (ch >= '0' && ch <= '9');
        if (i > MAX_NUM_LEN) {
            // 数值位数过多
        }
        result.add(new Token(NUMBER.get("IntConst"), String.valueOf(num)));
    }

    private static void matchOperator() throws IOException {
        switch (ch) {
            // 处理双字符运算符
            case '=':
                getch();
                if (ch == '=') {
                    result.add(new Token(OPERATOR.get("=="), "=="));
                    getch();
                } else {
                    result.add(new Token(OPERATOR.get("="), "="));
                }
                break;
            case '!':
                getch();
                if (ch == '=') {
                    result.add(new Token(OPERATOR.get("!="), "!="));
                    getch();
                } else {
                    result.add(new Token(OPERATOR.get("!"), "!"));
                }
                break;
            case '<':
                getch();
                if (ch == '=') {
                    result.add(new Token(OPERATOR.get("<="), "<="));
                    getch();
                } else {
                    result.add(new Token(OPERATOR.get("<"), "<"));
                }
                break;
            case '>':
                getch();
                if (ch == '=') {
                    result.add(new Token(OPERATOR.get(">="), ">="));
                    getch();
                } else {
                    result.add(new Token(OPERATOR.get(">"), ">"));
                }
                break;
            case '&':
                getch();
                if (ch == '&') {
                    result.add(new Token(OPERATOR.get("&&"), "&&"));
                    getch();
                } else {
                    // 错误处理
                }
                break;
            case '|':
                getch();
                if (ch == '|') {
                    result.add(new Token(OPERATOR.get("||"), "||"));
                    getch();
                } else {
                    // 错误处理
                }
                break;
            default:
                // 处理单字符运算符
                if (OPERATOR.containsKey(String.valueOf(ch))) {
                    result.add(new Token(OPERATOR.get(String.valueOf(ch)), String.valueOf(ch)));
                    getch();
                } else {
                    // 错误处理
                }
                break;
        }
    }
}
