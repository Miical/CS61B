package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileList implements Serializable {
    private static class FileItem implements Serializable {
        public String name;
        public String hash;
        FileItem(String n, String h) {
            name = n;
            hash = h;
        }
    }
    Map<String, String> files;


    FileList() {
        files = new HashMap<>();
    }

    public void addFile(String name, String hash) {
        files.put(name, hash);
    }

    public boolean contain(String name) {
        return files.containsKey(name);
    }

    public boolean changed(String name, String hashCode) {
        return files.get(name) != hashCode;
    }

    public void remove(String name) {
        files.remove(name);
    }

    public String getHashCode(String name) {
        return files.get(name);
    }

}
