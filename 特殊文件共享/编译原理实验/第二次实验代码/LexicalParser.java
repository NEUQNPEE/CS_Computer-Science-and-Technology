/**
 * @Author       : NieFire planet_class@foxmail.com
 * @Date         : 2023-10-13 14:29:02
 * @LastEditors  : NieFire planet_class@foxmail.com
 * @LastEditTime : 2023-10-13 14:45:25
 * @FilePath     : \CS_Computer-Science-and-Technology\特殊文件共享\编译原理实验\第二次实验代码\LexicalParser.java
 * @Description  : 
 * @( ﾟ∀。)只要加满注释一切都会好起来的( ﾟ∀。)
 * @Copyright (c) 2023 by NieFire, All Rights Reserved. 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//词法分析器负责的工作是从源代码里面读取文法符号

public class LexicalParser {
    class Token {
        String category;
        LexSymbol symbol;

        Token(LexSymbol symbol, String category) {
            this.symbol = symbol;
            this.category = category;
        }
    }

    // 符号所允许的最大长度
    private final int MAX_ID_LEN = 10;

    // 数值所允许的最大位数
    private final int MAX_NUM_LEN = 14;

    // 刚刚读入的字符
    private char ch = ' ';

    // 当前读入文件的字符数组
    private char[] lineCharArr;

    // 当前读入文件字符串的长度
    public int lineLength = 0;

    // 当前字符在当前读入文件字符串中的位置
    public int charCounter = 0;

    // 当前字符所在行数
    public int lineCounter = 1;

    // 当前读入的符号
    public LexSymbol sym;

    // 标识符名称
    public String id = "";

    // 数值
    public int num = 0;

    // 将词与词的种别码对应起来，使用map数据结构
    private final Map<String, LexSymbol> KEYWORD_OR_IDENT = new HashMap<String, LexSymbol>();
    private final Map<String, LexSymbol> NUMBER = new HashMap<String, LexSymbol>();
    private final Map<String, LexSymbol> OPERATOR = new HashMap<String, LexSymbol>();

    // 所得结果键值对
    public List<Token> result = new ArrayList<>();

    // 根据实验需求，读入下一个字符后，才将当前字符传给中介，因此需要两个Str作为缓存
    private String tempKey = "";
    private String tempStr = "";

    private IFileWriteMediator fileWriteMediator;

    // 构造函数
    public LexicalParser(IFileReadMediator fileReadMediator, IFileWriteMediator fileWriteMediator) throws Exception {
        // 初始化各种词法符号
        initKeywordOrIdent();
        initNumber();
        initOperator();

        this.fileWriteMediator = fileWriteMediator;

        // 加载文件
        String line = fileReadMediator.readFile();

        lineLength = line.length();
        lineCharArr = line.toCharArray();
        charCounter = 0;

        getch();
    }

    // 输出词法分析器的结果
    public void writeLexResult() throws Exception {
        for (Token token : result) {
            fileWriteMediator.write(token.symbol + " " + token.category + "\n");
        }
        fileWriteMediator.writeFile();
    }

    // 开始分析
    public void Analysis() throws Exception {
        while (ch != '\0') {
            getsym();
        }
    }

    private void initKeywordOrIdent() {
        KEYWORD_OR_IDENT.put("main", LexSymbol.MAINTK);
        KEYWORD_OR_IDENT.put("const", LexSymbol.CONSTTK);
        KEYWORD_OR_IDENT.put("int", LexSymbol.INTTK);
        KEYWORD_OR_IDENT.put("break", LexSymbol.BREAKTK);
        KEYWORD_OR_IDENT.put("continue", LexSymbol.CONTINUETK);
        KEYWORD_OR_IDENT.put("if", LexSymbol.IFTK);
        KEYWORD_OR_IDENT.put("else", LexSymbol.ELSETK);
        KEYWORD_OR_IDENT.put("while", LexSymbol.WHILETK);
        KEYWORD_OR_IDENT.put("getint", LexSymbol.GETINTTK);
        KEYWORD_OR_IDENT.put("printf", LexSymbol.PRINTFTK);
        KEYWORD_OR_IDENT.put("return", LexSymbol.RETURNTK);
        KEYWORD_OR_IDENT.put("void", LexSymbol.VOIDTK);
        KEYWORD_OR_IDENT.put("Ident", LexSymbol.IDENFR);
        KEYWORD_OR_IDENT.put("FormatString", LexSymbol.STRCON);
    }

    private void initNumber() {
        NUMBER.put("IntConst", LexSymbol.INTCON);
    }

    private void initOperator() {
        OPERATOR.put("!", LexSymbol.NOT);
        OPERATOR.put("&&", LexSymbol.AND);
        OPERATOR.put("||", LexSymbol.OR);
        OPERATOR.put(">=", LexSymbol.GEQ);
        OPERATOR.put("==", LexSymbol.EQL);
        OPERATOR.put("<=", LexSymbol.LEQ);
        OPERATOR.put("+", LexSymbol.PLUS);
        OPERATOR.put("-", LexSymbol.MINU);
        OPERATOR.put("!=", LexSymbol.NEQ);
        OPERATOR.put("*", LexSymbol.MULT);
        OPERATOR.put("=", LexSymbol.ASSIGN);
        OPERATOR.put("/", LexSymbol.DIV);
        OPERATOR.put(";", LexSymbol.SEMICN);
        OPERATOR.put("%", LexSymbol.MOD);
        OPERATOR.put(",", LexSymbol.COMMA);
        OPERATOR.put("<", LexSymbol.LSS);
        OPERATOR.put("(", LexSymbol.LPARENT);
        OPERATOR.put(")", LexSymbol.RPARENT);
        OPERATOR.put(">", LexSymbol.GRE);
        OPERATOR.put("[", LexSymbol.LBRACK);
        OPERATOR.put("]", LexSymbol.RBRACK);
        OPERATOR.put("{", LexSymbol.LBRACE);
        OPERATOR.put("}", LexSymbol.RBRACE);
    }

    private void getch() {

        // 如果当前字符串已经读完，说明已经读完文件，返回
        if (charCounter == lineLength) {
            return;
        }

        ch = lineCharArr[charCounter];
        charCounter++;

        if(ch == '\n') {
            lineCounter++;
            ch = lineCharArr[charCounter];
            charCounter++;
        }
    }

    public void getsym() {

        while (Character.isWhitespace(ch)) {
            // 跳过所有空白字符
            getch();
        }

        if (ch == '\0') {
            writeTempResult();
            return;
        }

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
                updateResultAndSym("/", "/");
            }
        }

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

    private void matchString() {
        // 首先，将"读入
        String str = "";
        str += ch;
        ch = lineCharArr[charCounter];
        charCounter++;
        while (ch != '"') {

            str += ch;
            ch = lineCharArr[charCounter];
            charCounter++;

            if (ch == '\\') {
                str += ch;
                ch = lineCharArr[charCounter];
                charCounter++;

                str += ch;
                ch = lineCharArr[charCounter];
                charCounter++;

            }
        }
        str += ch;
        ch = lineCharArr[charCounter];
        charCounter++;

        updateResultAndSym("FormatString", str);
    }

    private void matchKeywordOrIdentifier() {
        StringBuilder sb = new StringBuilder(MAX_ID_LEN);

        // 首先把整个单词读出来
        do {
            sb.append(ch);
            getch();
        } while (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9');

        id = sb.toString();

        // 搜索是否是保留字
        if (KEYWORD_OR_IDENT.containsKey(id)) {
            updateResultAndSym(id, id);
        } else {
            // 是一般标识符
            updateResultAndSym("Ident", id);
        }
    }

    private void matchNumber() {
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
        updateResultAndSym("IntConst", String.valueOf(num));
    }

    private void matchOperator() {
        switch (ch) {
            // 处理双字符运算符
            case '=':
                getch();
                if (ch == '=') {
                    updateResultAndSym("==", "==");
                    getch();
                } else {
                    updateResultAndSym("=", "=");
                }
                break;
            case '!':
                getch();
                if (ch == '=') {
                    updateResultAndSym("!=", "!=");
                    getch();
                } else {
                    updateResultAndSym("!", "!");
                }
                break;
            case '<':
                getch();
                if (ch == '=') {
                    updateResultAndSym("<=", "<=");
                    getch();
                } else {
                    updateResultAndSym("<", "<");
                }
                break;
            case '>':
                getch();
                if (ch == '=') {
                    updateResultAndSym(">=", ">=");
                    getch();
                } else {
                    updateResultAndSym(">", ">");
                }
                break;
            case '&':
                getch();
                if (ch == '&') {
                    updateResultAndSym("&&", "&&");
                    getch();
                } else {
                    // 错误处理
                }
                break;
            case '|':
                getch();
                if (ch == '|') {
                    updateResultAndSym("||", "||");
                    getch();
                } else {
                    // 错误处理
                }
                break;
            default:
                // 处理单字符运算符
                if (OPERATOR.containsKey(String.valueOf(ch))) {
                    updateResultAndSym(String.valueOf(ch), String.valueOf(ch));
                    getch();
                } else {
                    // 错误处理
                }
                break;
        }
    }

    // 记录词及其种别码，并更新sym
    private void updateResultAndSym(String key, String str) {
        if (OPERATOR.containsKey(key)) {
            result.add(new Token(OPERATOR.get(key), str));
            sym = OPERATOR.get(key);
        } else if (KEYWORD_OR_IDENT.containsKey(key)) {
            result.add(new Token(KEYWORD_OR_IDENT.get(key), str));
            sym = KEYWORD_OR_IDENT.get(key);

        } else if (NUMBER.containsKey(key)) {
            result.add(new Token(NUMBER.get(key), str));
            sym = NUMBER.get(key);
        } else {
            // 错误处理
        }

        writeTempResult(key, str);
    }

    private void writeTempResult(String key, String str) {
        if (!tempKey.equals("") && !tempStr.equals("")) {
            if (OPERATOR.containsKey(tempKey)) {
                fileWriteMediator.write(OPERATOR.get(tempKey) + " " + tempStr + "\n");
            } else if (KEYWORD_OR_IDENT.containsKey(tempKey)) {
                fileWriteMediator.write(KEYWORD_OR_IDENT.get(tempKey) + " " + tempStr + "\n");
            } else if (NUMBER.containsKey(tempKey)) {
                fileWriteMediator.write(NUMBER.get(tempKey) + " " + tempStr + "\n");
            } else {
                // 错误处理
            }
        }
        tempKey = key;
        tempStr = str;
    }

    private void writeTempResult() {
        if (!tempKey.equals("") && !tempStr.equals("")) {
            if (OPERATOR.containsKey(tempKey)) {
                fileWriteMediator.write(OPERATOR.get(tempKey) + " " + tempStr + "\n");
            } else if (KEYWORD_OR_IDENT.containsKey(tempKey)) {
                fileWriteMediator.write(KEYWORD_OR_IDENT.get(tempKey) + " " + tempStr + "\n");
            } else if (NUMBER.containsKey(tempKey)) {
                fileWriteMediator.write(NUMBER.get(tempKey) + " " + tempStr + "\n");
            } else {
                // 错误处理
            }
        }
    }
}
