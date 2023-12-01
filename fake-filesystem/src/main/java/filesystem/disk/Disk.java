package filesystem.disk;

import filesystem.inode.Directory;
import filesystem.inode.InodeHeader;
import lombok.RequiredArgsConstructor;
import user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Disk {
    private final int totalBlocks;
    private int availableBlocks;
    private final List<Block> blocks = new ArrayList<>();

    public Disk(int totalBlocks) {
        this.totalBlocks = totalBlocks;
        this.availableBlocks = totalBlocks;
    }

    public Directory createDirectory(User currentUser, Directory fatherDirectory) {
        if (this.availableBlocks <= 0) {
            System.out.println("No blocks available. Can't create directory");
            return null;
        }

        InodeHeader inodeHeader = new InodeHeader(currentUser.getUuid(), LocalDateTime.now());
        return new Directory(inodeHeader, createBlock(), fatherDirectory);
    }

    public Block createBlockForContent(String data) {
        if (this.availableBlocks <= 0) {
            System.out.println("No blocks available. Can't store file content");
            return null;
        }

        Block block = new Block();
        block.setData(data);
        this.blocks.add(block);
        this.availableBlocks--;
        return block;
    }

    private Block createBlock() {
        Block block = new Block();
        this.blocks.add(block);
        this.availableBlocks--;
        return block;
    }

    public void removeBlock(Block block) {
        this.blocks.remove(block);
        this.availableBlocks++;
    }

}
