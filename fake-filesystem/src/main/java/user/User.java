package user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {
    protected UUID uuid;
    protected String name;
    private boolean hasAdminPrivileges = false;

    public User(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public void grantAdminPrivileges() {
        this.hasAdminPrivileges = true;
    }

    public void removeAdminPrivileges() {
        this.hasAdminPrivileges = false;
    }

    public boolean isRoot() {
        return hasAdminPrivileges;
    }

    @Override
    public String toString() {
        return this.name + "#" + this.uuid.toString().substring(0, 8);
    }
}
