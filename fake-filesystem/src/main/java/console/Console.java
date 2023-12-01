package console;

import main.Main;

import java.util.Scanner;

public class Console {
    private final ProcessManager processManager;

    public Console() {
        processManager = new ProcessManager();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (Main.fileSystem == null)
                System.out.print("\n> ");
            else
                System.out.print("\n" + Main.fileSystem.toString());

            String command = scanner.nextLine();
            if (command.equals("exit")) {
                break;
            }
            processCommand(command);
        }
        scanner.close();
    }

    private void processCommand(String command) {
        String[] tokens = command.split(" ");
        switch (tokens[0]) {
            case "formatar":
                processManager.format(tokens);
                System.out.println(">>> Formatação concluída <<<");
                break;

            case "touch":
                processManager.createFile(tokens);
                break;

            case "write":
                processManager.writeContentInFile(tokens);
                break;

            case "cat":
                processManager.displayFileContent(tokens);
                break;

            case "rm":
                processManager.removeFile(tokens);
                break;

            case "chown":
                processManager.changeOwner(tokens);
                break;

            case "chmod":
                break;

            case "mkdir":
                processManager.createDirectory(tokens[1]);
                break;

            case "rmdir":
                processManager.removeDirectory(tokens);
                break;

            case "cd":
                processManager.changeDirectory(tokens);
                break;

            case "ls":
                processManager.displayDirectoryContent(tokens);
                break;

            case "adduser":
                processManager.addUser(tokens);
                break;

            case "rmuser":
                break;

            case "lsuser":
                processManager.displayUsers();
                break;

            case "sudo":

                break;

            case "su":
                processManager.changeCurrentUser(tokens);
                break;

            default:
                System.out.println("Invalid command.");
        }
    }
}
