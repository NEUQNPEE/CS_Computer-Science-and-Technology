package 学科资料.操作系统实验.第四次实验.java;

import java.io.*;
import java.util.*;

public class FileSystemManagement {

    // 是目录
    private static final int IS_DIR = 3;
    // 是文件
    private static final int IS_FILE = 2;

    /**
     * 当前目录
     */
    private FCB currentDir;

    private Map<String, FCB> totalFiles = new HashMap<>();

    private static class FATManageMent {

        // FAT 终结符
        static final int FAT_END = 255;

        // FAT表的盘块个数
        static final int BLOCK_SIZE = 128;

        // 剩余盘块
        static int available;

        static int[] FAT = new int[BLOCK_SIZE];

        static void initFAT() {
            // FAT表初始化，FAT[0]为根目录
            Arrays.fill(FAT, 0);
            FAT[0] = FAT_END;
            // 剩余块数
            available = BLOCK_SIZE - 1;
        }

        static int allocFAT(int size) {
            int[] startNum = new int[128];
            int i = 1; // 纪录fat循环定位
            for (int j = 0; j < size; i++) {
                if (FAT[i] == 0) {
                    startNum[j] = i; // 纪录该文件所有磁盘块
                    if (j > 0) {
                        FAT[startNum[j - 1]] = i; // fat上一磁盘块指向下一磁盘块地址
                    }
                    j++;
                }
            }
            FAT[i - 1] = FAT_END;
            return startNum[0];
        }

        /**
         * 释放文件对应的FAT表占用空间
         */
        static void delFAT(int startNum) {
            int nextBlock;
            int currentBlock = startNum;
            int freeSize = 0;
            while (FAT[currentBlock] != FAT_END) {
                nextBlock = FAT[currentBlock];
                FAT[currentBlock] = 0;
                currentBlock = nextBlock;
                freeSize++;
            }
            FAT[currentBlock] = 0;
            available += freeSize + 1;
        }

        static void FATlist() {
            System.out.println(Arrays.toString(FAT));
        }
    }

    public FileSystemManagement() {
        FATManageMent.initFAT();
        // 创建根目录 使用fat表的第一项
        FCB root = new FCB("root", 0, 0, 1);
        root.setFather(root);
        currentDir = root;
    }

    /**
     * 1. 格式化
     */
    public void format() {
        String path = "./fs.txt";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fs);
            for (FCB fcb : totalFiles.values()) {
                oos.writeObject(fcb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 创建目录
     */
    public void mkdir(String name) {

        if (FATManageMent.available >= 1) {
            FCB value = currentDir.filesOrDirsList.get(name);
            if (value != null && value.getAttribute() == IS_DIR) {
                System.out.println("该目录下存在重名目录。不可创建");
            } else {
                int startNum = FATManageMent.allocFAT(1);
                FCB catalog = new FCB(name, IS_DIR, startNum, 1);
                catalog.setFather(currentDir);
                currentDir.filesOrDirsList.put(name, catalog);
                FATManageMent.available--;

                System.out.println("成功创建目录");
            }
        } else {
            System.out.println("创建目录失败，磁盘空间不足");
        }
    }

    /**
     * 9. 删除文件 / 3. 删除目录
     */
    public void rm(String name) {

        FCB value = currentDir.filesOrDirsList.get(name);
        if (value == null) {
            System.out.println("删除失败，目标不存在");
        } else if (value.getAttribute() == IS_DIR && !value.filesOrDirsList.isEmpty()) {
            System.out.println("删除失败，对应目录不为空");
        } else {
            currentDir.filesOrDirsList.remove(name);
            totalFiles.remove(name);
            FATManageMent.delFAT(value.getStartNum());
            System.out.println((value.getAttribute() == IS_DIR ? "目录 " : "文件 ") + value.getName() + " 已成功删除");
        }
    }

    /**
     * 4. 显示目录下所有项
     */
    public void ls() {
        if (!currentDir.filesOrDirsList.isEmpty()) {
            System.out.println("文件名    类型     起始盘块     大小");
            for (FCB value : currentDir.filesOrDirsList.values()) {
                String type = value.getAttribute() == IS_DIR ? "目录" : "文件";
                System.out.println(value.getName() + "      " + type + "       "
                        + value.getStartNum() + "       " + value.getSize());
            }
        }
    }

    public void showPath() {
        System.out.print(currentDir.getName() + ">>");
    }

    /**
     * 5. 用于更改当前目录
     */
    public void cd(String name) {
        if ("../".equals(name)) {
            cdpp();
            return;
        }
        FCB value = currentDir.filesOrDirsList.get(name);
        if (value == null) {
            System.out.println("切换失败，目录不存在");
        } else if (value.getAttribute() == IS_FILE) {
            System.out.println("打开文件可使用 open 命令");
        } else {
            currentDir = value;
        }
    }

    /*
     * 6. 创建文件
     */
    public void create(String name, int size) {

        if (FATManageMent.available >= size) {
            // 查找目录下有无同名项
            FCB value = currentDir.filesOrDirsList.get(name);
            if (value != null && value.getAttribute() == IS_FILE) {
                System.out.println("该目录下存在重名文件。不可创建");
            } else {
                int startBlockNum = FATManageMent.allocFAT(size);
                FCB file = new FCB(name, IS_FILE, startBlockNum, size);
                file.setFather(currentDir);
                // 当前目录添加该文件
                currentDir.filesOrDirsList.put(name, file);
                FATManageMent.available -= size;
                totalFiles.put(name, file);
                System.out.println("成功创建文件");
            }
        } else {
            System.out.println("创建文件失败，磁盘空间不足");
        }

    }

    /*
     * 7. 打开文件
     */
    public void open(String name) {
        FCB value = currentDir.filesOrDirsList.get(name);
        if (value == null) {
            System.out.println("打开失败，文件不存在");
        } else if (value.getAttribute() == IS_DIR) {
            System.out.println("打开目录可使用 cd 命令");
        } else {
            System.out.println("打开成功");
            currentDir = value;
        }
    }

    /**
     * 8. 关闭文件
     */
    public void close() {
        if (currentDir.getAttribute() == IS_FILE) {
            System.out.println("文件已关闭");
            cdpp();
        } else {
            System.out.println("并没有打开文件");
        }
    }

    /*
     *
     * 以下为返回上一层目录
     *
     */
    public void cdpp() {
        if (currentDir.getFather() == null) {
            System.out.println("返回上一层目录失败");
        } else {
            currentDir = currentDir.getFather();
        }
    }

    public void read() {
        if (currentDir.getAttribute() == IS_FILE) {
            System.out.println(currentDir.getContent());
        } else {
            System.out.println("当前未打开文件");
        }
    }

    public void write(String content, int offset) {
        if (offset < 0) {
            System.out.println("偏移量不可小于 0");
        }
        if (currentDir.getAttribute() == IS_FILE) {
            StringBuilder builder = new StringBuilder();
            builder.append(currentDir.getContent());
            builder.insert(offset, content);
            currentDir.setContent(builder.toString());
        } else {
            System.out.println("当前未打开文件");
        }
    }

    public void FAT() {
        FATManageMent.FATlist();
    }

    public void exit() {
        System.exit(1);
    }
}
