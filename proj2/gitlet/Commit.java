package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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

    public FileList fileList;

    static final File COMMIT_FOLDER = join(".gitlet", "commits");

    Commit(String msg, Date d, String parent) {
       message = msg;
       date = d;
       parentID = parent;
       fileList = new FileList();
    }
    public String getHashCode() {
        return sha1(serialize(this));
    }

    public void saveCommit() {
        String hashCode = getHashCode();
        File outFile = new File(COMMIT_FOLDER, hashCode);
        try {
            outFile.createNewFile();
        } catch(IOException excp) {
            System.out.println("Failed in Commit saveCommit()");
            outFile = null;
        }
        if (outFile != null) {
            writeObject(outFile, this);
        }
    }

    public static Commit fromFile(String commitHash) {
        File CommitFile = new File(COMMIT_FOLDER, commitHash);
        return readObject(CommitFile, Commit.class);
    }

}
