// =====================================================================
//  ネットワーク応用  第1回 演習用  EchoServer
//
//  ★ 学生に配布するのは EchoServer.jar だけにしてください。
//     このソースは第3回（ServerSocket）の答えそのものです。
//
//  jar 化：
//     javac -encoding UTF-8 EchoServer.java
//     jar --create --file EchoServer.jar --main-class EchoServer EchoServer.class
//
//  起動：
//     java -jar EchoServer.jar 40001
// =====================================================================

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class EchoServer {

    private static final int DEFAULT_PORT = 40001;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("ポート番号が数値ではありません: " + args[0]);
                System.exit(1);
            }
        }
        if (port < 1024 || port > 65535) {
            System.err.println("この授業では 1024 以上のポートを使ってください: " + port);
            System.exit(1);
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("listening on " + port + "   （止めるときは Ctrl + C）");
            while (true) {
                try (Socket s = server.accept();
                     BufferedReader in = new BufferedReader(
                         new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter out = new BufferedWriter(
                         new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

                    System.out.println("connected: " + s.getRemoteSocketAddress());
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.write(line);
                        out.write("\n");
                        out.flush();
                    }
                    System.out.println("closed:    " + s.getRemoteSocketAddress());

                } catch (IOException e) {
                    System.out.println("client error: " + e.getMessage());
                }
            }
        } catch (BindException e) {
            System.err.println("ポート " + port + " はすでに使われています。");
            System.err.println("前に起動した EchoServer がまだ生きていないか確認してください。");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("起動できませんでした: " + e.getMessage());
            System.exit(1);
        }
    }
}
