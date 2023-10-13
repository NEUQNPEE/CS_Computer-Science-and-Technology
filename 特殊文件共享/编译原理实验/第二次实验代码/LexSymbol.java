//各种词法符号的种别码

public enum LexSymbol {
    // 标识符
    IDENFR,
    // 字符串
    STRCON,
    // 整数
    INTCON,

    // 关键字
    MAINTK,     // main
    CONSTTK,    // const
    INTTK,      // int
    BREAKTK,    // break
    CONTINUETK, // continue
    IFTK,       // if
    ELSETK,     // else
    WHILETK,    // while
    GETINTTK,   // getint
    PRINTFTK,   // printf
    RETURNTK,   // return
    VOIDTK,     // void

    // 操作符
    NOT,    // ！
    AND,    // &&
    OR,     // ||
    GEQ,    // >=
    EQL,    // ==
    LEQ,    // <=
    PLUS,   // +
    MINU,   // -
    NEQ,    // ！=
    MULT,   // *
    ASSIGN, // =
    DIV,    // /
    SEMICN, // ;
    MOD,    // %
    COMMA,  // ,
    LSS,    // <
    LPARENT, // (
    RPARENT, // )
    GRE,    // >
    LBRACK, // [
    RBRACK, // ]
    LBRACE, // {
    RBRACE  // }
}