import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MailNet {
    private String SMTP_SERVER; // = "smtp.naver.com";
    private int SMTPS_PORT; // 465

    private SSLSocketFactory factory;
    private SSLSocket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private String senderEmail;
    private String appPassword;

    // IMAP 관련 변수들
    private String IMAP_SERVER; // = "imap.naver.com";
    private int IMAP_PORT; // 993

    private SSLSocketFactory imap_factory;
    private SSLSocket imap_socket;

    private BufferedReader imap_reader;
    private BufferedWriter imap_writer;

    public String date_sub_from;
    public String text;

    private boolean loginFlag = true;
    public void SocketSetup(int port, String server) throws IOException {
        SMTP_SERVER = server;
        SMTPS_PORT = port;

        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(SMTP_SERVER, SMTPS_PORT);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }


    public boolean AuthLogin(String senderEmail, char[] appPassword) throws IOException {
        loginFlag = true;
        this.senderEmail = senderEmail;
        this.appPassword = new String(appPassword);

        // SMTP 서버의 응답 메시지 출력
        System.out.println(reader.readLine());

        // HELO/EHLO
        sendCommand(writer, reader, "EHLO localhost");

        // 로그인 (AUTH LOGIN)
        sendCommand(writer, reader, "AUTH LOGIN");

        return loginFlag;
    }

    public boolean sendMail_login(String to, String subject, String text) throws IOException {
        sendCommand(writer, reader, encodeBase64(senderEmail));
        sendCommand(writer, reader, encodeBase64(appPassword));

        // MAIL FROM, RCPT TO
        sendCommand(writer, reader, "MAIL FROM: <"+senderEmail+">");
        sendCommand(writer, reader, "RCPT TO: <"+to+">");

        // DATA
        sendCommand(writer, reader, "DATA");
        writer.write("Subject: "+subject+"\r\n");
        writer.write("From: "+senderEmail+"\r\n");
        writer.write("To: "+to+"\r\n\r\n");
        writer.write(text + "\r\n.\r\n");
        writer.flush();
        System.out.println(reader.readLine());

        return loginFlag;
    }

    public void sendMail(String to, String subject, String text) throws IOException {
        sendCommand(writer, reader, encodeBase64(senderEmail));
        sendCommand(writer, reader, encodeBase64(appPassword));

        // MAIL FROM, RCPT TO
        sendCommand(writer, reader, "MAIL FROM: <"+senderEmail+">");
        sendCommand(writer, reader, "RCPT TO: <"+to+">");

        // DATA
        sendCommand(writer, reader, "DATA");
        writer.write("Subject: "+subject+"\r\n");
        writer.write("From: "+senderEmail+"\r\n");
        writer.write("To: "+to+"\r\n\r\n");
        writer.write(text + "\r\n.\r\n");
        writer.flush();
        System.out.println(reader.readLine());

    }
    
    public void quit() throws IOException {
        // QUIT
        sendCommand(writer, reader, "QUIT");
        reader.close();
        writer.close();
        socket.close();

        loginFlag = false;
    }

    private void sendCommand(BufferedWriter writer, BufferedReader reader, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("> " + command);

        String response;
        do {
            response = reader.readLine();
            System.out.println(response);
            if (response.startsWith("535 5.7.1"))
            { loginFlag  = false; }
        } while(response.charAt(3) == '-');
    }

    private String encodeBase64(String input) {
        return new String(Base64.encodeBase64(input.getBytes()));
    }


    // 아래는 ** IMAP ** 관련 함수들 //
    // ------------------------- //
    public void IMAPSocketSetup(int port, String server) throws IOException {
        IMAP_PORT = port;
        IMAP_SERVER = server;

        imap_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        imap_socket = (SSLSocket) factory.createSocket(IMAP_SERVER, IMAP_PORT);

        imap_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        imap_writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void IMAPGetMSG() throws IOException{
        String command = "A0001 LOGIN " + senderEmail + " " + appPassword;
        IMAPsendCommand(imap_writer, imap_reader, command, "A0001");

        command = "A0002 SELECT INBOX";
        IMAPsendCommand(imap_writer, imap_reader, command, "A0002");

        int uid = 1000;
        int cnt_mail = 3;
        for (int i = 0 ; i < cnt_mail ; i++) {
            int uid_tmp = uid - i;
            command = "A0003 FETCH " + uid_tmp + " (FLAGS BODY[HEADER.FIELDS (DATE SUBJECT FROM)])";
            IMAPfetchCommand(imap_writer, imap_reader, command, "A0003", "DSF");

            command = "A0003 FETCH " + uid_tmp + " (FLAGS BODY[TEXT])";
            IMAPfetchCommand(imap_writer, imap_reader, command, "A0003", "TEXT");
        }

        command = "A0004 LOGOUT";
    }
    private void logout(BufferedWriter writer, BufferedReader reader, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        String res_logout = reader.readLine();
        System.out.println(res_logout);
        socket.close();
    }
    private void IMAPsendCommand(BufferedWriter writer, BufferedReader reader, String command, String tag) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("> " + command);
        String response;
        do {
            response = reader.readLine();
            System.out.println(response);
        } while(response.startsWith(tag));

    }

    private void IMAPfetchCommand(BufferedWriter writer, BufferedReader reader, String command, String tag, String field) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("> " + command);

        String response;
        if (field.equals("DSF")){
            do {
                response = reader.readLine();
                System.out.println(response);
                String decode_fetch = parseEmailResponse(response);
                if (decode_fetch.isEmpty())
                    continue;

                date_sub_from += decode_fetch;


            } while(response.startsWith(tag));

        } else if (field.equals("TEXT")){
            do {
                response = reader.readLine();
                System.out.println(response);
                if (response.startsWith("* 1000 FETCH"))
                    continue;

                text += response;

            } while(response.startsWith(tag));

            text = text.replaceAll("=.*", "");
            String decode_fetch = parseEmailResponse(text);
        }

    }

    public static String decodeBase64String(String base64EncodedString, String field) {
        try {
            String decodedString = "";

            if (field.equals("DSF")){
                // 데이터 형식이 다음과 같음.
                // SUBJECT: =?EUC-KR?B?W73Fx9HEq7XlXSC9xcfRcGF5ILytuvG9uiDAzL/rvuCw/CCws8GkIL7Is7s=?=
                // 그래서 encoding 방식인 EUC-KR과 디코딩 해야할 내용인 B? 다음부터 ?= 전까지인 내용을 나눠야함.

                String[] parts = base64EncodedString.split("\\?");

                if (parts.length >= 4) {
                    String encoding = parts[1]; // encoding 방식
                    String encodedString = parts[3]; // 내용
                    // Base64 디코딩
                    byte[] base64DecodedBytes = java.util.Base64.getDecoder().decode(encodedString);

                    // UTF-8 디코딩
                    decodedString = new String(base64DecodedBytes, encoding);
                } else {

                    byte[] base64DecodedBytes = java.util.Base64.getDecoder().decode(base64EncodedString);
                    decodedString = new String(base64DecodedBytes, StandardCharsets.UTF_8);
                }

            } else if (field.equals("TEXT")) {
                // 본문 내용은 그냥 UTF-8임
                byte[] base64DecodedBytes = java.util.Base64.getDecoder().decode(base64EncodedString);
                decodedString = new String(base64DecodedBytes, StandardCharsets.UTF_8);
            }

            return decodedString;

        } catch (Exception e) {
            return "";
        }
    }
    public static String parseEmailResponse(String fetchResponse) {

        String result;
        if (fetchResponse.startsWith("* 1000 FETCH")) {
            result = "";

        } else if (fetchResponse.startsWith("SUBJECT:")) {


            String encodedSubject = fetchResponse.substring(9).trim();
            if (Character.isLetter(encodedSubject.charAt(0))){
                result = "Subject: " + encodedSubject;
            }
            else{
                String decodedSubject = decodeBase64String(encodedSubject, "DSF");
                result = "Subject: " + decodedSubject;
            }

        } else if (fetchResponse.startsWith("FROM:")) {

            String encodedFrom = fetchResponse.substring(6).trim();
            if (Character.isLetter(encodedFrom.charAt(0))){
                result = "From: " + encodedFrom;
            }
            else{
                String decodedFrom = decodeBase64String(encodedFrom, "DSF");
                result = "From: " + decodedFrom;
            }


        } else if (fetchResponse.startsWith("DATE:")){
            result = fetchResponse;

        } else{
            // 메이비 본문 내용
            String decodedText = decodeBase64String(fetchResponse, "TEXT");
            result = decodedText;
        }

        return result;
    }
}

