package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *  @author Jason Liu
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String parentID;
    private Date date;
    private String mergedBranch;

    public FileList fileList;

    static final File COMMIT_FOLDER = join(".gitlet", "commits");
    static final File OBJECT_FOLDER = join(".gitlet", "objects");

    Commit(String msg, Date d, String parent) {
        message = msg;
        date = d;
        parentID = parent;
        fileList = new FileList();
        mergedBranch = null;
    }

    Commit(String msg, Date d, String parent, String merge) {
        message = msg;
        date = d;
        parentID = parent;
        fileList = new FileList();
        mergedBranch = merge;
    }

    public void updateFileList(Commit lastCommit, StagingArea stagingArea) {
        for (Map.Entry<String, String> file : stagingArea.stageFiles.files.entrySet()) {
            File stagedFile = join(StagingArea.FILE_FOLDER, file.getValue());
            byte[] content = readContents(stagedFile);
            File savedFile = join(OBJECT_FOLDER, file.getValue());
            try {
                savedFile.createNewFile();
            } catch (IOException excp) {
                System.out.println("Failed in Commit updateFileLast()");
                return;
            }
            writeContents(savedFile, content);
            fileList.addFile(file.getKey(), file.getValue());
        }

        for (Map.Entry<String, String> file : lastCommit.fileList.files.entrySet()) {
            if (!fileList.contain(file.getKey())) {
                if (!stagingArea.removedFiles.contain(file.getKey())) {
                    fileList.addFile(file.getKey(), file.getValue());
                }
            }
        }
    }

    public String getHashCode() {
        return sha1(serialize(this));
    }

    public void print() {
        System.out.println("===");
        System.out.println("commit " + getHashCode());
        if (mergedBranch != null) {
            System.out.println("Merge: " + parentID.substring(0, 7)
                    + " " + mergedBranch.substring(0, 7));
        }
        SimpleDateFormat format = new
                SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        System.out.println("Date: " + format.format(date));
        System.out.println(message);
        System.out.println();
    }

    public void printHistory() {
        print();
        if (parentID != null) {
            fromFile(parentID).printHistory();
        }
    }

    public boolean containMessage(String msg) {
        return message.contains(msg);
    }

    public void saveCommit() {
        String hashCode = getHashCode();
        File outFile = new File(COMMIT_FOLDER, hashCode);
        try {
            outFile.createNewFile();
        } catch (IOException excp) {
            System.out.println("Failed in Commit saveCommit()");
            outFile = null;
        }
        if (outFile != null) {
            writeObject(outFile, this);
        }
    }

    public Set<String> ancestorCommits() {
        Set<String> ancestors = new HashSet<>();
        Commit now = this;
        ancestors.add(getHashCode());
        while (now.parentID != null) {
            ancestors.add(now.parentID);
            now = fromFile(now.parentID);
        }
        return ancestors;
    }

    public String getParentID() {
        return parentID;
    }

    public static Commit fromFile(String commitHash) {
        File commitFile = new File(COMMIT_FOLDER, commitHash);
        if (!commitFile.exists()) {
            return null;
        }
        return readObject(commitFile, Commit.class);
    }
    public static Commit fromFileWithPrefix(String prefixHashCode) {
        if (prefixHashCode.length() > UID_LENGTH) {
            return null;
        }
        String commitID = null;
        int num = 0;
        for (String fileName : plainFilenamesIn(COMMIT_FOLDER)) {
            boolean correct = true;
            for (int i = 0; i < prefixHashCode.length(); i++) {
                if (fileName.charAt(i) != prefixHashCode.charAt(i)) {
                    correct = false;
                }
            }
            if (correct) {
                commitID = fileName;
                num++;
            }
        }

        if (num != 1) {
            System.out.println(prefixHashCode);
            System.out.println("not found");
            return null;
        }

        return fromFile(commitID);
    }

}
