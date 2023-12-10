package os4;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FileSystemManagement management = new FileSystemManagement();
        run(management);
    }

    private static void run(FileSystemManagement manager) {
        Scanner s = new Scanner(System.in);
        String str;
        manager.showPath();
        while ((str = s.nextLine()) != null) {
            String[] strs = str.split(" ");
            switch (strs[0]) {
                case "format":
                    manager.format();
                    break;
                case "mkdir":
                    manager.mkdir(strs[1]);
                    //manager.FAT();
                    break;
                case "rmdir":
                case "rm":
                    manager.rm(strs[1]);
                    //manager.FAT();
                    break;
                case "ls":
                    manager.ls();
                    break;
                case "cd":
                    manager.cd(strs[1]);
                    break;
                case "create":
                    manager.create(strs[1], Integer.parseInt(strs[2]));
                    //manager.FAT();
                    break;
                case "open":
                    manager.open(strs[1]);
                    break;
                case "close":
                    manager.close();
                    break;
                case "read":
                    manager.read();
                    break;
                case "write":
                    manager.write(strs[1], Integer.parseInt(strs[2]));
                    break;
                case "FAT":
                    manager.FAT();
                    break;
                case "exit":
                    manager.exit();
                    break;
                default:
                    System.out.println("未知的命令.");
            }
            manager.showPath();
        }
    }


}
