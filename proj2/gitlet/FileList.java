package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileList implements Serializable {
    private Map<String, String> files;

    FileList() {
        files = new HashMap<>();
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public void addFile(String name, String hash) {
        files.put(name, hash);
    }

    public boolean contain(String name) {
        return files.containsKey(name);
    }

    public boolean changed(String name, String hashCode) {
        if (files.get(name) == null) {
            return true;
        }
        return !files.get(name).equals(hashCode);
    }

    public void remove(String name) {
        files.remove(name);
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public void clear() {
        files.clear();
    }

    public String getHashCode(String name) {
        return files.get(name);
    }
}
