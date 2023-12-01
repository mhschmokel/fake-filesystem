package filesystem.inode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import user.User;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Inode {
    private final UUID uuid = UUID.randomUUID();
    protected final InodeHeader inodeHeader;

    public void changeOwner(UUID newOwnerUuid) {
        this.inodeHeader.updateOwner(newOwnerUuid);
    }
}
