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
    private final String vfsName;
    private final String vfsPhysicalPath;
    private boolean running = true;
    private static final Pattern ENV_VAR = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");

    public Shell(String vfsName, String vfsPhysicalPath) {
        this.vfsName = vfsName;
        this.vfsPhysicalPath = vfsPhysicalPath;
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
            case "ls", "cd" -> System.out.println("Command: " + cmd + ", arguments: " + args);
            default -> System.err.println("Error: unknown command or invalid arguments: " + cmd);
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