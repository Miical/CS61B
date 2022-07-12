package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jason Liu
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    private static Branch head;
    private static StagingArea stagingArea;

    private static Commit getCurrentCommit() {
        return Commit.fromFile(head.commit);
    }

    private static void creatWorkFolder() {
        GITLET_DIR.mkdir();
        join(GITLET_DIR, "branches").mkdir();
        join(GITLET_DIR, "stage").mkdir();
        join(GITLET_DIR, "stage", "objects").mkdir();
        join(GITLET_DIR, "commits").mkdir();
    }
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            return;
        }
        creatWorkFolder();
        Commit firstCommit = new Commit("initial commit", new Date(), null);
        firstCommit.saveCommit();
        head = new Branch("master", firstCommit.getHashCode());
        stagingArea = new StagingArea();
        saveRepo();
    }

    public static void add(String fileName) {
        File f = join(CWD, fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        loadRepo();
        stagingArea.addFile(f, getCurrentCommit());
        saveRepo();
    }

    public static void commit(String message) {

    }

    private static void loadRepo() {
        File headFile = new File(GITLET_DIR, "HEAD");
        String headName = readObject(headFile, String.class);
        head = Branch.fromFile(headName);
        stagingArea = StagingArea.fromFile();
    }
    private static void saveRepo() {
        File headFile = new File(GITLET_DIR, "HEAD");
        String headName = head.name;
        writeObject(headFile, headName);

        head.saveBranch();
        stagingArea.saveArea();
    }

}
