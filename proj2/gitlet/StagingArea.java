package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    static final File AREA_FOLDER = join(".gitlet", "stage");
    static final File FILE_FOLDER = join(AREA_FOLDER, "objects");
    FileList stageFiles;
    FileList removedFiles;

    StagingArea() {
        removedFiles = new FileList();
        stageFiles = new FileList();
    }

    void addFile(File f, Commit currentCommit) {
        byte[] content = readContents(f);
        String hashCode = sha1(content);
        File tempFile = join(FILE_FOLDER, hashCode);

        if (removedFiles.contain(f.getName())) {
            removedFiles.remove(f.getName());
        }

        if (!currentCommit.fileList.changed(f.getName(), hashCode)) {
            tempFile.delete();
            return;
        }

        if (stageFiles.contain(f.getName())) {
            unstage(f.getName());
        }

        stageFiles.addFile(f.getName(), hashCode);
        try {
            tempFile.createNewFile();
        } catch (IOException excp) {
            System.out.println("Failed in StagingArea addFile()");
        }
        writeContents(tempFile, content);
        saveArea();
    }

    private void unstage(String name) {
        File stagedFile = join(FILE_FOLDER, stageFiles.getHashCode(name));
        stagedFile.delete();
        stageFiles.remove(name);
    }

    public void removeFile(String name, Commit currentCommit) {
        boolean changed = false;
        if (stageFiles.contain(name)) {
            unstage(name);
            changed = true;
        }

        if (currentCommit.fileList.contain(name)) {
            removedFiles.addFile(name, null);
            restrictedDelete(name);
            changed = true;
        }

        if (!changed) {
            System.out.println("No reason to remove the file.");
        }
    }

    public boolean isEmpty() {
        return removedFiles.isEmpty() && stageFiles.isEmpty();
    }

    public void clearStagingArea() {
        Set<String> allStagedFiles = new HashSet<>();
        for (String fileName : stageFiles.files.keySet()) {
            allStagedFiles.add(fileName);
        }
        for (String fileName : allStagedFiles) {
            unstage(fileName);
        }
        stageFiles.clear();
        removedFiles.clear();
    }

    public boolean changed() {
        return !stageFiles.isEmpty() || !removedFiles.isEmpty();
    }

    public static StagingArea fromFile() {
        File areaFile = new File(AREA_FOLDER, "stage");
        return readObject(areaFile, StagingArea.class);
    }
    public void saveArea() {
        File outFile = new File(AREA_FOLDER, "stage");
        try {
            outFile.createNewFile();
        } catch (IOException excp) {
            System.out.println("Failed in StagingArea saveArea()");
            outFile = null;
        }
        if (outFile != null) {
            writeObject(outFile, this);
        }
    }
}
