package filesystem.inode;

import filesystem.disk.Block;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Directory extends Inode {
    private final Map<String, Directory> directSubdirectories = new HashMap<>();
    private final Map<String, File> directFiles = new HashMap<>();
    private int availableDirectBlocks = 10;
    private final Map<String, Directory> indirectSubdirectories = new HashMap<>();
    private final Map<String, File> indirectFiles = new HashMap<>();
    private final Block associatedBlock;
    private final Directory fatherInode;

    public Directory(InodeHeader inodeHeader, Block associatedBlock, Directory fatherInode) {
        super(inodeHeader, 'd');
        this.associatedBlock = associatedBlock;
        this.fatherInode = fatherInode;
    }

    public void addFile(String fileName, File file) {
        if (this.availableDirectBlocks > 0) {
            directFiles.put(fileName, file);
            availableDirectBlocks--;
            return;
        }

        indirectFiles.put(fileName, file);
    }

    public void addSubdirectory(String subdirectoryName, Directory subdirectory) {
        if (this.availableDirectBlocks > 0) {
            directSubdirectories.put(subdirectoryName, subdirectory);
            this.availableDirectBlocks--;
            return;
        }

        if (directSubdirectories.containsKey(subdirectoryName)) {
            return;
        }

        indirectSubdirectories.put(subdirectoryName, subdirectory);
    }

    public boolean containsSubdirectory(String dirName) {
        return this.directSubdirectories.containsKey(dirName)
                || this.indirectSubdirectories.containsKey(dirName);
    }

    public Directory getSubdirectory(String dirName) {
        if (this.directSubdirectories.containsKey(dirName)) {
            return this.directSubdirectories.get(dirName);
        }

        return this.indirectSubdirectories.get(dirName);
    }

    public File getFile(String filename) {
        if (this.directFiles.containsKey(filename)) {
            return this.directFiles.get(filename);
        }

        return this.indirectFiles.get(filename);
    }

    public List<String> getSubfolders() {
        List<String> subfolders = new ArrayList<>();

        Map<String, Inode> allSubfolders = new HashMap<>();
        allSubfolders.putAll(directSubdirectories);
        allSubfolders.putAll(indirectSubdirectories);

        for (Map.Entry<String, Inode> entry : allSubfolders.entrySet()) {
            String header = entry.getValue().getHeaderInline();
            String name = entry.getKey();
            String line = header + name;
            subfolders.add(line);
        }

        return subfolders;
//        List<String> subfolders = new ArrayList<>(directSubdirectories.keySet());
//        subfolders.addAll(indirectSubdirectories.keySet());
//        return subfolders;
    }

    public List<String> getFiles() {
        List<String> files = new ArrayList<>();

        Map<String, Inode> allFiles = new HashMap<>();
        allFiles.putAll(directFiles);
        allFiles.putAll(indirectFiles);

        for (Map.Entry<String, Inode> entry : allFiles.entrySet()) {
            String header = entry.getValue().getHeaderInline();
            String name = entry.getKey();
            String line = header + name;
            files.add(line);
        }

        return files;
//        List<String> files = new ArrayList<>(directFiles.keySet());
//        files.addAll(indirectFiles.keySet());
//        return  files;
    }

    public boolean isEmpty() {
        return this.directSubdirectories.isEmpty()
                && this.indirectSubdirectories.isEmpty()
                && this.directFiles.isEmpty()
                && this.indirectFiles.isEmpty();
    }

    public void removeDirectory(Directory dir) {
        String key = getDirectoryKeyByValue(dir);

        if (key.isEmpty()) {
            return;
        }

        this.directSubdirectories.remove(key);
        this.indirectSubdirectories.remove(key);
    }

    public void removeFile(String fileName) {
        this.directFiles.remove(fileName);
        this.indirectFiles.remove(fileName);
    }

    public String getDirectoryKeyByValue(Directory directory) {
        for (Map.Entry<String, Directory> entry : this.directSubdirectories.entrySet()) {
            if (entry.getValue().getUuid().equals(directory.getUuid())) {
                return entry.getKey();
            }
        }

        for (Map.Entry<String, Directory> entry : this.indirectSubdirectories.entrySet()) {
            if (entry.getValue().getUuid().equals(directory.getUuid())) {
                return entry.getKey();
            }
        }

        return "";
    }

    public boolean containsFile(String filename) {
        return this.directFiles.containsKey(filename) || this.indirectFiles.containsKey(filename);
    }

    @Override
    public String toString() {
        String result = "";

        return result;
    }
}
