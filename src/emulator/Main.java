package emulator;

public class Main {
    public static void main(String[] args) {

        String vfsPhysicalPath = (args.length > 0) ? args[0] : "./default_vfs_root";
        String scriptPath = (args.length > 1) ? args[1] : null;

        System.out.println("[DEBUG] VFS Physical Path: " + vfsPhysicalPath);
        System.out.println("[DEBUG] Startup Script: " + (scriptPath != null ? scriptPath : "None (Interactive Mode)"));

        Shell shell = new Shell("test_vfs", vfsPhysicalPath);

        if (scriptPath != null) {
            shell.executeScript(scriptPath);
        } else {
            shell.startInteractive();
        }
    }
}