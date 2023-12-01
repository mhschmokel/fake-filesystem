package user;

public class Root extends User {
    public Root() {
        super("root");
    }
    @Override
    public boolean isRoot() {
        return true;
    }
}
