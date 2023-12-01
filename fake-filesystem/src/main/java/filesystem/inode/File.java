package filesystem.inode;

import filesystem.disk.Block;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class File extends Inode{
    private final List<Block> directBlocks = new ArrayList<>();
    private int availableDirectBlocks = 10;
    private final List<Block> indirectBlocks = new ArrayList<>();
    public File(InodeHeader inodeHeader) {
        super(inodeHeader, '-');
    }

    public void addBlock(Block block) {
        if (this.availableDirectBlocks > 0) {
            this.directBlocks.add(block);
            this.availableDirectBlocks--;
            return;
        }

        this.indirectBlocks.add(block);
    }

    public void removeBlock(Block block) {
        this.directBlocks.remove(block);
        this.indirectBlocks.remove(block);
    }

    public void updateAccess() {
        this.inodeHeader.updateLastAccessDate();
        this.inodeHeader.updateLastChangeDate();
        this.updateSize(getSize());
    }

    private int getSize() {
        List<Block> allBlocks = new ArrayList<>();
        allBlocks.addAll(this.directBlocks);
        allBlocks.addAll(this.indirectBlocks);

        int size = 0;
        for (Block b : allBlocks) {
            size += b.getData().length();
        }
        return size;
    }

    public String getData() {
        StringBuilder data = new StringBuilder();

        for (Block b : this.directBlocks) {
            data.append(b.getData());
        }
        for (Block b : this.indirectBlocks) {
            data.append(b.getData());
        }

        return data.toString();
    }

    @Override
    public String toString() {
        String fileHeader = this.inodeHeader.toString();
        String filePermissions = this.getPermissionsString();
        String fileData = "\n\n== File Content ==\n";
        String fileContent = getData();

        StringBuilder allContent = new StringBuilder()
                .append(fileHeader)
                .append("\nPermissions: ")
                .append(filePermissions)
                .append("\nSize (bytes): ")
                .append(this.inodeHeader.getFileSize())
                .append(fileData)
                .append(fileContent);

        return allContent.toString();
    }
}
