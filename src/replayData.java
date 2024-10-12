import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SevenZip.Compression.LZMA.Decoder;

public class replayData {

  int gameMode;
  int version;
  String beatmapMD5;
  String playerName;
  String replayHash;
  int num300;
  int num100;
  int num50;
  int numMax300;
  int num200;
  int numMisses;
  int score;
  int maxCombo;
  boolean fullCombo;
  int mods;
  String lifeGraph;
  long timestamp;
  int replayLength;
  byte[] replayData;

  public String parseOsrFile(File osrFile) throws IOException {

    try (DataInputStream dis = new DataInputStream(new FileInputStream(osrFile))) {
      StringBuilder sb = new StringBuilder();

      gameMode = dis.readUnsignedByte();
      sb.append("Game Mode: ").append(gameMode == 3 ? "osu!mania" : "Other").append("\n");

      version = Integer.reverseBytes(dis.readInt());
      sb.append("Version: ").append(version).append("\n");

      beatmapMD5 = readOsuString(dis);
      sb.append("Beatmap MD5 Hash: ").append(beatmapMD5).append("\n");

      playerName = readOsuString(dis);
      sb.append("Player Name: ").append(playerName).append("\n");

      replayHash = readOsuString(dis);
      sb.append("Replay Hash: ").append(replayHash).append("\n");

      num300 = readUnsignedShort(dis);
      sb.append("Number of 300s: ").append(num300).append("\n");

      num100 = readUnsignedShort(dis);
      sb.append("Number of 100s: ").append(num100).append("\n");

      num50 = readUnsignedShort(dis);
      sb.append("Number of 50s: ").append(num50).append("\n");

      numMax300 = readUnsignedShort(dis);
      sb.append("Number of MAX 300s: ").append(numMax300).append("\n");

      num200 = readUnsignedShort(dis);
      sb.append("Number of 200s: ").append(num200).append("\n");

      numMisses = readUnsignedShort(dis);
      sb.append("Number of Misses: ").append(numMisses).append("\n");

      score = Integer.reverseBytes(dis.readInt());
      sb.append("Score: ").append(score).append("\n");

      maxCombo = readUnsignedShort(dis);
      sb.append("Max Combo: ").append(maxCombo).append("\n");

      fullCombo = dis.readBoolean();
      sb.append("Perfect Combo: ").append(fullCombo).append("\n");

      mods = Integer.reverseBytes(dis.readInt());
      sb.append("Mods: ").append(mods).append("\n");

      lifeGraph = readOsuString(dis);
      sb.append("Life Graph: ").append(lifeGraph).append("\n");

      timestamp = Long.reverseBytes(dis.readLong());
      sb.append("Timestamp: ").append(timestamp).append("\n");

      replayLength = Integer.reverseBytes(dis.readInt());
      sb.append("Replay Length: ").append(replayLength).append(" bytes\n");

      replayData = new byte[replayLength];
      dis.readFully(replayData);
      sb.append("Replay Data (Compressed) : ").append(Arrays.toString(replayData)).append(" bytes\n");

      long onlineScoreID = Long.reverseBytes(dis.readLong());
      sb.append("Online Score ID: ").append(onlineScoreID).append("\n");

      return sb.toString();
    }
  }

  private static String readOsuString(DataInputStream dis) throws IOException {
    byte stringFlag = dis.readByte();
    if (stringFlag == 0x00) {
      return ""; // Empty string
    } else if (stringFlag == 0x0B) {
      int length = readVarInt(dis);
      byte[] strBytes = new byte[length];
      dis.readFully(strBytes);
      return new String(strBytes, StandardCharsets.UTF_8);
    } else {
      throw new IOException("Unexpected string flag: " + stringFlag);
    }
  }

  //For integers of variable length
  private static int readVarInt(DataInputStream dis) throws IOException {
    int value = 0;
    int shift = 0;
    while (true) {
      byte b = dis.readByte();
      value |= (b & 0x7F) << shift;
      if ((b & 0x80) == 0) break;
      shift += 7;
    }
    return value;
  }

  //For unsigned shorts
  private static int readUnsignedShort(DataInputStream dis) throws IOException {
    return Short.toUnsignedInt(Short.reverseBytes(dis.readShort()));
  }

  public static String decompressToText(byte[] compressedData) throws IOException {
    byte[] decompressedData = decompress(compressedData);
    return new String(decompressedData, StandardCharsets.UTF_8);
  }

  private static byte[] decompress(byte[] compressedData) throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // LZMA decoder
    Decoder decoder = new Decoder();
    byte[] properties = new byte[5];
    inputStream.read(properties);
    decoder.SetDecoderProperties(properties);

    long outSize = 0;
    for (int i = 0; i < 8; i++) {
      int v = inputStream.read();
      outSize |= ((long) v) << (8 * i);
    }

    if (!decoder.Code(inputStream, outputStream, outSize)) {
      throw new IOException("Error in LZMA decompression");
    }

    //return final result
    return outputStream.toByteArray();
  }

  public static int[][] replayDataConversion(String replayData) {

    //split data
    String[] entries = replayData.split(",");
    List<int[]> parsedList = new ArrayList<>();

    for (String entry : entries) {
      //split at every | (sets of 4)
      String[] values = entry.split("\\|");
      int[] parsedEntry = new int[2];
      parsedEntry[0] = Integer.parseInt(values[0].trim());
      parsedEntry[1] = Integer.parseInt(values[1].trim());
      parsedList.add(parsedEntry);
    }

    int[][] parsedArray = new int[parsedList.size()][2];
    parsedList.toArray(parsedArray);
    return parsedArray;
  }

  public ArrayList<IManiaKeyEvent> convertToEvents(int[][] replayData, ArrayList<String> mods) {

    double multi = 1;

    if(mods.contains("halftime")) {
      multi = 1.33333333;
    }

    if (mods.contains("doubletime")) {
      multi = 0.66666666666666666;
    }

    //make auto-detection for offset later
    int currentMS = 0;

    int skip = 0;

    ArrayList<IManiaKeyEvent> events = new ArrayList<>();

    for(int[] entry : replayData) {

      if(skip < 3) {
        currentMS += entry[0];
      }

      if(skip >= 3 && skip != replayData.length - 1) {
       // System.out.println(currentMS);
        int keyData = entry[1];
        String[] keys = {};

        //key1 = 1, key2 = 2, key3 = 4, key4 = 8
        if(keyData == 1) {
          keys = new String[] {"key1"};
        } else if(keyData == 2) {
          keys = new String[] {"key2"};
        } else if(keyData == 4) {
          keys = new String[] {"key3"};
        } else if(keyData == 8) {
          keys = new String[] {"key4"};
        } else if(keyData == 3) {
          keys = new String[] {"key1", "key2"};
        } else if(keyData == 7) {
          keys = new String[] {"key1", "key2", "key3"};
        } else if(keyData == 15) {
          keys = new String[] {"key1", "key2", "key3", "key4"};
        } else if(keyData == 5) {
          keys = new String[] {"key1", "key3"};
        } else if(keyData == 9) {
          keys = new String[] {"key1", "key4"};
        } else if(keyData == 6) {
          keys = new String[] {"key2", "key3"};
        } else if(keyData == 10) {
          keys = new String[] {"key2", "key4"};
        } else if(keyData == 12) {
          keys = new String[] {"key3", "key4"};
        } else if(keyData == 14) {
          keys = new String[] {"key2", "key3", "key4"};
        } else if(keyData == 11) {
          keys = new String[] {"key1", "key2", "key4"};
        } else if(keyData == 13) {
          keys = new String[] {"key1", "key3", "key4"};
        }

        currentMS = entry[0] + currentMS;
        events.add(new maniaKeyEvent((int) (currentMS * multi), keys));
      }
      skip ++;
    }
    return events;
  }
}
