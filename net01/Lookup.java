// コマンドライン引数で受け取ったホスト名を InetAddress で解決し、
// ホスト名とIPアドレスを1行ずつ表示する。
// 解決できなかった場合は UnknownHostException を捕まえて
// "UNKNOWN_HOST" と表示する。
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Lookup {
    public static void main(String[] args) {
        for (String hostname : args) {
            try {
                InetAddress address = InetAddress.getByName(hostname);
                System.out.println(hostname + " => " + address.getHostAddress());
            } catch (UnknownHostException e) {
                System.out.println(hostname + " => UNKNOWN_HOST");
            }
        }
    }
}