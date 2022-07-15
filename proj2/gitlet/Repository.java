package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jason Liu
 */
public class Repository implements Serializable {
    /**
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
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
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
        loadRepo();
        File f = join(CWD, fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
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
        loadRepo();
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit.fromFile(fileName).print();
        }
    }

    public static void find(String msg) {
        loadRepo();
        boolean found = false;
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit c = Commit.fromFile(fileName);
            if (c.containMessage(msg)) {
                found = true;
                System.out.println(fileName);
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
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
        for (String fileName : stagingArea.stageFiles.getFiles().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : stagingArea.removedFiles.getFiles().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (Map.Entry<String, String> file : stagingArea.stageFiles.getFiles().entrySet()) {
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
        for (Map.Entry<String, String> file
                : getCurrentCommit().getFileList().getFiles().entrySet()) {
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
            if (!stagingArea.stageFiles.contain(fileName)
                    && !getCurrentCommit().getFileList().contain(fileName)) {
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
        if (!commit.getFileList().contain(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        File oldFile = join(CWD, fileName);
        File exceptedFile = join(Commit.OBJECT_FOLDER,
                commit.getFileList().getHashCode(fileName));
        try {
            oldFile.createNewFile();
        } catch (IOException excp) {
            oldFile = null;
        }
        writeContents(oldFile, readContents(exceptedFile));
        saveRepo();
    }

    private static void changeFilesToCommit(Commit c) {
        for (String fileName : plainFilenamesIn(CWD)) {
            if (!stagingArea.stageFiles.contain(fileName)
                    && !getCurrentCommit().getFileList().contain(fileName)) {
                if (c.getFileList().contain(fileName)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    return;
                }
            }
        }

        for (String fileName : getCurrentCommit().getFileList().getFiles().keySet()) {
            File f = join(CWD, fileName);
            f.delete();
        }

        for (Map.Entry<String, String> file : c.getFileList().getFiles().entrySet()) {
            File cwdFile = join(CWD, file.getKey());
            File exceptedFile = join(Commit.OBJECT_FOLDER, file.getValue());
            try {
                cwdFile.createNewFile();
            } catch (IOException excp) {
                cwdFile = null;
            }
            writeContents(cwdFile, readContents(exceptedFile));
        }

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

        changeFilesToCommit(Commit.fromFile(b.commit));
        head = b;
        stagingArea.clearStagingArea();
        saveRepo();
    }

    public static void branch(String branchName) {
        loadRepo();
        if (Branch.fromFile(branchName) != null) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch newBranch = new Branch(branchName, head.commit);
        newBranch.saveBranch();
        saveRepo();
    }

    public static void rmBranch(String branchName) {
        loadRepo();
        if (Branch.fromFile(branchName) == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (head.name.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        Branch.removeBranch(branchName);
        saveRepo();
    }

    public static void reset(String commitID) {
        loadRepo();
        Commit c = Commit.fromFileWithPrefix(commitID);
        if (c == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        changeFilesToCommit(c);
        head.commit = c.getHashCode();
        stagingArea.clearStagingArea();
        saveRepo();
    }

    private static boolean modified(String fileName, Commit oldCommit, Commit newCommit) {
        if (oldCommit.getFileList().contain(fileName)
                && newCommit.getFileList().contain(fileName)) {
            return !oldCommit.getFileList().getHashCode(fileName).equals(
                    newCommit.getFileList().getHashCode(fileName));
        } else if (!oldCommit.getFileList().contain(fileName)
                && !newCommit.getFileList().contain(fileName)) {
            return false;
        }
        return true;
    }
    private static boolean mergeCheck(String branchName) {
        if (stagingArea.changed()) {
            System.out.println("You have uncommitted changes.");
            return false;
        }
        Branch b = Branch.fromFile(branchName);
        if (b == null) {
            System.out.println("A branch with that name does not exist.");
            return false;
        }
        if (b.name.equals(head.name)) {
            System.out.println("Cannot merge a branch with itself.");
            return false;
        }
        Commit c = Commit.fromFile(b.commit), current = getCurrentCommit();
        Set<String> ancestorCommits = Commit.fromFile(head.commit).ancestorCommits();
        Commit split = c;
        while (!ancestorCommits.contains(split.getHashCode())) {
            split = Commit.fromFile(split.getParentID());
        }
        if (split.getHashCode().equals(c.getHashCode())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return false;
        }
        if (split.getHashCode().equals(current.getHashCode())) {
            reset(c.getHashCode());
            System.out.println("Current branch fast-forwarded.");
            return false;
        }
        for (String fileName : plainFilenamesIn(CWD)) {
            if (!split.getFileList().contain(fileName) && c.getFileList().contain(fileName)
                    && !current.getFileList().contain(fileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return false;
            }
        }
        return true;
    }

    public static String mergeConflictFile(File fileA, File fileB) {
        String fileAContent = "", fileBContent = "";
        if (fileA != null) {
            fileAContent = readContentsAsString(fileA);
        }
        if (fileB != null) {
            fileBContent = readContentsAsString(fileB);
        }
        String newContent = "<<<<<<< HEAD\n" + fileAContent
                + "=======\n" + fileBContent + ">>>>>>>\n";
        byte[] contentByte = newContent.getBytes();
        String newFileHash = sha1(contentByte);
        File newFile = join(Commit.OBJECT_FOLDER, newFileHash);
        try {
            newFile.createNewFile();
        } catch (IOException excp) {
            newFile = null;
        }
        writeContents(newFile, contentByte);
        return newFileHash;
    }

    public static void merge(String branchName) {
        loadRepo();
        if (!mergeCheck(branchName)) {
            return;
        }

        Branch b = Branch.fromFile(branchName);
        Commit c = Commit.fromFile(b.commit), current = getCurrentCommit();
        Set<String> ancestorCommits = Commit.fromFile(head.commit).ancestorCommits();
        Commit split = c;
        while (!ancestorCommits.contains(split.getHashCode())) {
            split = Commit.fromFile(split.getParentID());
        }

        boolean hasConflict = false;
        Commit newCommit = new Commit("Merged " + branchName + " into " + head.name + ".",
                new Date(), head.commit, b.commit);
        for (String fileName : current.getFileList().getFiles().keySet()) {
            if (!modified(fileName, split, current)) {
                if (c.getFileList().contain(fileName)) {
                    newCommit.getFileList().addFile(fileName,
                            c.getFileList().getHashCode(fileName));
                }
            } else {
                if (!modified(fileName, split, c)) {
                    newCommit.getFileList().addFile(fileName,
                            current.getFileList().getHashCode(fileName));
                } else {
                    hasConflict = true;
                    File currentFile = join(Commit.OBJECT_FOLDER,
                            current.getFileList().getHashCode(fileName));
                    File givenFile = null;
                    if (c.getFileList().contain(fileName)) {
                        givenFile = join(Commit.OBJECT_FOLDER,
                                c.getFileList().getHashCode(fileName));
                    }
                    String newFileHash = mergeConflictFile(currentFile, givenFile);
                    newCommit.getFileList().addFile(fileName, newFileHash);
                }
            }
        }
        for (String fileName : c.getFileList().getFiles().keySet()) {
            if (!current.getFileList().contain(fileName)) {
                if (!split.getFileList().contain(fileName)) {
                    newCommit.getFileList().addFile(fileName,
                            c.getFileList().getHashCode(fileName));
                } else {
                    if (modified(fileName, split, c)) {
                        hasConflict = true;
                        String newFileHash = mergeConflictFile(null,
                            join(Commit.OBJECT_FOLDER, c.getFileList().getHashCode(fileName)));
                        newCommit.getFileList().addFile(fileName, newFileHash);
                    }
                }
            }
        }
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
        newCommit.saveCommit();
        reset(newCommit.getHashCode());
    }

    private static void loadRepo() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
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
