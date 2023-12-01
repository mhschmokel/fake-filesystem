package filesystem;

import filesystem.disk.Block;
import filesystem.disk.Disk;
import filesystem.inode.Directory;
import filesystem.inode.File;
import filesystem.inode.Inode;
import filesystem.inode.InodeHeader;
import lombok.Getter;
import user.Root;
import user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class FileSystem {
    private final Directory rootDirectory;
    private Directory currentDirectory;
    private final Disk disk;
    private final List<User> users = new ArrayList<>();
    private User currentUser;
    private String currentPath = "/";

    public FileSystem(Disk disk) {
        Root rootUser = new Root();
        this.disk = disk;
        this.currentUser = rootUser;
        this.users.add(currentUser);
        this.rootDirectory = disk.createDirectory(this.currentUser, null);
        this.currentDirectory = this.rootDirectory;
    }

    public Directory createDirectory(String name, Directory fatherDir) {
        return disk.createDirectory(this.currentUser, fatherDir);
    }

    public void updateCurrentDirectory(Directory newDirectory) {
        this.currentDirectory = newDirectory;
        updateCurrentPath();
    }

    public void updateCurrentPath() {
        List<UUID> inodesTrace = new ArrayList<>();
        StringBuilder path = new StringBuilder("/");

        Directory directoryTrace = this.currentDirectory;
        while (directoryTrace != null) {
            inodesTrace.add(directoryTrace.getUuid());
            directoryTrace = directoryTrace.getFatherInode();
        }
        Collections.reverse(inodesTrace);

        Directory dir = this.getRootDirectory();
        for (UUID uuid : inodesTrace) {
            Map<String, Directory> allDirs = new HashMap<>(dir.getDirectSubdirectories());
            allDirs.putAll(dir.getIndirectSubdirectories());
            for (Map.Entry<String, Directory> entry : allDirs.entrySet()) {
                if (entry.getValue().getUuid().equals(uuid)) {
                    path.append(entry.getKey())
                            .append("/");
                    dir = entry.getValue();
                }
            }
        }

        this.currentPath = path.toString();
    }

    public void removeDirectory(Directory directory) {
        disk.removeBlock(directory.getAssociatedBlock());

        Directory fatherDir;
        if (directory.getFatherInode() == null) {
            fatherDir = this.rootDirectory;
        } else {
            fatherDir = directory.getFatherInode();
        }

        fatherDir.removeDirectory(directory);
    }

    public void addUser(String name) {
        for (User u : this.users) {
            if (u.getName().equals(name)) {
                System.out.println("User " + name + " already exists");
                return;
            }
        }
        User user = new User(name);
        this.users.add(user);
    }

    public boolean isCurrentUserAdmin() {
        return this.currentUser.isRoot();
    }

    public void changeCurrentUser(String name) {
        for (User user : this.users) {
            if (user.getName().equals(name)) {
                this.currentUser = user;
                return;
            }
        }

        System.out.println("User not find");
    }

    public void createFile(String fileName, Directory directory) {
        InodeHeader inodeHeader = new InodeHeader(this.currentUser.getUuid(), LocalDateTime.now());
        File newFile = new File(inodeHeader);
        directory.addFile(fileName, newFile);
    }

    public void updateFileAccess(String filename, Directory directory) {
        File file = directory.getFile(filename);

        if (file != null) {
            file.updateAccess();
        }
    }

    public void removeFile(String filename, Directory directory) {
        File file = directory.getFile(filename);
        directory.removeFile(filename);

        List<Block> fileBlocks = new ArrayList<>();
        fileBlocks.addAll(file.getDirectBlocks());
        fileBlocks.addAll(file.getIndirectBlocks());

        for (Block b : fileBlocks) {
            file.removeBlock(b);
            disk.removeBlock(b);
        }
    }

    public void writeContentInFile(String filename, Directory directory, String data) {
        File file = directory.getFile(filename);

        int numOfWrittenContent = 0;
        int end = 0;
        while (numOfWrittenContent < data.length()) {
            if (data.length() - numOfWrittenContent < Block.size) {
                end += data.length() - numOfWrittenContent;
            } else {
                end += Block.size - 1;
            }

            String fileContent = data.substring(numOfWrittenContent, end);
            numOfWrittenContent += end;
            Block block = disk.createBlockForContent(fileContent);
            if (block != null) {
                file.addBlock(block);
            }
        }

        updateFileAccess(filename, directory);
    }

    public void updateInodeOwner(Inode inode, String newUser) {
        for (User u : this.users) {
            if (u.getName().equals(newUser)) {
                inode.changeOwner(u.getUuid());
                break;
            }
        }
    }

    public void removeUser(String username) {
        User user = null;

        for (User u : this.users) {
            if (u.getName().equals(username)) {
                user = u;
                break;
            }
        }

        if (user != null) {
            this.users.remove(user);
        } else {
            System.out.println("Usuário não encontrado");
        }

    }

    @Override
    public String toString() {
        return this.currentUser.toString() + ":" + currentPath + "$ ";
    }
}
