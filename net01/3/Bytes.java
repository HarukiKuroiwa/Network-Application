import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Bytes {
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    long start = System.currentTimeMillis();
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 2000);
      System.out.println("CONNECTED");

      byte[] message = "こん".getBytes(StandardCharsets.UTF_8);
      OutputStream out = socket.getOutputStream();
      out.write(message);
      out.write(0x0A);
      out.flush();
      System.out.println("SEND_BYTES=" + (message.length + 1));

      byte[] response = new byte[64];
      int read = socket.getInputStream().read(response);
      int byteCount = Math.max(read, 0);
      System.out.println("READ_BYTES=" + byteCount);

      StringBuilder hex = new StringBuilder();
      for (int i = 0; i < byteCount; i++) {
        hex.append(String.format("%02X ", response[i] & 0xFF));
      }
      System.out.println(hex);
      System.out.println(new String(response, 0, byteCount, StandardCharsets.UTF_8));
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