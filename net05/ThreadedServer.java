import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// ============================================================================
//  第5回 配布：ThreadedServer.java（今日の出発点）
// ----------------------------------------------------------------------------
//  第4回で作った「待たせないサーバ」の完成形です。今日は、このファイルに足していきます。
//  足す場所には、資料と同じ番号の目印を ★ で付けてあります。
//    授業（第5回）で使う：A-1 の①〜③（B-2・B-3 も同じ場所を書き換える）
//    中間レポートの課題2で使う：課題2 の①〜④
//
//      javac -encoding UTF-8 ThreadedServer.java
//      java ThreadedServer 40001
//
//  自分の第4回の ThreadedServer.java が動いている人は、そちらを使っても構いません。
// ============================================================================
public class ThreadedServer {

  // ★ A-1 ①：ここに、接続を数える変数 total を足す（黒板：全スレッドで1つ）

  // ★ 課題2 ①（中間レポート）：ここに、全員の出口の一覧 ALL を足す（黒板：全スレッドで1つ）

  public static void main(String[] args) throws IOException {
    int port = (args.length > 0) ? Integer.parseInt(args[0]) : 40001;

    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("LISTENING port=" + port);

      while (true) {
        Socket s = server.accept();                  // 受付係（main）は、迎えるだけ
        new Thread(() -> serve(s)).start();          // 会話は担当者（新しいスレッド）へ
      }
    }
  }

  // 1人ぶんの会話。担当者のスレッドで動く。ソケットを閉じるのも、ここの仕事
  static void serve(Socket sock) {
    // ★ A-1 ②：ここに total++; を足す（B-2・B-3 では、この1行を書き換える）

    System.err.println("ACCEPTED " + sock.getRemoteSocketAddress()
                     + " thread=" + Thread.currentThread().getName());
    try (Socket s = sock) {
      handle(s);
    } catch (IOException e) {
      System.err.println("client error: " + e.getMessage());   // 1人のエラーで、サーバ全体を止めない
    }
    System.err.println("CLOSED thread=" + Thread.currentThread().getName());
  }

  // 1本の接続を最後まで相手する。QUIT か切断で戻る（第3回と同じ）
  static void handle(Socket s) throws IOException {
    BufferedReader in = new BufferedReader(
        new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
    BufferedWriter out = new BufferedWriter(
        new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));

    // ★ 課題2 ②（中間レポート）：ここから下の while を、ALL.add(out); try { … } finally { ALL.remove(out); } で囲む

    String line;
    while ((line = in.readLine()) != null) {
      if (line.equals("QUIT")) {
        send(out, "BYE");
        return;                         // この接続だけを閉じる（サーバは続ける）
      }

      // ★ 課題2 ④（中間レポート）：ここに SAY の5行を足す

      send(out, reply(line));
    }
  }

  // ★ 課題2 ③（中間レポート）：ここに、全員に配る broadcast を足す

  static String reply(String line) {
    if (line.equals("PING")) return "PONG";

    // ★ A-1 ③：ここに TOTAL の1行を足す

    int sp = line.indexOf(' ');
    if (sp > 0) {
      String cmd = line.substring(0, sp);
      String arg = line.substring(sp + 1);
      if (!arg.isEmpty()) {
        if (cmd.equals("ECHO")) return arg;
        if (cmd.equals("LEN")) return String.valueOf(arg.getBytes(StandardCharsets.UTF_8).length);
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
