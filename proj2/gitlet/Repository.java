package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        join(GITLET_DIR, "objects").mkdir();
    }
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            return;
        }
        creatWorkFolder();
        Commit firstCommit = new Commit("initial commit", new Date(0), null);
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
        loadRepo();
        if (stagingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Commit newCommit = new Commit(message, new Date(), head.commit);
        newCommit.updateFileList(getCurrentCommit(), stagingArea);
        newCommit.saveCommit();
        head.commit = newCommit.getHashCode();
        stagingArea.clearStagingArea();
        saveRepo();
    }

    public static void remove(String name) {
        loadRepo();
        stagingArea.removeFile(name, getCurrentCommit());
        saveRepo();
    }

    public static void log() {
        loadRepo();
        getCurrentCommit().printHistory();
    }

    public static void globalLog() {
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit.fromFile(fileName).print();
        }
    }

    public static void find(String msg) {
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit c = Commit.fromFile(fileName);
            if (c.containMessage(msg)) {
                c.print();
            }
        }
    }

    public static void status() {
        loadRepo();
        System.out.println("=== Branches ===");
        for (String fileName : plainFilenamesIn(Branch.BRANCH_FOLDER)) {
            if (fileName.equals(head.name)) {
                System.out.print("*");
            }
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String fileName : stagingArea.stageFiles.files.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : stagingArea.removedFiles.files.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (Map.Entry<String, String> file : stagingArea.stageFiles.files.entrySet()) {
            File f = join(CWD, file.getKey());
            if (!f.exists()) {
                System.out.println(file.getKey() + " (deleted)");
            } else {
               byte[] content = readContents(f);
               String hashCode = sha1(content);
               if (!hashCode.equals(file.getValue())) {
                   System.out.println(file.getKey() + " (modified)");
               }
            }
        }
        for (Map.Entry<String, String> file : getCurrentCommit().fileList.files.entrySet()) {
            if (stagingArea.stageFiles.contain(file.getKey())) {
                continue;
            }
            File f = join(CWD, file.getKey());
            if (!f.exists()) {
                if (!stagingArea.removedFiles.contain(file.getKey())) {
                    System.out.println(file.getKey() + " (deleted)");
                }
            } else {
                byte[] content = readContents(f);
                String hashCode = sha1(content);
                if (!hashCode.equals(file.getValue())) {
                    System.out.println(file.getKey() + " (modified)");
                }
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String fileName : plainFilenamesIn(CWD)) {
            if (!stagingArea.stageFiles.contain(fileName) &&
                    !getCurrentCommit().fileList.contain(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    public static void checkout(String fileName, String commitID) {
        loadRepo();
        if (commitID == null) {
            commitID = head.commit;
        }

        Commit commit = Commit.fromFileWithPrefix(commitID);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (!commit.fileList.contain(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        File oldFile = join(CWD, fileName);
        File exceptedFile = join(Commit.OBJECT_FOLDER,
                commit.fileList.getHashCode(fileName));
        try {
            oldFile.createNewFile();
        } catch (IOException excp) {}
        writeContents(oldFile, readContents(exceptedFile));
        saveRepo();
    }

    public static void checkoutBranch(String branch) {
        loadRepo();
        Branch b = Branch.fromFile(branch);
        if (b == null) {
            System.out.println("No such branch exists.");
            return;
        }
        if (head.name.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Commit c = Commit.fromFile(b.commit);
        for (String fileName : plainFilenamesIn(CWD)) {
            if (!stagingArea.stageFiles.contain(fileName) &&
                    !getCurrentCommit().fileList.contain(fileName)) {
                if (c.fileList.contain(fileName)) {
                    System.out.println("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    return;
                }
            }
        }

        for (String fileName : getCurrentCommit().fileList.files.keySet()) {
            File f = join(CWD, fileName);
            f.delete();
        }

        for (Map.Entry<String, String> file : c.fileList.files.entrySet()) {
            File cwdFile = join(CWD, file.getKey());
            File exceptedFile = join(Commit.OBJECT_FOLDER, file.getValue());
            try {
                cwdFile.createNewFile();
            } catch (IOException excp) {}
            writeContents(cwdFile, readContents(exceptedFile));
        }
        stagingArea.clearStagingArea();
        saveRepo();
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
