package job.devices.lcdutils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrameDump {
  public static void dump(byte[] ba) {
    String frame = IntStream.range(0, ba.length)
            .map(idx -> ba[idx])
            .boxed()
            .map(b -> String.format("%02X", (b & 0xFF)))
            .collect(Collectors.joining(" "));
    dump(frame);
  }

  public static void dump(String frame) {
    String[] data = frame.split(" ");
    System.out.println(String.format("There are %d entry(ies).", data.length));
    if (data.length == 512) {
      for (int row=0; row<4; row++) {
        for (int bit=0; bit<8; bit++) {
          for (int col=0; col<128; col++) {
            byte b = (byte)Integer.parseInt(data[(row * 128) + col], 16);
            System.out.print(String.format("%s", ((b & (1 << bit)) == 0) ? " " : "X"));
          }
          System.out.println();
        }
      }
    }
  }
}
