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
id˳���ѵ�380650
 */

public class Txt {
    public static void main(String[] args) {
        //��ȡͬĿ¼�µ�txt�ļ����������.txt��
        //��txt�ļ�����Ҫ�����������£�
        //435465	����	PGD-627����餷�������Ǥȥ��å���С��������	С��������	4.51GB	����[����ȥ��������]	2020/12/31	magnet:?xt=urn:btih:e4969c15f34427aee7512c098b67741117801f66	https://jp.netcdn.space/digital/video/pgd00627/pgd00627pl.jpg	https://www.assdrty.com/tupian/forum/202012/31/134336tm8naau8cz8vuql7.jpg	
        //���е���Ҫ���������ֿ�ͷ�����������������͡����⣬��Ա����С���Ƿ����룬���ڣ��������ӣ�һ������ͼƬ����
        //�������Ĵ���������Ҫ�������������ݣ�һ���ĸ��֣���Ȼ����������Ҫ���ݵı���������������������򽫸������ݵĿ�ͷ���֣����⣬��Ա����С���������ӣ�һ������ͼƬ���������ͬĿ¼�µ�txt�ļ������.txt����ͼƬ����Ҫ�Գ����ӵ���ʽ���

        //��ʱ������������
        int id = 0;
        String title = "";
        String actor = "";
        String size = "";
        String magnet = "";
        String img1 = "";
        String img2 = "";
        String img3 = "";


        Scanner sc = new Scanner(System.in);
        System.out.println("�������������ݣ�");
        String search = sc.nextLine();

        //��ȡtxt�ļ�
        String path = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\�������1.txt";
        String path1 = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\���.html";

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
                    //����������ӣ���ӱ�ǩ
                    magnet = "<a href=\"" + str[7] + "\">" + "��������" + "</a>";
                    //����ͼƬ���ӣ���ӱ�ǩ
                    if (str.length > 8) {
                        img1 = "<a href=\"" + str[8] + "\">" + str[8] + "</a>";
                    }
                    if (str.length > 9) {
                        img2 = "<a href=\"" + str[9] + "\">" + str[9] + "</a>";
                    }
                    if (str.length > 10) {
                        img3 = "<a href=\"" + str[10] + "\">" + str[10] + "</a>";
                    }

                    //��������.txt
                    bw.write(id + "\t" + title + "\t" + actor + "\t" + size + "\t" + magnet + "\t" + img1 + "\t" + img2 + "\t" + img3 + "\n");
                    //���������ҳ����
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
