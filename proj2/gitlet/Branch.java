package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Branch implements Serializable {
    static final File BRANCH_FOLDER = join(".gitlet", "branches");
    String name;
    String commit;

    public Branch(String n, String c) {
       name = n;
       commit = c;
    }

    public static Branch fromFile(String branchName) {
        File branchFile = new File(BRANCH_FOLDER, branchName);
        if (!branchFile.exists()) {
            return null;
        }
        return readObject(branchFile, Branch.class);
    }

    public void saveBranch() {
        File outFile = new File(BRANCH_FOLDER, name);
        try {
            outFile.createNewFile();
        } catch(IOException excp) {
            System.out.println("Failed in Branch saveBranch()");
            outFile = null;
        }
        if (outFile != null) {
            writeObject(outFile, this);
        }
    }
}
