#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <sstream>
using namespace std;
map<string, string> m;
string line;
bool state = false;
int n;
void init()
{
    m.insert(make_pair("main", "MAINTK"));
    m.insert(make_pair("const", "CONSTTK"));
    m.insert(make_pair("int", "INTTK"));
    m.insert(make_pair("break", "BREAKTK"));
    m.insert(make_pair("continue", "CONTINUETK"));
    m.insert(make_pair("if", "IFTK"));
    m.insert(make_pair("else", "ELSETK"));
    m.insert(make_pair("!", "NOT"));
    m.insert(make_pair("&&", "AND"));
    m.insert(make_pair("||", "OR"));
    m.insert(make_pair("while", "WHILETK"));
    m.insert(make_pair("getint", "GETINTTK"));
    m.insert(make_pair("printf", "PRINTFTK"));
    m.insert(make_pair("return", "RETURNTK"));
    m.insert(make_pair("+", "PLUS"));
    m.insert(make_pair("-", "MINU"));
    m.insert(make_pair("void", "VOIDTK"));
    m.insert(make_pair("*", "MULT"));
    m.insert(make_pair("/", "DIV"));
    m.insert(make_pair("%", "MOD"));
    m.insert(make_pair("<", "LSS"));
    m.insert(make_pair("<=", "LEQ"));
    m.insert(make_pair(">", "GRE"));
    m.insert(make_pair(">=", "GEQ"));
    m.insert(make_pair("==", "EQL"));
    m.insert(make_pair("!=", "NEQ"));
    m.insert(make_pair("=", "ASSIGN"));
    m.insert(make_pair(";", "SEMICN"));
    m.insert(make_pair(",", "COMMA"));
    m.insert(make_pair("(", "LPARENT"));
    m.insert(make_pair(")", "RPARENT"));
    m.insert(make_pair("[", "LBRACK"));
    m.insert(make_pair("]", "RBRACK"));
    m.insert(make_pair("{", "LBRACE"));
    m.insert(make_pair("}", "RBRACE"));
}
// 定义一个write函数用来将两个字符串写入output.txt文件并用空格分开并换行
void write(string str1, string str2)
{
    ofstream out("output.txt", ios::app);
    out << str2 << " " << str1 << endl;
    out.close();
}
void note()
{
    while ((n + 1) < line.size())
    {
        if (line[n] == '*' && line[n + 1] == '/')
        {
            state = false;
            n += 2;
            break;
        }
        else
        {
            n++;
        }
    }
}
void keywordOrIdentifier()
{
    string word;
    while ((line[n] >= 'a' && line[n] <= 'z') || (line[n] >= 'A' && line[n] <= 'Z') || (line[n] >= '0' && line[n] <= '9'))
    {
        word += line[n];
        n++;
    }
    map<string, string>::iterator pos = m.find(word);
    if (pos != m.end())
    {
        write(word, pos->second);
    }
    else
    {
        write(word, "IDENFR");
    }
}
void str()
{
    string word = "";
    word += line[n];
    n++;
    while (line[n] != '"')
    {
        word += line[n];
        n++;
    }
    word += line[n];
    write(word, "STRCON");
    n++;
}
void number()
{
    string word = "";
    while ((line[n] >= '0' && line[n] <= '9'))
    {
        word += line[n];
        n++;
    }
    write(word, "INTCON");
}
void oper()
{
    switch (line[n]) {
        // 处理双字符运算符
    case '=':
        n++;
        if (line[n] == '=')
        {
            write("==", m["=="]);
            n++;
        }
        else
        {
            write("=", m["="]);
        }
        break;
    case '!':
        n++;
        if (line[n] == '=')
        {
            write("!=", m["!="]);
            n++;
        }
        else
        {
            write("!", m["!"]);
        }
        break;
    case '<':
        n++;
        if (line[n] == '=')
        {
            write("<=", m["<="]);
            n++;
        }
        else
        {
            write("<", m["<"]);
        }
        break;
    case '>':
        n++;
        if (line[n] == '=')
        {
            write(">=", m[">="]);
            n++;
        }
        else
        {
            write(">", m[">"]);
        }
        break;
    case '&':
        n++;
        if (line[n] == '&')
        {
            write("&&", m["&&"]);
            n++;
        }
        else
        {

        }
        break;
    case '|':
        n++;
        if (line[n] == '|')
        {
            write("||", m["||"]);
            n++;
        }
        else
        {

        }
        break;
    default:
        string word;
        word += line[n];
        map<string, string>::iterator pos = m.find(word);
        if (pos != m.end())
        {
            write(word, pos->second);
            n++;
            break;
        }
        else
        {
            n++;
            break;
        }
    }
}
void analysis()
{
    if (!state)
    {
        while (n < line.size())
        {
            while (line[n] == ' ')
            {
                n++;
            }
            if (n >= line.size())
            {
                break;
            }
            if (line[n] == '\r')
            {
                break;
            }
            if (line[n] == '/')
            {
                n++;
                if (line[n] == '*')
                {
                    state = true;
                    n++;
                    note();
                }
                else if (line[n] == '/')
                {
                    break;
                }
                else
                {
                    write("/", m["/"]);
                }
            }
            // 分四类处理：关键字或者一般标识符、字符串、数字、操作符
            if ((line[n] >= 'a' && line[n] <= 'z') || (line[n] >= 'A' && line[n] <= 'Z')) {
                // 关键字或者一般标识符
                keywordOrIdentifier();
            }
            else if (line[n] == '"') {
                str();
            }
            else if (line[n] >= '0' && line[n] <= '9') {
                // 数字
                number();
            }
            else {
                // 操作符
                oper();
            }
        }
    }
    else
    {
        note();
    }
}
void readtext()
{
    // 定义并打开文件整行读取并接收
    ifstream ifs;
    ifs.open("testfile.txt");
    if (!ifs.is_open())
    {
        cout << "文件打开失败" << endl;
        return;
    }
    while (getline(ifs,line))
    {
        n = 0;
        analysis();
    }
    ifs.close();
}
int main()
{
    init();
    ofstream ofs("output.txt", ios::trunc);
    ofs.close();
    readtext();
	return 0;
}
