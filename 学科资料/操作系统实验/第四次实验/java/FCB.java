package 学科资料.操作系统实验.第四次实验.java;
import java.io.Serializable;
import java.util.HashMap;

import java.util.Map;

public class FCB implements Serializable {

    public Map<String, FCB> filesOrDirsList = new HashMap<>();
    private String name;
    private int attribute;
    private int startNum;    //在FAT表中起始位置
    private int size;
    private FCB father = null;
    private String content = "";


    public FCB(String name, int attribute, int startNum, int size) {
        this.name = name;
        this.attribute = attribute;
        this.startNum = startNum;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FCB getFather() {
        return father;
    }

    public void setFather(FCB father) {
        this.father = father;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

