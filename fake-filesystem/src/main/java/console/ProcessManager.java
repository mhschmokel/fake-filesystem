package console;

import filesystem.FileSystem;
import filesystem.disk.Disk;
import filesystem.inode.Directory;
import filesystem.inode.File;
import main.Main;
import user.User;
import util.ArgumentParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProcessManager {
    public void format(String[] args) {
        Map<String, String> argMap = ArgumentParser.parse(args, 1);
        int numOfBlocks = Integer.parseInt(argMap.get("-b"));

        if(numOfBlocks < 1 || numOfBlocks > 65536) {
            numOfBlocks = 65536;
        }

        Disk disk = new Disk(numOfBlocks);
        Main.fileSystem = new FileSystem(disk);
    }

    public void changeDirectory(String[] args) {
        Directory directory = getDirByPath(args);
        if (directory != null) {
            Main.fileSystem.updateCurrentDirectory(directory);
        }
    }

    public void createDirectory(String path) {
        String[] directories = path.split("/");
        Directory currentDir = Main.fileSystem.getCurrentDirectory();

        for (String dir : directories) {
            if (dir.equals(".")) {
                continue;
            }

            if (dir.equals("..")) {
                if (currentDir.getFatherInode() == null) {
                    currentDir = Main.fileSystem.getRootDirectory();
                } else {
                    currentDir = currentDir.getFatherInode();
                }
                continue;
            }

            if (!dir.isBlank()) {
                if (currentDir.containsSubdirectory(dir)) {
                    currentDir = currentDir.getSubdirectory(dir);
                } else {
                    //Directory newDirectory = Main.fileSystem.createDirectory(dir, currentDir.getFatherInode());
                    Directory newDirectory = Main.fileSystem.createDirectory(dir, currentDir);

                    if (newDirectory != null) {
                        currentDir.addSubdirectory(dir, newDirectory);
                        currentDir = currentDir.getSubdirectory(dir);
                    }
                }
            }
        }
    }

    public void displayDirectoryContent(String[] args) {
        Directory currentDir = getDirByPath(args);

        if (currentDir != null) {
            List<String> folders = currentDir.getSubfolders();
            List<String> files = currentDir.getFiles();

            System.out.println("= SUBFOLDERS =");
            for (String f : folders) {
                System.out.println(f);
            }

            System.out.println("\n= FILES =");
            for (String f : files) {
                System.out.println(f);
            }
        }
    }

    private Directory getDirByPath(String[] args) {
        Directory currentDir = Main.fileSystem.getCurrentDirectory();
        String[] directories;

        if (args.length < 2) {
            return currentDir;
        }

        String path = args[1];
        return getDir(path.split("/"));
    }

    public Directory getDir(String[] directories) {
        Directory currentDir = Main.fileSystem.getCurrentDirectory();

        for (String dir : directories) {
            if (dir.equals(".")) {
                continue;
            }

            if (dir.equals("..")) {
                if (currentDir.getFatherInode() == null) {
                    currentDir = Main.fileSystem.getRootDirectory();
                } else {
                    currentDir = currentDir.getFatherInode();
                }
                continue;
            }

            if (!dir.isBlank()) {
                if (currentDir.containsSubdirectory(dir)) {
                    currentDir = currentDir.getSubdirectory(dir);
                } else {
                    System.out.println("Couldn't find directory " + dir);
                    return null;
                }
            }
        }
        return currentDir;
    }

    public void removeDirectory(String[] args) {
        Directory directory = getDirByPath(args);

        if (directory == null) {
            return;
        }

        if (!directory.isEmpty()) {
            System.out.println("rmdir: failed to remove. Directory not empty");
            return;
        }

//        if (directory.getFatherInode() == null) {
//            System.out.println("Can't remove root folder");
//            return;
//        }

        Main.fileSystem.removeDirectory(directory);
    }

    public void addUser(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing username");
            return;
        }

        String name = args[1];
        if(Main.fileSystem.isCurrentUserAdmin()) {
            Main.fileSystem.addUser(name);
            return;
        }

        System.out.println("Current user has no admin privileges");
    }

    public void displayUsers() {
        List<User> users = Main.fileSystem.getUsers();

        for (User user : users) {
            String isAdminText = user.isRoot() || user.isHasAdminPrivileges() ? "admin" : "user";
            System.out.println(user.getName()+";" + user.getUuid() + ";" + isAdminText);
        }
    }

    public void changeCurrentUser(String[] args) {
        if (args.length < 2) {
            Main.fileSystem.changeCurrentUser("root");
            return;
        }

        Main.fileSystem.changeCurrentUser(args[1]);
    }

    private Directory getFileDirectory(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing filename");
            return null;
        }

        String fullPath = args[1];
        String[] path = fullPath.split("/");
        String filename = path[path.length-1];

        if (path.length == 1) {
            return Main.fileSystem.getCurrentDirectory();
//            if (Main.fileSystem.getCurrentDirectory().containsFile(path[0])) {
//                Main.fileSystem.updateFileAccess(filename, Main.fileSystem.getCurrentDirectory());
//            } else {
//                Main.fileSystem.createFile(filename, Main.fileSystem.getCurrentDirectory());
//            }
//            return;
        }

        List<String> pathList = new ArrayList<String>(List.of(path));
        pathList.remove(path.length - 1);

        String[] pathListConcatenated = pathList.toArray(new String[0]);
        return getDir(pathListConcatenated);

//        if (directory.containsFile(filename)) {
//            Main.fileSystem.updateFileAccess(filename, directory);
//        } else {
//            Main.fileSystem.createFile(filename, directory);
//        }
    }

    public void createFile(String[] args) {
        Directory fileDirectory = getFileDirectory(args);

        if (fileDirectory != null) {
            String fullPath = args[1];
            String[] path = fullPath.split("/");
            String filename = path[path.length-1];

            if (fileDirectory.containsFile(filename)) {
                Main.fileSystem.updateFileAccess(filename, fileDirectory);
            } else {
                Main.fileSystem.createFile(filename, fileDirectory);
            }
        }
    }

    public void displayFileContent(String[] args) {
        Directory fileDirectory = getFileDirectory(args);

        if (fileDirectory != null) {
            String fullPath = args[1];
            String[] path = fullPath.split("/");
            String filename = path[path.length-1];

            if (fileDirectory.containsFile(filename)) {
                File file = fileDirectory.getFile(filename);
                System.out.println(file.toString());
            } else {
                System.out.println("File not found");
            }
        }
    }

    public void removeFile(String[] args) {
        Directory fileDirectory = getFileDirectory(args);

        if (fileDirectory != null) {
            String fullPath = args[1];
            String[] path = fullPath.split("/");
            String filename = path[path.length-1];

            if (fileDirectory.containsFile(filename)) {
                Main.fileSystem.removeFile(filename, fileDirectory);
            } else {
                System.out.println("File not found");
            }
        }
    }

    public void writeContentInFile(String[] args) {
        if (args.length < 3) {
            System.out.println("Missing args");
            return;
        }

        Directory fileDirectory = getFileDirectory(args);

        if (fileDirectory != null) {
            String fullPath = args[1];
            String[] path = fullPath.split("/");
            String filename = path[path.length-1];
            String content = args[2].trim();

            Main.fileSystem.writeContentInFile(filename, fileDirectory, content);
        }
    }

    public void changeOwner(String[] args) {
        if (args.length < 3) {
            System.out.println("Missing args");
            return;
        }

        String newUser = args[1];
        String fullPath = args[2];
        String[] path = fullPath.split("/");
        String filename = path[path.length-1];

        String[] pathToGetInode = {"", fullPath};

        Directory fileDir = getFileDirectory(pathToGetInode);

        if (fileDir != null) {
            File file = fileDir.getFile(filename);

            if (file != null) {
                Main.fileSystem.updateInodeOwner(file, newUser);
            } else {
                System.out.println("File not found");
            }
            return;
        }

        Directory directory = getDirByPath(pathToGetInode);
        if (directory != null) {
            Main.fileSystem.updateInodeOwner(directory, newUser);
        }
    }

    public void changeFilePermissions(String[] args) {
        if (args.length < 3) {
            System.out.println("Missing args");
            return;
        }

        String code = args[1];
        String fullPath = args[2];
        String[] path = fullPath.split("/");
        String filename = path[path.length-1];

        String[] pathToGetInode = {"", fullPath};

        Directory fileDir = getFileDirectory(pathToGetInode);

        if (fileDir != null) {
            File file = fileDir.getFile(filename);

            if (file != null) {
                String[] permissions = getPermissionsByCode(Integer.parseInt(code.substring(0,1)));
                file.changeOwnerPermission(permissions[0], permissions[1], permissions[2]);

                permissions = getPermissionsByCode(Integer.parseInt(code.substring(1,2)));
                file.changeGroupPermission(permissions[0], permissions[1], permissions[2]);

                permissions = getPermissionsByCode(Integer.parseInt(code.substring(2,3)));
                file.changeOtherPermission(permissions[0], permissions[1], permissions[2]);
            } else {
                System.out.println("File not found");
            }
            return;
        }

        Directory directory = getDirByPath(pathToGetInode);
        if (directory != null) {
            String[] permissions = getPermissionsByCode(Integer.parseInt(code.substring(0,1)));
            directory.changeOwnerPermission(permissions[0], permissions[1], permissions[2]);

            permissions = getPermissionsByCode(Integer.parseInt(code.substring(1,2)));
            directory.changeGroupPermission(permissions[0], permissions[1], permissions[2]);

            permissions = getPermissionsByCode(Integer.parseInt(code.substring(2,3)));
            directory.changeOtherPermission(permissions[0], permissions[1], permissions[2]);
        }

    }

    private String[] getPermissionsByCode(int code) {
        switch (code) {
            case 0:
                return new String[]{"-", "-", "-"};
            case 1:
                return new String[]{"-", "-", "x"};
            case 2:
                return new String[]{"-", "w", "-"};
            case 4:
                return new String[]{"r", "-", "-"};
            case 5:
                return new String[]{"r", "-", "x"};
            case 6:
                return new String[]{"r", "w", "-"};
            case 7:
                return new String[]{"r", "w", "x"};
            default:
                return new String[]{"r", "w", "x"};
        }
    }

    public void removeUser(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing args");
            return;
        }

        String username = args[1];
    }

    public void grantCurrentUserAdminPrivileges() {
        Main.fileSystem.getCurrentUser().grantAdminPrivileges();
    }

    public void removeCurrentUserAdminPrivileges() {
        Main.fileSystem.getCurrentUser().removeAdminPrivileges();
    }
}
