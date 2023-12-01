package main;

import console.Console;
import filesystem.FileSystem;

public class Main {
    public static FileSystem fileSystem;
    public static void main(String[] args) {
        Console console = new Console();
        console.run();
    }
}