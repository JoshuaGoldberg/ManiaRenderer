package replaydata;

import replaydata.notes.Note;
import replaydata.notes.longNote;
import replaydata.notes.riceNote;
import videocreator.ImageGrabber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class osuFileData {

  private final ArrayList<String> mods;

  private final ImageGrabber imageGrabber;

  public osuFileData(ImageGrabber imageGrabber, ArrayList<String> mods) {
    this.imageGrabber = imageGrabber;
    this.mods = mods;
  }


  ArrayList<Note> notes = new ArrayList<>();

  public ArrayList<Note> getNotes() {
    return notes;
  }

  public String extractOD(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("OverallDifficulty")) {
          // Split the line at ':' to get the OD value
          String[] parts = line.split(":");
          if (parts.length > 1) {
            return parts[1].trim();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Return null if OD value is not found
    return null;
  }


  public String extractSongName(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("Title")) {
          // Split the line at ':' to get the OD value
          String[] parts = line.split(":");
          if (parts.length > 1) {
            return parts[1].trim();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Return null if OD value is not found
    return null;
  }

  public String extractDifficulty(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("Version")) {
          // Split the line at ':' to get the OD value
          String[] parts = line.split(":");
          if (parts.length > 1) {
            return parts[1].trim();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Return null if OD value is not found
    return null;
  }

  public void parseHitObjects(File osuFile) throws IOException {

    try (BufferedReader reader = new BufferedReader(new FileReader(osuFile))) {
      String line;
      boolean hitObjectSection = false;

      while ((line = reader.readLine()) != null) {
        // Identify the start of the HitObjects section
        if (line.trim().equals("[HitObjects]")) {
          hitObjectSection = true;
          continue;
        }

        if (hitObjectSection) {

          if (line.trim().isEmpty()) {
            continue; // Skip empty lines
          }

          // Parse the line in the HitObjects section
          String[] parts = line.split(",");
          if (parts.length < 5) {
            continue; // Ensure the line has at least 5 parts
          }

          int x = Integer.parseInt(parts[0].trim());
          int y = Integer.parseInt(parts[1].trim());
          int time = Integer.parseInt(parts[2].trim());
          int type = Integer.parseInt(parts[3].trim());

          // Check the additional data after the type (the 5th part, which might have a colon)
          String[] extraParts = parts[5].split(":");
          int lnData = Integer.parseInt(extraParts[0].trim());

          int tempOD = (int) (Double.parseDouble(this.extractOD(osuFile)) * 10);

          tempOD = tempOD * 3;

          if(tempOD % 10 != 0) {
            tempOD = tempOD + (10 - (tempOD % 10));
          }

          tempOD = tempOD/10;

          if(mods.contains("mirror")) {
            x = 512 - x;
          }

          double multi = 1;

          if (mods.contains("halftime")) {
            multi = 1.33333333333333333;
          }

          if (mods.contains("doubletime")) {
            multi = 0.66666666666666666;
          }

          if (lnData < time) {
            // Create a notetypes.riceNote with the first three numbers

            riceNote note = new riceNote(x, y, (int) ((time) * multi), tempOD, Double.parseDouble(this.extractOD(osuFile)), imageGrabber, mods);
            notes.add(note);
            //System.out.println("Created RiceNote: " + note);
          } else {
            // Create a notetypes.riceNote with the first, second, third, and sixth number
            int addition = Integer.parseInt(parts[5].split(":")[0].trim());
            longNote note = new longNote(x, y, (int) ((time) * multi), (int) ((addition) * multi), tempOD, Double.parseDouble(this.extractOD(osuFile)), imageGrabber, mods);
            notes.add(note);
            //System.out.println("Created RiceNote with addition: " + note);
          }
        }
      }
    }
  }

}
