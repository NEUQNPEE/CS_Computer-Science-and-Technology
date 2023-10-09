package corepackage.tool;

import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
id顺序已到380650
 */

public class Txt {
    public static void main(String[] args) {
        //读取同目录下的txt文件“八万磁力.txt”
        //该txt文件的主要内容样例如下：
        //435465	无码	PGD-627いやらし～い接吻とセックス小川あさ美	小川あさ美	4.51GB	无码[有损去除马赛克]	2020/12/31	magnet:?xt=urn:btih:e4969c15f34427aee7512c098b67741117801f66	https://jp.netcdn.space/digital/video/pgd00627/pgd00627pl.jpg	https://www.assdrty.com/tupian/forum/202012/31/134336tm8naau8cz8vuql7.jpg	
        //所有的主要内容以数字开头，接下来依次是类型、标题，演员，大小，是否有码，日期，磁力链接，一到三张图片链接
        //接下来的代码中首先要求输入搜索内容（一到四个字），然后在所有主要内容的标题中搜索，如果搜索到则将该行内容的开头数字，标题，演员，大小，磁力链接，一到三张图片链接输出到同目录下的txt文件“结果.txt”，图片连接要以超链接的形式输出

        //暂时储存搜索内容
        int id = 0;
        String title = "";
        String actor = "";
        String size = "";
        String magnet = "";
        String img1 = "";
        String img2 = "";
        String img3 = "";


        Scanner sc = new Scanner(System.in);
        System.out.println("请输入搜索内容：");
        String search = sc.nextLine();

        //读取txt文件
        String path = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\八万磁力1.txt";
        String path1 = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\结果.html";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path1)), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\t");
                if(str.length<8) {
                    continue;
                }
                if (str[2].contains(search)) {
                    id = Integer.parseInt(str[0]);
                    title = str[2];
                    actor = str[3];
                    size = str[4];
                    // magnet = str[7];
                    //处理磁力链接，添加标签
                    magnet = "<a href=\"" + str[7] + "\">" + "磁力链接" + "</a>";
                    //处理图片链接，添加标签
                    if (str.length > 8) {
                        img1 = "<a href=\"" + str[8] + "\">" + str[8] + "</a>";
                    }
                    if (str.length > 9) {
                        img2 = "<a href=\"" + str[9] + "\">" + str[9] + "</a>";
                    }
                    if (str.length > 10) {
                        img3 = "<a href=\"" + str[10] + "\">" + str[10] + "</a>";
                    }

                    //输出到结果.txt
                    bw.write(id + "\t" + title + "\t" + actor + "\t" + size + "\t" + magnet + "\t" + img1 + "\t" + img2 + "\t" + img3 + "\n");
                    //输出两个网页换行
                    bw.write("<br>");
                    bw.write("<br>");
                    
                }
            }

            br.close();
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
