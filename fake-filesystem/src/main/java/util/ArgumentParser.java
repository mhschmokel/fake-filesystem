package util;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {

    public static Map<String, String> parse(String[] args, int start) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = start; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                argMap.put(args[i], args[i + 1]);
            } else {
                System.out.println("Missing value for argument: " + args[i]);
            }
        }
        return argMap;
    }

    public static String parseCommand(String[] args) {
        return args[1];
    }
}
