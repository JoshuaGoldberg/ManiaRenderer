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

      // Parse game mode (1 byte)
      gameMode = dis.readUnsignedByte();
      sb.append("Game Mode: ").append(gameMode == 3 ? "osu!mania" : "Other").append("\n");

      // Parse version (4 bytes, int)
      version = Integer.reverseBytes(dis.readInt());
      sb.append("Version: ").append(version).append("\n");

      // Parse beatmap MD5 hash (String with length prefix)
      beatmapMD5 = readOsuString(dis);
      sb.append("Beatmap MD5 Hash: ").append(beatmapMD5).append("\n");

      // Parse player name (String with length prefix)
      playerName = readOsuString(dis);
      sb.append("Player Name: ").append(playerName).append("\n");

      // Parse replay hash (String with length prefix)
      replayHash = readOsuString(dis);
      sb.append("Replay Hash: ").append(replayHash).append("\n");

      // Parse number of 300s (2 bytes, short)
      num300 = readUnsignedShort(dis);
      sb.append("Number of 300s: ").append(num300).append("\n");

      // Parse number of 100s (2 bytes, short)
      num100 = readUnsignedShort(dis);
      sb.append("Number of 100s: ").append(num100).append("\n");

      // Parse number of 50s (2 bytes, short)
      num50 = readUnsignedShort(dis);
      sb.append("Number of 50s: ").append(num50).append("\n");

      // Parse number of MAX 300s (2 bytes, short)
      numMax300 = readUnsignedShort(dis);
      sb.append("Number of MAX 300s: ").append(numMax300).append("\n");

      // Parse number of 200s (2 bytes, short)
      num200 = readUnsignedShort(dis);
      sb.append("Number of 200s: ").append(num200).append("\n");

      // Parse number of misses (2 bytes, short)
      numMisses = readUnsignedShort(dis);
      sb.append("Number of Misses: ").append(numMisses).append("\n");

      // Parse score (4 bytes, int)
      score = Integer.reverseBytes(dis.readInt());
      sb.append("Score: ").append(score).append("\n");

      // Parse max combo (2 bytes, short)
      maxCombo = readUnsignedShort(dis);
      sb.append("Max Combo: ").append(maxCombo).append("\n");

      // Parse perfect combo (1 byte, boolean)
      fullCombo = dis.readBoolean();
      sb.append("Perfect Combo: ").append(fullCombo).append("\n");

      // Parse mods (4 bytes, int)
      mods = Integer.reverseBytes(dis.readInt());
      sb.append("Mods: ").append(mods).append("\n");

      // Parse life bar graph (String with length prefix)
      lifeGraph = readOsuString(dis);
      sb.append("Life Graph: ").append(lifeGraph).append("\n");

      // Parse timestamp of the replay (8 bytes, long)
      timestamp = Long.reverseBytes(dis.readLong());
      sb.append("Timestamp: ").append(timestamp).append("\n");

      // Parse replay length (4 bytes, int)
      replayLength = Integer.reverseBytes(dis.readInt());
      sb.append("Replay Length: ").append(replayLength).append(" bytes\n");

      // Parse the replay data itself (binary blob)
      replayData = new byte[replayLength];
      dis.readFully(replayData);
      sb.append("Replay Data (Compressed) : ").append(Arrays.toString(replayData)).append(" bytes\n");

      // Parse online score ID (8 bytes, long)
      long onlineScoreID = Long.reverseBytes(dis.readLong());
      sb.append("Online Score ID: ").append(onlineScoreID).append("\n");

      return sb.toString();
    }
  }

  // Helper method to read an osu! string (with a length prefix)
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

  // Helper method to read a variable-length integer (osu! specific)
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

  // Helper method to read an unsigned short correctly
  private static int readUnsignedShort(DataInputStream dis) throws IOException {
    return Short.toUnsignedInt(Short.reverseBytes(dis.readShort()));
  }

  public static String decompressToText(byte[] compressedData) throws IOException {
    // Step 1: Decompress the LZMA byte array
    byte[] decompressedData = decompress(compressedData);

    // Step 2: Convert the decompressed byte array into a string (text)
    return new String(decompressedData, StandardCharsets.UTF_8);
  }

  private static byte[] decompress(byte[] compressedData) throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // Create a new LZMA decoder
    Decoder decoder = new Decoder();

    // Read the properties for the LZMA decoder (first 5 bytes)
    byte[] properties = new byte[5];
    inputStream.read(properties);
    decoder.SetDecoderProperties(properties);

    // Read the decompressed size (next 8 bytes, little endian)
    long outSize = 0;
    for (int i = 0; i < 8; i++) {
      int v = inputStream.read();
      outSize |= ((long) v) << (8 * i);
    }

    // Now, decompress the input stream into the output stream
    if (!decoder.Code(inputStream, outputStream, outSize)) {
      throw new IOException("Error in LZMA decompression");
    }

    return outputStream.toByteArray();
  }

  public static int[][] replayDataConversion(String replayData) {
    //System.out.println(replayData);
    // Split the string by commas to get each X|X|X|X entry
    String[] entries = replayData.split(",");

    // Use an ArrayList to store the arrays before converting to an int[][]
    List<int[]> parsedList = new ArrayList<>();

    for (String entry : entries) {
      // Split each entry by the pipe symbol to get the values
      String[] values = entry.split("\\|");

      // Parse the first two values as integers and store them in an array
      int[] parsedEntry = new int[2];
      parsedEntry[0] = Integer.parseInt(values[0].trim());
      parsedEntry[1] = Integer.parseInt(values[1].trim());

      // Add the array to the list
      parsedList.add(parsedEntry);
    }

    // Convert the ArrayList to a 2D array
    int[][] parsedArray = new int[parsedList.size()][2];
    parsedList.toArray(parsedArray);

    return parsedArray;
  }

  public ArrayList<IManiaKeyEvent> convertToEvents(int[][] replayData, ArrayList<String> mods) {

    double multi = 1;

    if(mods.contains("halftime")) {
      multi = 1.33333333;
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
