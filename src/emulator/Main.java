package emulator;

public class Main {
    public static void main() {
        String vfsName = "test_vfs";
        Shell shell = new Shell(vfsName);
        shell.startInteractive();
    }
}