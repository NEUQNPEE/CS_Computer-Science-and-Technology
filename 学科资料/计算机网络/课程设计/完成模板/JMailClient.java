package JPOP3;

import java.util.Base64;
import java.util.Scanner;

import javax.mail.internet.MimeUtility;

public class JMailClient {
    JPOP3 pop3;

    private void onStauts(String pop3ConnectStr) {
        String[] parts = pop3ConnectStr.split(";");
        if (parts.length < 4) {
            System.out.println("Error: Invalid connection string");
            return;
        }

        pop3 = new JPOP3(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
        if (!pop3.connect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        int num = pop3.getNumMessages();
        if (num < 0) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        System.out.println("Number of messages: " + num);
    }

    private void onRetr(String pop3ConnectStr, int msgNum) {
        String[] parts = pop3ConnectStr.split(";");
        if (parts.length < 4) {
            System.out.println("Error: Invalid connection string");
            return;
        }

        pop3 = new JPOP3(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
        if (!pop3.connect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        JMailMessage msg = new JMailMessage();
        if (!pop3.getMessage(msgNum, msg)) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        printMail(msg);

        String to = "";

        for (int i = 0; i < msg.getNumRecipients(); i++) {
            String emailAddr = "";
            String friendlyName = "";
            if (!msg.getRecipient(emailAddr, friendlyName, i)) {
                System.out.println("Error: " + pop3.getLastError());
                return;
            }

            to += emailAddr + " ";
        }

        to = to.trim();

        if (!pop3.disconnect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }
        System.out.println("邮件接收成功");
    }

    private void printMail(JMailMessage msg) {
        String body = msg._body;
        String subject = msg._subject;
        String date = msg._date;
        String from = msg._from;

        // 将body的第五行与第十行内容base64解码为gbk
        String[] lines = body.split("\r\n");
        // String contentText = lines[4];
        // String contentHtml = lines[9];
        String contentText = "";
        String contentHtml = "";
        // 按行解析，先跳过4行，从第五行开始只要遇到开头不为“------”的行就是文本内容
        // 读取完文本内容后，再跳过4行，再读取html内容
        int line = 0;
        for (line = 4; line < lines.length; line++) {
            if (lines[line].startsWith("------")) {
                break;
            }
            contentText += lines[line];
        }
        for (line += 4; line < lines.length; line++) {
            if (lines[line].startsWith("------")) {
                break;
            }
            contentHtml += lines[line];

        }

        try {
            contentText = new String(Base64.getDecoder().decode(contentText), "GBK");
            contentHtml = new String(Base64.getDecoder().decode(contentHtml), "GBK");
            subject = MimeUtility.decodeText(subject);
            System.out.println("邮件内容: " + contentText);
            System.out.println("Html格式: " + contentHtml);
            System.out.println("邮件主题: " + subject);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("邮件日期: " + date);
        System.out.println("邮件发送者: " + from);
    }

    private void onDele(String pop3ConnectStr, int msgNum) {
        String[] parts = pop3ConnectStr.split(";");
        if (parts.length < 4) {
            System.out.println("Error: Invalid connection string");
            return;
        }

        pop3 = new JPOP3(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
        if (!pop3.connect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        if (!pop3.deleteMessage(msgNum)) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }
    }

    private void onRest() {
        if (!pop3.undeleteMessage()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        if (!pop3.disconnect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        System.out.println("撤销所有删除成功");
    }

    private void onQuit() {
        if (!pop3.disconnect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        System.out.println("删除邮件成功");
    }

    private void onList(String pop3ConnectStr) {
        String[] parts = pop3ConnectStr.split(";");
        if (parts.length < 4) {
            System.out.println("Error: Invalid connection string");
            return;
        }

        JPOP3 pop3 = new JPOP3(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
        if (!pop3.connect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        String list = pop3.getMailList();
        if (list == null) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        System.out.println("邮件列表: " + list);

        if (!pop3.disconnect()) {
            System.out.println("Error: " + pop3.getLastError());
            return;
        }

        System.out.println("邮件列表获取成功");
    }

    public static void main(String[] args) {
        JMailClient client = new JMailClient();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入命令");

        while (true) {
            String cmd = sc.nextLine();
            if (cmd.equals("exit")) {
                break;
            }

            if (cmd.equals("status")) {
                client.onStauts(String.format("%s;%d;%s;%s", "pop.163.com", 110, "自己填写", "自己填写"));
                continue;
            }

            if (cmd.equals("list")) {
                client.onList(String.format("%s;%d;%s;%s", "pop.163.com", 110, "自己填写", "自己填写"));
                continue;
            }

            if (cmd.equals("rest")) {
                client.onRest();
                continue;
            }

            if (cmd.equals("quit")) {
                client.onQuit();
                continue;
            }

            String[] parts = cmd.split(" ");
            if (parts.length < 2) {
                System.out.println("Error: Invalid command");
                continue;
            }

            if (parts[0].equals("retr")) {
                client.onRetr(String.format("%s;%d;%s;%s", "pop.163.com", 110, "MillionAura", "VGAJPXFUSLAFJJIJ"),
                        Integer.parseInt(parts[1]));
            } else if (parts[0].equals("dele")) {
                client.onDele(String.format("%s;%d;%s;%s", "pop.163.com", 110, "MillionAura", "VGAJPXFUSLAFJJIJ"),
                        Integer.parseInt(parts[1]));
                System.out.println("该邮件标记完毕。如删除请执行quit命令");
            } else {
                System.out.println("Error: Invalid command");
            }
        }
    }
}
