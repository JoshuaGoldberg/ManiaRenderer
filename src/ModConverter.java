import java.util.ArrayList;

public class ModConverter {
  
  public ArrayList<String> translate(int mods) {

    ArrayList<String> modList = new ArrayList<>();

    if (mods == 1073741824) {
      modList.add("mirror");
    } else if(mods == 256){
      modList.add("halftime");
    } else if(mods == 64){
      modList.add("doubletime");
    } else if(mods == (256 + 1073741824)){
      modList.add("mirror");
      modList.add("halftime");
    } else if(mods == (64 + 1073741824)){
      modList.add("doubletime");
      modList.add("mirror");
    }

    return modList;

  }

}
