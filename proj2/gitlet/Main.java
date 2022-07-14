package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jason Liu
 */
public class Main {

    public static boolean checkArgsNumber(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }

    public static void checkout(String[] args) {
        if (args.length == 2) {
            Repository.checkoutBranch(args[1]);
        } else if (args.length == 3) {
            if (args[1].equals("--")) {
                Repository.checkout(args[2], null);
            } else {
                System.out.println("Incorrect operands.");
            }
        } else if (args.length == 4) {
            if (args[2].equals("--")) {
                Repository.checkout(args[3], args[1]);
            } else {
                System.out.println("Incorrect operands.");
            }
        }
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (checkArgsNumber(args, 1)) {
                    Repository.init();
                }
                break;
            case "add":
                if (checkArgsNumber(args, 2)) {
                    Repository.add(args[1]);
                }
                break;
            case "commit":
                if (checkArgsNumber(args, 2)) {
                    Repository.commit(args[1]);
                }
                break;
            case "rm":
                if (checkArgsNumber(args, 2)) {
                    Repository.remove(args[1]);
                }
                break;
            case "log":
                if (checkArgsNumber(args, 1)) {
                    Repository.log();
                }
                break;
            case "global-log":
                if (checkArgsNumber(args, 1)) {
                    Repository.globalLog();
                }
                break;
            case "find":
                if (checkArgsNumber(args, 2)) {
                    Repository.find(args[1]);
                }
                break;
            case "status":
                if (checkArgsNumber(args, 1)) {
                    Repository.status();
                }
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                if (checkArgsNumber(args, 2)) {
                    Repository.branch(args[1]);
                }
                break;
            case "rm-branch":
                if (checkArgsNumber(args, 2)) {
                    Repository.rmBranch(args[1]);
                }
                break;
            case "reset":
                if (checkArgsNumber(args, 2)) {
                    Repository.reset(args[1]);
                }
                break;
            case "merge":
                if (checkArgsNumber(args, 2)) {
                    Repository.merge(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
