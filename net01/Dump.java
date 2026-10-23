import java.net.*;
import java.nio.charset.StandardCharsets;

public class Dump {
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    long start = System.currentTimeMillis();
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 2000);
      System.out.println("CONNECTED");
      socket.getOutputStream().write("HELLO\n".getBytes(StandardCharsets.UTF_8));
      socket.getOutputStream().flush();

      byte[] response = new byte[64];
      int length = socket.getInputStream().read(response);
      if (length < 0) {
        length = 0;
      }

      System.out.print("HEX: ");
      for (int i = 0; i < length; i++) {
        System.out.printf("%02X ", response[i] & 0xFF);
      }
      System.out.println();
      System.out.println("UTF-8: " + new String(response, 0, length, StandardCharsets.UTF_8));
    } catch (UnknownHostException e) {
      System.out.println("UNKNOWN_HOST");
    } catch (SocketTimeoutException e) {
      System.out.println("TIMEOUT");
    } catch (ConnectException e) {
      System.out.println("REFUSED");
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getClass().getSimpleName());
    }
    System.out.println("ELAPSED_MS=" + (System.currentTimeMillis() - start));
  }
}