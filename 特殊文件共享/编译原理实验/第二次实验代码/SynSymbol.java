//各种语法符号的种别码

public enum SynSymbol {
    // 编译单元
    CompUnit,

    // 声明
    Decl,

    // 常量声明
    ConstDecl,

    // 基本类型
    BType,

    // 常数定义
    ConstDef,

    // 常量初值
    ConstInitVal,

    // 变量声明
    VarDecl,

    // 变量定义
    VarDef,

    // 变量初值
    InitVal,

    // 函数定义
    FuncDef,

    // 函数类型
    FuncType,

    // 函数形参表
    FuncFParams,

    // 函数形参
    FuncFParam,

    // 主函数定义
    MainFuncDef,

    // 语句块
    Block,

    // 语句块项
    BlockItem,

    // 语句
    Stmt,

    // 表达式
    Exp,

    // 条件表达式
    Cond,

    // 左值表达式
    LVal,

    // 基本表达式
    PrimaryExp,

    // 数值
    Number,

    // 一元表达式
    UnaryExp,

    // 单目运算符
    UnaryOp,

    // 函数实参表
    FuncRParams,

    // 乘除模表达式
    MulExp,

    // 加减表达式
    AddExp,

    // 关系表达式
    RelExp,

    // 相等性表达式
    EqExp,

    // 逻辑与表达式
    LAndExp,

    // 逻辑或表达式
    LOrExp,

    // 常量表达式
    ConstExp,
}

