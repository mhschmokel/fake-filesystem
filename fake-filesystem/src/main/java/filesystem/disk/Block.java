package filesystem.disk;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Block {
    public static final int size = 512;
    private int availableSpace = Block.size;
    private final UUID uuid = UUID.randomUUID();
    private String data;

    public void setData(String data) {
        this.availableSpace = Block.size - data.length();
        this.data = data;
    }
}
