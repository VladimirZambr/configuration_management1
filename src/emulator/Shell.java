package emulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell {
    private VFS vfs;
    private final String vfsName;
    private final String vfsPath;
    private boolean running = true;
    private static final Pattern ENV_VAR = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");

    public Shell(String vfsName, String vfsPath) {
        this.vfsName = vfsName;
        this.vfsPath = vfsPath;
        this.vfs = new VFS(vfsPath);
    }

    public void startInteractive() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                System.out.print(vfsName + ":/$ ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                executeLine(scanner.nextLine());
            }
        }
    }

    public void executeScript(String scriptPath) {
        System.out.println("Executing script: " + scriptPath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {
            String rawLine;
            while ((rawLine = reader.readLine()) != null) {
                String line = rawLine.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                System.out.print(vfsName + ":/$ ");
                System.out.println(line);
                executeLine(line);
                if (!running) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to execute script file: " + scriptPath + " (" + e.getMessage() + ")");
        }
    }

    private void executeLine(String rawLine) {
        String line = expandEnv(rawLine);
        List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\s+")));
        if (tokens.isEmpty() || tokens.get(0).isEmpty()) {
            return;
        }

        String cmd = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        switch (cmd) {
            case "exit" -> running = false;
            case "ls" -> {
                List<String> items = vfs.listDirectory();
                for (String item : items) {
                    System.out.println(item);
                }
            }
            case "cd" -> {
                if (args.isEmpty()) {
                    System.out.println("cd: missing path argument");
                } else {
                    String path = args.get(0);
                    boolean success = vfs.changeDirectory(path);
                    if (!success) {
                        System.out.println("cd: no such file or directory: " + path);
                    }
                }
            }
            default -> System.err.println("[ERROR] Unknown command or invalid arguments: " + cmd);
        }
    }

    private String expandEnv(String input) {
        Matcher matcher = ENV_VAR.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String val = System.getenv(matcher.group(1));
            matcher.appendReplacement(sb, val != null ? Matcher.quoteReplacement(val) : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}