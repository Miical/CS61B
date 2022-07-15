package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

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
    private String mergedCommit;

    private FileList fileList;

    static final File COMMIT_FOLDER = join(".gitlet", "commits");
    static final File OBJECT_FOLDER = join(".gitlet", "objects");

    Commit(String msg, Date d, String parent) {
        message = msg;
        date = d;
        parentID = parent;
        fileList = new FileList();
        mergedCommit = null;
    }

    Commit(String msg, Date d, String parent, String merge) {
        message = msg;
        date = d;
        parentID = parent;
        fileList = new FileList();
        mergedCommit = merge;
    }

    public FileList getFileList() {
        return fileList;
    }

    public String getMergedCommit() {
        return mergedCommit;
    }

    public void updateFileList(Commit lastCommit, StagingArea stagingArea) {
        for (Map.Entry<String, String> file : stagingArea.stageFiles.getFiles().entrySet()) {
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

        for (Map.Entry<String, String> file : lastCommit.fileList.getFiles().entrySet()) {
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
        if (mergedCommit != null) {
            System.out.println("Merge: " + parentID.substring(0, 7)
                    + " " + mergedCommit.substring(0, 7));
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
        Set<String> vis = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(getHashCode());
        vis.add(getHashCode());
        while (!queue.isEmpty()) {
            Commit c = fromFile(queue.poll());
            ancestors.add(c.getHashCode());
            if (c.getParentID() != null && !vis.contains(c.getParentID())) {
                queue.add(c.getParentID());
                vis.add(c.getParentID());
            }
            if (c.getMergedCommit() != null && !vis.contains(c.getMergedCommit())) {
                queue.add(c.getMergedCommit());
                vis.add(c.getMergedCommit());
            }
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
            return null;
        }

        return fromFile(commitID);
    }

}
