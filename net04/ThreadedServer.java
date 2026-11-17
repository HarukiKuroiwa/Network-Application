import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// ============================================================================
//  第4回 演習A で書き換えるサーバ（ThreadedServer.java）
// ----------------------------------------------------------------------------
//  中身は、同じフォルダの LineServer.java（第3回の課題の解答例）をコピーして、
//  クラス名だけ LineServer → ThreadedServer に直したものです。
//  まだスレッドは使っていません（第3回と同じ動き。2人目は待たされます）。
//  演習A で、main の while の中をコメントから Copilot に書かせて直します。
//  コンパイル：javac -encoding UTF-8 ThreadedServer.java
//  実行      ：java ThreadedServer        （ポート番号を省略すると 40001）
// ============================================================================
public class ThreadedServer {
  public static void main(String[] args) throws IOException {
    int port = (args.length > 0) ? Integer.parseInt(args[0]) : 40001;   // 省略したら 40001

    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("LISTENING port=" + port);

      while (true) {
        // 接続を受け付けたら、その Socket を引数にして serve を呼ぶ新しいスレッドを作り、すぐに開始する。
        // ★ ここでは try ( ) を使わない。Socket を閉じるのは serve の仕事。
        Socket sock = server.accept();
        new Thread(() -> serve(sock)).start();
      }
    }
  }

  // 1人ぶんの会話をする static メソッド serve(Socket sock)。
  // 最初に System.err へ「ACCEPTED 接続元のアドレス thread=いまのスレッドの名前」を1行出す。
  // try-with-resources で sock を閉じるようにして、中で第3回の handle を呼ぶ。
  // IOException は「client error: メッセージ」として System.err に1行出す。
  static void serve(Socket sock) {
    System.err.println("ACCEPTED " + sock.getRemoteSocketAddress() + " thread=" + Thread.currentThread().getName());
    try (Socket s = sock) {
      handle(s);
    } catch (IOException e) {
      System.err.println("client error: " + e.getMessage());
    }
  }


  // 1本の接続を最後まで相手する。QUIT か切断で戻る。
  static void handle(Socket s) throws IOException {
    BufferedReader in = new BufferedReader(
        new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
    BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));

    String line;
    while ((line = in.readLine()) != null) {
      System.err.println("RECEIVED " + line);
      if (line.equals("QUIT")) {
        send(out, "BYE");
        return;                         // この接続だけを閉じる（サーバは続ける）
      }
      send(out, reply(line));
    }
  }

  static String reply(String line) {
    if (line.equals("PING")) {
      return "PONG";
    }
    int sp = line.indexOf(' ');
    if (sp > 0) {
      String cmd = line.substring(0, sp);
      String arg = line.substring(sp + 1);   // 最初の半角スペースより後ろ、全部
      if (!arg.isEmpty()) {
        if (cmd.equals("ECHO")) {
          return arg;                        // trim しない
        }
        if (cmd.equals("LEN")) {
          return String.valueOf(arg.getBytes(StandardCharsets.UTF_8).length);
        }
      }
    }
    return "ERR unknown command";
  }

  static void send(BufferedWriter out, String text) throws IOException {
    out.write(text);
    out.write("\n");                     // 0x0A を1バイト。newLine() は使わない
    out.flush();
  }
}
