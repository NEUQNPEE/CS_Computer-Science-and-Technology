/**
 * @Author       : NieFire planet_class@foxmail.com
 * @Date         : 2023-10-06 18:03:44
 * @LastEditors  : NieFire planet_class@foxmail.com
 * @LastEditTime : 2023-10-13 14:45:39
 * @FilePath     : \CS_Computer-Science-and-Technology\特殊文件共享\编译原理实验\第二次实验代码\IFileWriteMediator.java
 * @Description  : 文件写入中介者的接口
 * @( ﾟ∀。)只要加满注释一切都会好起来的( ﾟ∀。)
 * @Copyright (c) 2023 by NieFire, All Rights Reserved. 
 */

public interface IFileWriteMediator {
    // 写文件函数，将已经传给中介者的暂存字符串写入文件并清空
    public void writeFile() throws Exception;

    // 向中介者暂存字符串
    public void write(String str);

    // 清空中介者暂存字符串
    public void clear();

    // 关闭文件流
    public void close() throws Exception;
}
