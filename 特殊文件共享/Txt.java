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
id乏會厮欺380650
 */

public class Txt {
    public static void main(String[] args) {
        //響函揖朕村和議txt猟周＾伊嵐甘薦.txt￣
        //乎txt猟周議麼勣坪否劔箭泌和��
        //435465	涙鷹	PGD-627いやらし゛い俊稜とセックス弌寒あさ胆	弌寒あさ胆	4.51GB	涙鷹[嗤鱒肇茅瀧琵針]	2020/12/31	magnet:?xt=urn:btih:e4969c15f34427aee7512c098b67741117801f66	https://jp.netcdn.space/digital/video/pgd00627/pgd00627pl.jpg	https://www.assdrty.com/tupian/forum/202012/31/134336tm8naau8cz8vuql7.jpg	
        //侭嗤議麼勣坪否參方忖蝕遊��俊和栖卆肝頁窃侏、炎籾��處埀��寄弌��頁倦嗤鷹��晩豚��甘薦全俊��匯欺眉嫖夕頭全俊
        //俊和栖議旗鷹嶄遍枠勣箔補秘朴沫坪否��匯欺膨倖忖����隼朔壓侭嗤麼勣坪否議炎籾嶄朴沫��泌惚朴沫欺夸繍乎佩坪否議蝕遊方忖��炎籾��處埀��寄弌��甘薦全俊��匯欺眉嫖夕頭全俊補竃欺揖朕村和議txt猟周＾潤惚.txt￣��夕頭銭俊勣參階全俊議侘塀補竃

        //壙扮刈贋朴沫坪否
        int id = 0;
        String title = "";
        String actor = "";
        String size = "";
        String magnet = "";
        String img1 = "";
        String img2 = "";
        String img3 = "";


        Scanner sc = new Scanner(System.in);
        System.out.println("萩補秘朴沫坪否��");
        String search = sc.nextLine();

        //響函txt猟周
        String path = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\伊嵐甘薦1.txt";
        String path1 = "E:\\Javachengxu\\ceshi01\\Student\\src\\corepackage\\tool\\潤惚.html";

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
                    //侃尖甘薦全俊��耶紗炎禰
                    magnet = "<a href=\"" + str[7] + "\">" + "甘薦全俊" + "</a>";
                    //侃尖夕頭全俊��耶紗炎禰
                    if (str.length > 8) {
                        img1 = "<a href=\"" + str[8] + "\">" + str[8] + "</a>";
                    }
                    if (str.length > 9) {
                        img2 = "<a href=\"" + str[9] + "\">" + str[9] + "</a>";
                    }
                    if (str.length > 10) {
                        img3 = "<a href=\"" + str[10] + "\">" + str[10] + "</a>";
                    }

                    //補竃欺潤惚.txt
                    bw.write(id + "\t" + title + "\t" + actor + "\t" + size + "\t" + magnet + "\t" + img1 + "\t" + img2 + "\t" + img3 + "\n");
                    //補竃曾倖利匈算佩
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
