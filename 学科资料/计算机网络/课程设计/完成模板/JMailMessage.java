package JPOP3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JMailMessage {

    public JMailMessage() {
        _from = "";
        _subject = "";
        _header = "";
        _date = "";
        _dateTime = new Date();
        _body = "";
    }

    public int getNumRecipients() {
        return _recipients.size();
    }

    public boolean getRecipient(String emailAddr, String friendlyName, int index) {
        if (index < 0 || index > _recipients.size()) {
            return false;
        }

        Recipient recipient = _recipients.get(index);
        emailAddr = recipient._emailAddr;
        friendlyName = recipient._friendlyName;
        return true;
    }

    public boolean addRecipient(String emailAddr, String friendlyName) {
        if (emailAddr == null || emailAddr.isEmpty()) {
            return false;
        }

        if (friendlyName == null || friendlyName.isEmpty()) {
            return false;
        }

        Recipient recipient = new Recipient(emailAddr, friendlyName);
        _recipients.add(recipient);

        return true;
    }

    // 每一条接收者的信息用分号隔开
    public boolean addMultipleRecipients(String recipients) {
        String[] parts = recipients.split(";");
        for (String part : parts) {
            String[] subparts = part.split("<");
            if (subparts.length < 2) {
                continue;
            }

            String friendlyName = subparts[0].trim();
            String emailAddr = subparts[1].replace(">", "").trim();

            addRecipient(emailAddr, friendlyName);
        }

        return true;
    }

    public boolean encodeHeader() {
        String To = "";

        if (getNumRecipients() <= 0) {
            return false;
        }

        _header = "";
        String email = "";
        String friendly = "";

        for (int i = 0; i < getNumRecipients(); i++) {
            getRecipient(email, friendly, i);
            To += (i > 0 ? ", " : "")
                    + friendly
                    + " <"
                    + email
                    + ">";
        }

        _dateTime = new Date();
        // 时间格式为：Thu, 01 Jul 2021 12:00:00 GMT
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        String Date = sdf.format(_dateTime);

        _header = String.format("Date: %s\r\n"
                + "From: %s\r\n"
                + "To: %s\r\n"
                + "Subject: %s\r\n",
                Date, _from, To, _subject);

        return true;
    }

    public boolean decodeHeader() {
        // 开始解析头部
        String[] lines = _header.split("\r\n");
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length < 2) {
                continue;
            }

            String key = parts[0].trim();
            String value = parts[1].trim();

            switch (key) {
                case "Date":
                    _date = value;
                    _dateTime = new Date();
                    break;
                case "From":
                    _from = value;
                    break;
                case "Subject":
                    _subject = value;
                    break;
                case "To":
                    addMultipleRecipients(value);
                    break;
                case "Cc":
                    addMultipleRecipients(value);
            }
        }

        return true;
    }

    public void encodeBody() {
        _body = _body.replace("\r\n.\r\n", "\r\n..\r\n");
    }

    public void decodeBody() {
        _body = _body.replace("\r\n..\r\n", "\r\n.\r\n");
    }

    public String _from;
    public String _subject;
    public String _header;
    public String _date;
    public Date _dateTime;
    public String _body;

    private class Recipient {
        public String _emailAddr;
        public String _friendlyName;

        public Recipient(String emailAddr, String friendlyName) {
            _emailAddr = emailAddr;
            _friendlyName = friendlyName;
        }
    }

    ArrayList<Recipient> _recipients = new ArrayList<>();

}
