package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

public class StagingArea implements Serializable {
    static final File AREA_FOLDER = join(".gitlet", "stage");
    static final File FILE_FOLDER = join(AREA_FOLDER, "objects");
    FileList stageFiles;
    List<String> removedFiles;

    StagingArea() {
        removedFiles = new ArrayList<>();
        stageFiles = new FileList();
    }

    void addFile(File f, Commit currentCommit) {
        byte[] content = readContents(f);
        String hashCode = sha1(content);
        File tempFile = join(FILE_FOLDER, hashCode);

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
        File lastFile = join(FILE_FOLDER, stageFiles.getHashCode(name);
        lastFile.delete();
        stageFiles.remove(name);
    }

    void removeFile(String name, Commit currentCommit) {
        if (stageFiles.contain(name)) {
            unstage(name);
        }

        if (currentCommit.fileList.contain(name)) {
            removedFiles.add(name);
        }





    }

    public static StagingArea fromFile() {
        File areaFile = new File(AREA_FOLDER, "stage");
        return readObject(areaFile, StagingArea.class);
    }
    public void saveArea() {
        File outFile = new File(AREA_FOLDER, "stage");
        try {
            outFile.createNewFile();
        } catch(IOException excp) {
            System.out.println("Failed in StagingArea saveArea()");
            outFile = null;
        }
        if (outFile != null) {
            writeObject(outFile, this);
        }
    }
}
