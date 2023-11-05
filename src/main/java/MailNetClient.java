import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class MailNetClient {
    private String SMTP_SERVER; // = "smtp.naver.com";
    private int SMTPS_PORT; // 465

    private SSLSocketFactory factory;
    private SSLSocket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private String senderEmail;
    private String appPassword;

    private boolean loginFlag = true;
    public void SocketSetup(int port, String server) throws IOException {
        SMTP_SERVER = server;
        SMTPS_PORT = port;

        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(SMTP_SERVER, SMTPS_PORT);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }


    public void AuthLogin(String senderEmail, char[] appPassword) throws IOException {
        this.senderEmail = senderEmail;
        this.appPassword = new String(appPassword);

        // SMTP 서버의 응답 메시지 출력
        System.out.println(reader.readLine());

        // HELO/EHLO
        sendCommand(writer, reader, "EHLO localhost");

        // 로그인 (AUTH LOGIN)
        sendCommand(writer, reader, "AUTH LOGIN");

        loginFlag = true;
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
}

