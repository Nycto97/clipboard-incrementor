package nycto.clipboard_updater;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchFolder {

    public void watchFolder() {

        try {
            System.out.println("Watching directory for changes");

            // STEP 1: Create a watch service
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // STEP 2: Get the path of the directory which you want to monitor.
            Path directory = Path.of("C:\\users\\myName\\Desktop\\Test");

            // STEP 3: Register the directory with the watch service
            WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            // STEP 4: Poll for events
            while (true) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    // STEP 5: Get file name from even context
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();

                    // STEP 6: Check type of event.
                    WatchEvent.Kind<?> kind = event.kind();

                    // STEP 7: Perform necessary action with the event
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        String[] fileNameParts = fileName.toString().split(" \\(");

                        // Example: Very Cool Car (21).jpg => "Very Cool Car"
                        String fileNameBWAFB = fileNameParts[0];
                        // BFBAW => Before Whitespace And First Bracket

                        int fileNameCounter = Integer.parseInt(fileName.toString().replaceAll("^\\D*?(-?\\d+).*$", "$1"));
                        int newFileNameCounter = fileNameCounter + 1;

                        String newFileName = fileNameBWAFB + " (" + newFileNameCounter + ")";

                        System.out.println("A new file is created: " + fileName);
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Clipboard clipboard = toolkit.getSystemClipboard();
                        StringSelection strSel = new StringSelection(newFileName);
                        clipboard.setContents(strSel, null);

                        System.out.println("Clipboard set to: " + newFileName);
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("A file has been deleted: " + fileName);
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("A file has been modified: " + fileName);
                    }
                }

                // STEP 8: Reset the watch key everytime for continuing to use it for further event polling
                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class WatchCallable implements Callable<Void> {
        @Override
        public Void call() {

            WatchFolder wf = new WatchFolder();
            wf.watchFolder();

            return null;
        }

    }

    public static void main(String[] args) {
        System.out.println("Starting a background thread for watching folders");

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new WatchCallable());

        System.out.println("After submitting Callable for watching folder");
    }
}
