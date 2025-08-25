// Options.java

package cps450;

import java.util.ArrayList;

// class for containing the options set in the command line
public class Options {
    private ArrayList<String> filenames = new ArrayList<>();
    private boolean isDS;
    private boolean isDP;
    private boolean isS;


    public Options(ArrayList<String> filenames, boolean isDS, boolean isDP, boolean isS) {
        this.filenames = filenames;   // file being read
        this.isDS = isDS;           // -ds option
        this.isDP = isDP;           // -dp option
        this.isS = isS;             // -S option
    }

    public String getFilename() {
        return filenames.remove(0);
    }

    // filenames getter/setter
    public ArrayList<String> getFilenames() {
        return filenames;
    }

    public void addFilename(String filename) {
        this.filenames.add(filename);
    }

    // isDS getter/setter
    public boolean isDS() {
        return isDS;
    }

    public void setDS(boolean isDS) {
        this.isDS = isDS;
    }

    // isDP getter/setter
    public boolean isDP() {
        return isDP;
    }

    public void setDP(boolean isDP) {
        this.isDP = isDP;
    }

    // isS getter/setter
    public boolean isS() {
        return isS;
    }

    public void setS(boolean isS) {
        this.isS = isS;
    }
}