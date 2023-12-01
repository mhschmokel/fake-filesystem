package filesystem.inode;

import filesystem.disk.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import user.User;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Inode {
    private final UUID uuid = UUID.randomUUID();
    protected final InodeHeader inodeHeader;
    protected final char fileTypeIndicator;
    protected String[] ownerPermission = new String[]{"r", "w", "x"};
    protected String[] groupPermission = new String[]{"-","-","-"};
    protected String[] otherPermission = new String[]{"r","w","x"};

    public void changeOwner(UUID newOwnerUuid) {
        this.inodeHeader.updateOwner(newOwnerUuid);
    }

    protected void updateSize(int size) {
        this.inodeHeader.updateSize(size);
    }

    public void changeOwnerPermission(String read, String write, String execute) {
        this.ownerPermission = new String[]{read, write, execute};
    }

    public void changeGroupPermission(String read, String write, String execute) {
        this.groupPermission = new String[]{read, write, execute};
    }

    public void changeOtherPermission(String read, String write, String execute) {
        this.otherPermission = new String[]{read, write, execute};
    }

    public String getPermissionsString() {
        return this.fileTypeIndicator
                + String.join("", this.ownerPermission)
                + String.join("", this.groupPermission)
                + String.join("", this.otherPermission);
    }
}
