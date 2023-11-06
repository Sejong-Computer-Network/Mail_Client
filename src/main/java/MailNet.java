import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    public String[] mailText = new String[3];
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
        this.senderEmail = senderEmail;
        this.appPassword = new String(appPassword);

        // SMTP 서버의 응답 메시지 출력
        System.out.println(reader.readLine());

        // HELO/EHLO
        sendCommand(writer, reader, "EHLO localhost");

        // 로그인 (AUTH LOGIN)
        sendCommand(writer, reader, "AUTH LOGIN");
        sendCommand(writer, reader, encodeBase64(senderEmail));
        sendCommand(writer, reader, encodeBase64(this.appPassword));

        loginFlag = true;
    }


    public void sendMail(String to, String subject, String text, File file) throws IOException {
        // MAIL FROM, RCPT TO
        sendCommand(writer, reader, "MAIL FROM: <" + senderEmail + ">");
        sendCommand(writer, reader, "RCPT TO: <" + to + ">");

        // DATA
        sendCommand(writer, reader, "DATA");

        String boundary = "===" + System.currentTimeMillis() + "===";

        // MIME 헤더 작성
        writer.write("MIME-Version: 1.0\r\n");
        writer.write("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");

        writer.write("Subject: " + subject + "\r\n");
        writer.write("From: " + senderEmail + "\r\n");
        writer.write("To: " + to + "\r\n\r\n");

        // 이메일 본문 작성
        writer.write("--" + boundary + "\r\n");
        writer.write("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
        writer.write(text + "\r\n\r\n");

        // 첨부 파일 처리
        if (file != null && file.exists()) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encodedFile = java.util.Base64.getEncoder().encodeToString(fileBytes);
            writer.write("--" + boundary + "\r\n");
            writer.write("Content-Type: application/octet-stream; name=\"" + file.getName() + "\"\r\n");
            writer.write("Content-Transfer-Encoding: base64\r\n");
            writer.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n\r\n");
            writer.write(encodedFile);
            writer.write("\r\n");
            writer.write("--" + boundary + "--\r\n");
        }

        // 이메일 종료
        writer.write(".\r\n");
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
        imap_socket = (SSLSocket) imap_factory.createSocket(IMAP_SERVER, IMAP_PORT);

        imap_reader = new BufferedReader(new InputStreamReader(imap_socket.getInputStream()));
        imap_writer = new BufferedWriter(new OutputStreamWriter(imap_socket.getOutputStream()));
    }

    public void IMAPGetMSG() throws IOException{
        String command = "A0001 LOGIN " + senderEmail + " " + appPassword + "\r\n";
        String check;

        check = IMAPsendCommand(imap_writer, imap_reader, command, "A0001");

        if (check.startsWith("A0001 OK")){
            command = "A0002 SELECT INBOX\r\n";
            check = IMAPsendCommand(imap_writer, imap_reader, command, "A0002");

            if (check.startsWith("A0002 OK")){
                int uid = 1000;
                int cnt_mail = 3;
                for (int i = 0 ; i < cnt_mail ; i++) {
                    int uid_tmp = uid - i;
                    command = "A0003 FETCH " + uid_tmp + " (FLAGS BODY[HEADER.FIELDS (DATE SUBJECT FROM)])\r\n";
                    String dsf = IMAPfetchCommand(imap_writer, imap_reader, command, "A0003", "DSF", uid_tmp);

                    command = "A0003 FETCH " + uid_tmp + " (FLAGS BODY[TEXT])\r\n";
                    String txt = IMAPfetchCommand(imap_writer, imap_reader, command, "A0003", "TEXT", uid_tmp);

                    mailText[i] += dsf + "TEXT: " +txt;

                    mailText[i] = mailText[i].replace("null", "");
                }
            }

        }


    }
    private void logout(BufferedWriter writer, BufferedReader reader) throws IOException {
        String command = "A0004 LOGOUT";
        writer.write(command);
        writer.flush();
        String res_logout = reader.readLine();
        System.out.println(res_logout);
        socket.close();
    }
    private String IMAPsendCommand(BufferedWriter writer, BufferedReader reader, String command, String tag) throws IOException {
        writer.write(command);
        writer.flush();
        System.out.println("> " + command);
        String response;
        while (true) {
            response = reader.readLine();
            System.out.println(response);
            if (response.startsWith(tag))
                break;
        }
        return response;
    }

    private String IMAPfetchCommand(BufferedWriter writer, BufferedReader reader, String command, String tag, String field, int uid) throws IOException {
        writer.write(command);
        writer.flush();
        System.out.println("> " + command);

        String date_sub_from = "";
        String text= "";
        String text_tmp = "";
        String response;
        String check = "* " + uid + " FETCH";
        if (field.equals("DSF")){
            while (true) {
                response = reader.readLine();
                if (response.equals("A0003 OK Fetch completed."))
                    break;

                // 요녀석이 우리가 필요한 DATE, SUBJECT, FROM 문자열 받아오는 부분
                // 한번에 한줄씩 받아옴.
                String decode_fetch = parseEmailResponse(response, uid);

                // 중간 중간 이상치(필요없는 데이터)가 있는 경우 ""값이 반환되기 때문에 한번 필터링
                if (decode_fetch == null || decode_fetch.trim().isEmpty())
                    continue;

                System.out.println(decode_fetch);
                date_sub_from += decode_fetch + "\n";
            }
            return date_sub_from;

        } else if (field.equals("TEXT")){

            while (true) {
                response = reader.readLine();
                System.out.println(response);
                // Fetch 응답 값으로 맨 처음 오는 값. 별 의미가 없기에 무시
                if (response.startsWith(check))
                    continue;

                // 처리 끝나면 오는 응답.
                if (response.equals("A0003 OK Fetch completed."))
                    break;

                // 을 제외한 본문 내용만 가져올 것이다.
                text_tmp += response;

            }
            text_tmp = text_tmp.replaceAll("=.*", "");
            text = parseEmailResponse(text_tmp, uid);
            text += "\n";
            System.out.println(text);
        }
        return text;

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
    public static String parseEmailResponse(String fetchResponse, int uid) {

        String result;
        String check= "* " + uid + " FETCH";
        if (fetchResponse.startsWith(check)) {
            result = "";

        } else if (fetchResponse.startsWith("SUBJECT:")) {

            String encodedSubject = fetchResponse.substring(9).trim();
            if (Character.isLetter(encodedSubject.charAt(0))){
                result = "SUBJECT: " + encodedSubject;
            }
            else{
                String decodedSubject = decodeBase64String(encodedSubject, "DSF");
                result = "SUBJECT: " + decodedSubject;
            }

        } else if (fetchResponse.startsWith("FROM:")) {

            String encodedFrom = fetchResponse.substring(6).trim();
            if (Character.isLetter(encodedFrom.charAt(0))){
                result = "FROM: " + encodedFrom;
            }
            else{
                String decodedFrom = decodeBase64String(encodedFrom, "DSF");
                result = "FROM: " + decodedFrom;
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

