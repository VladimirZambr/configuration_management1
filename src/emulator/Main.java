package emulator;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String vfsPath = (args.length > 0) ? args[0] : "VFS/VFS_big.xml";
        String scriptPath = (args.length > 1) ? args[1] : null;

        File file = new File(vfsPath);
        String fileName = file.getName();
        String vfsName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

        System.out.println("[DEBUG] VFS Path: " + vfsPath);
        System.out.println("[DEBUG] Startup Script: " + (scriptPath != null ? scriptPath : "None (Interactive Mode)"));

        Shell shell = new Shell(vfsName, vfsPath);

        if (scriptPath != null) {
            shell.executeScript(scriptPath);
        } else {
            shell.startInteractive();
        }
    }
}