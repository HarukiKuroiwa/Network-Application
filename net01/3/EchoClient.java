import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;

public class EchoClient {
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String mode = args[2];
    Charset charset = Charset.forName(args[3]);
    String charsetName = args[3];
    String text;
    if ("ASCII".equals(mode)) {
      text = "HELLO";
    } else if ("JP".equals(mode)) {
      text = "こんにちは";
    } else {
      throw new IllegalArgumentException("Mode must be ASCII or JP");
    }

    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 2000);

      byte[] message = text.getBytes(charset);
      OutputStream out = socket.getOutputStream();
      out.write(message);
      out.write(0x0A);
      out.flush();

      byte[] response = new byte[64];
      InputStream in = socket.getInputStream();
      int byteCount = 0;
      while (byteCount < response.length) {
        int read = in.read(response, byteCount, response.length - byteCount);
        if (read == -1) {
          break;
        }
        byteCount += read;
        if (response[byteCount - 1] == 0x0A) {
          break;
        }
      }

      byte[] received = Arrays.copyOf(response, byteCount);
      byte[] sent = Arrays.copyOf(message, message.length + 1);
      sent[sent.length - 1] = 0x0A;
      System.out.println("HOST=" + host);
      System.out.println("PORT=" + port);
      System.out.println("CHARSET=" + charsetName);
      System.out.println("SEND_CHARS=" + text.length());
      System.out.println("SEND_BYTES=" + sent.length);
      System.out.println("SEND_HEX=" + toHex(sent));
      System.out.println("RECV_BYTES=" + received.length);
      System.out.println("RECV_HEX=" + toHex(received));
      System.out.println("MATCH=" + Arrays.equals(sent, received));
    } catch (UnknownHostException e) {
      printError(host, port, charsetName, "UNKNOWN_HOST");
    } catch (SocketTimeoutException e) {
      printError(host, port, charsetName, "TIMEOUT");
    } catch (ConnectException e) {
      printError(host, port, charsetName, "REFUSED");
    } catch (Exception e) {
      printError(host, port, charsetName, e.getClass().getSimpleName());
    }
  }

  private static String toHex(byte[] bytes) {
    StringBuilder hex = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      if (i > 0) {
        hex.append(' ');
      }
      hex.append(String.format("%02X", bytes[i] & 0xFF));
    }
    return hex.toString();
  }

  private static void printError(String host, int port, String charset, String error) {
    System.out.println("HOST=" + host);
    System.out.println("PORT=" + port);
    System.out.println("CHARSET=" + charset);
    System.out.println("ERROR=" + error);
  }
}