//���ִʷ����ŵ��ֱ���

public enum LexSymbol {
    // ��ʶ��
    IDENFR,
    // �ַ���
    STRCON,
    // ����
    INTCON,

    // �ؼ���
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

    // ������
    NOT,    // ��
    AND,    // &&
    OR,     // ||
    GEQ,    // >=
    EQL,    // ==
    LEQ,    // <=
    PLUS,   // +
    MINU,   // -
    NEQ,    // ��=
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