import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// 第3回 演習A：1回だけ受け付けるサーバ
//   4つの動詞（bind / listen / accept / close）が1つずつ見えるように、
//   あえて try-with-resources を使わずに書いてある（演習Bで直す）。
//
//   javac -encoding UTF-8 OneShot.java
//   java OneShot 40001
public class LoopServer {
  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[0]);

    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("LISTENING port=" + port);

      while (true) { // 1人ずつ、順番に
        try (Socket s = server.accept();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

          System.err.println("ACCEPTED " + s.getRemoteSocketAddress());

          String line;
          while ((line = in.readLine()) != null) { // 相手が切断すると null になる
            System.err.println("RECEIVED " + line);
            out.write(line);
            out.write("\n");
            out.flush();
          }
          System.err.println("CLOSED");

        } catch (IOException e) {
          System.err.println("client error: " + e.getMessage()); // 落ちない
        }
      }
    }
  }
}
