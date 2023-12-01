package filesystem.inode;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Getter
public class InodeHeader {
    private UUID ownerUuid;
    private LocalDateTime lastChangeDate;
    private LocalDateTime lastAccessDate;
    private final LocalDateTime creationDate;
    private double fileSize;

    public InodeHeader(UUID ownerUuid, LocalDateTime creationDate) {
        this.ownerUuid = ownerUuid;
        this.creationDate = creationDate;
        this.lastChangeDate = this.creationDate;
        this.lastAccessDate = this.creationDate;

        this.fileSize = 1.0;
    }

    public void updateLastChangeDate() {
        this.lastChangeDate = LocalDateTime.now();
    }

    public void updateLastAccessDate() {
        this.lastAccessDate = LocalDateTime.now();
    }

    public void updateFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder fileHeader = new StringBuilder()
                .append("==File Header==\n")
                .append("Owner Uuid: ")
                .append(this.ownerUuid)
                .append("\nCreation date: ")
                .append(this.creationDate.format(formatter))
                .append("\nLast access date: ")
                .append(this.lastAccessDate.format(formatter))
                .append("\nLast change date: ")
                .append(this.lastChangeDate.format(formatter));

        return fileHeader.toString();
    }

    public void updateOwner(UUID newOwnerUuid) {
        this.ownerUuid = newOwnerUuid;
    }
}
