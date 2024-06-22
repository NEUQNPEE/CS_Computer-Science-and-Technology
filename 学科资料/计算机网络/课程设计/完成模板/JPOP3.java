package JPOP3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class JPOP3 {

    private static final int POP3_PORT = 110;
    private static final int RESPONSE_BUFFER_SIZE = 1024;

    public JPOP3(String pop3ServerName, int port, String username, String password) {
        if (pop3ServerName == null || pop3ServerName.isEmpty()) {
            return;
        }

        _pop3ServerHostName = pop3ServerName;
        _port = port;
        _username = username;
        _password = password;
        _connected = false;
        _error = "OK";
    }

    public JPOP3(String pop3ServerName) {
        this(pop3ServerName, POP3_PORT, null, null);
    }

    public void setServerProperties(String serverHostName, int port) {
        if (serverHostName == null || serverHostName.isEmpty()) {
            return;
        }

        _pop3ServerHostName = serverHostName;
        _port = port;
    }

    public void setServerProperties(String serverHostName) {
        setServerProperties(serverHostName, POP3_PORT);
    }

    public void setUserProperties(String username, String password) {
        if (username == null || username.isEmpty()) {
            return;
        }

        if (password == null || password.isEmpty()) {
            return;
        }

        _username = username;
        _password = password;
    }

    public String getLastError() {
        return _error;
    }

    public int getPort() {
        return _port;
    }

    public String getServerHostName() {
        return _pop3ServerHostName;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }

    public boolean connect() {
        if (_connected) {
            return true;
        }

        try {
            _pop3Server = new Socket(_pop3ServerHostName, _port);
            InputStream in = _pop3Server.getInputStream();
            OutputStream out = _pop3Server.getOutputStream();
            if (!getResponse(POP3Response.CONNECTION)) {
                return false;
            }

            String user = String.format("USER %s\r\n", getUsername());
            out.write(user.getBytes());

            if (!getResponse(POP3Response.IDENTIFICATION)) {
                return false;
            }

            String pass = String.format("PASS %s\r\n", getPassword());
            out.write(pass.getBytes());

            if (!getResponse(POP3Response.AUTHENTICATION)) {
                return false;
            }

            _connected = true;
            return true;
        } catch (IOException e) {
            _error = "Unable to connect to server";
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnect() {
        if (!_connected) {
            return true;
        }

        String quit = "QUIT\r\n";
        try {
            _pop3Server.getOutputStream().write(quit.getBytes());
            boolean ret = getResponse(POP3Response.QUIT);
            _pop3Server.close();
            _connected = false;
            return ret;
        } catch (IOException e) {
            _error = "Error while quitting";
            e.printStackTrace();
            return false;
        }
    }

    public int getNumMessages() {
        String stat = "STAT\r\n";
        try {
            _pop3Server.getOutputStream().write(stat.getBytes());
            if (!getResponse(POP3Response.STATUS)) {
                return -1;
            }

            int firstDigit = findFirstDigit(_response);

            if (firstDigit < 0) {
                return -1;
            }

            return Integer.parseInt(_response.substring(firstDigit, _response.indexOf(" ", firstDigit)));

        } catch (IOException e) {
            _error = "Error while retrieving status";
            e.printStackTrace();
            return -1;
        }
    }

    private int findFirstDigit(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public String getMailList() {
        String list = "LIST\r\n";
        try {
            _pop3Server.getOutputStream().write(list.getBytes());
            if (!getResponse(POP3Response.STATUS)) {
                return "邮件列表获取失败";
            }
        } catch (IOException e) {
            _error = "Error while retrieving message list";
            e.printStackTrace();
        }

        // 跳过第一个回车换行符
        return _response.substring(_response.indexOf("\r\n") + 2);
    }

    public boolean getMessage(int msg, JMailMessage message) {
        String retr = String.format("RETR %d\r\n", msg);
        try {
            _pop3Server.getOutputStream().write(retr.getBytes());
            if (!getResponse(POP3Response.RETRIEVE)) {
                return false;
            }

            String messageContent = _response;

            while (messageContent.indexOf("\r\n.\r\n") < 0) {
                byte[] responseBuffer = new byte[RESPONSE_BUFFER_SIZE];
                int nChars = _pop3Server.getInputStream().read(responseBuffer);
                if (nChars == -1) {
                    return false;
                }
                _response = new String(responseBuffer, 0, nChars);
                messageContent += _response.substring(0, nChars);
            }

            messageContent = messageContent.substring(messageContent.indexOf("\r\n") + 2, messageContent.length() - 3);

            int br = messageContent.indexOf("\r\n\r\n");
            message._header = messageContent.substring(0, br);
            message._body = messageContent.substring(br + 4);
            message.decodeHeader();
            message.decodeBody();
            return true;

        } catch (IOException e) {
            _error = "Error while retrieving message";
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMessage(int msg) {
        String dele = String.format("DELE %d\r\n", msg);
        try {
            _pop3Server.getOutputStream().write(dele.getBytes());
            return getResponse(POP3Response.DELETE);
        } catch (IOException e) {
            _error = "Error while deleting message";
            e.printStackTrace();
            return false;
        }
    }

    public boolean undeleteMessage() {
        String dele = String.format("RSET\r\n");
        try {
            _pop3Server.getOutputStream().write(dele.getBytes());
            return getResponse(POP3Response.DELETE);
        } catch (IOException e) {
            _error = "Error while deleting message";
            e.printStackTrace();
            return false;
        }
    }

    private boolean getResponse(POP3Response action) {
        byte[] responseBuffer = new byte[RESPONSE_BUFFER_SIZE];
        try {
            int nChars = _pop3Server.getInputStream().read(responseBuffer);
            if (nChars == -1) {
                _error = "Socket Error";
                return false;
            }

            _response = new String(responseBuffer, 0, nChars);

            if (_response.startsWith("-ERR")) {
                _error = errorTable[action.ordinal()];
                return false;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String _error;
    private String _response;
    private boolean _connected;
    private int _port;
    private String _pop3ServerHostName;
    private String _username;
    private String _password;
    private Socket _pop3Server;

    protected enum POP3Response {
        CONNECTION,
        IDENTIFICATION,
        AUTHENTICATION,
        STATUS,
        RETRIEVE,
        DELETE,
        QUIT
    }

    String responseBuffer;
    static String[] errorTable = {
            "Server didn't connect",
            "Bad user name",
            "Invalid username/passwond combination",
            "Status couldn't be retrieved",
            "Retrieve failed",
            "Could not delete message",
            "Error while quitting"
    };

}
