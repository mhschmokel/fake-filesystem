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
        super(inodeHeader);
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
        String fileData = "\n\n== File Content ==\n";
        String fileContent = getData();

        StringBuilder allContent = new StringBuilder()
                .append(fileHeader)
                .append(fileData)
                .append(fileContent);

        return allContent.toString();
    }
}
