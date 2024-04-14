package nycto.clipboard_updater;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchFolder {

    public void watchFolder() {

        try {
            System.out.println("Watching directory for changes");

            // Creates a watch service.
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Gets the path of the directory to monitor.
            Path directory = Path.of("C:\\users\\myName\\Desktop\\Test");

            // Registers the directory with the watch service to which this object is to
            // be registered and the events for which this object should be registered.
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            try {
                // Waits for and retrieves events.
                WatchKey key;

                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                        // Gets the filename from the event context.
                        Path filename = pathEvent.context();

                        String filenameString = filename.toString();

                        if (filenameString.endsWith(".crdownload")) break;

                        // Checks the type of the event.
                        WatchEvent.Kind<?> kind = event.kind();

                        // Performs the necessary action with the create event.
                        // The code up to the break runs twice.
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            Toolkit toolkit = Toolkit.getDefaultToolkit();

                            Clipboard clipboard = toolkit.getSystemClipboard();

                            String newFilename = createNewFilename(filenameString);

                            Transferable clipboardContents = clipboard.getContents(null);

                            boolean clipboardHasString = (clipboardContents != null) &&
                                    clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor);

                            if (clipboardHasString) {
                                try {
                                    String clipboardString =
                                            (String) clipboardContents.getTransferData(DataFlavor.stringFlavor);

                                    if (clipboardString.equals(newFilename)) break;
                                } catch (UnsupportedFlavorException | IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            System.out.println("A new file is created: " + filename);

                            StringSelection newFilenameStringSelection = new StringSelection(newFilename);

                            clipboard.setContents(newFilenameStringSelection, null);

                            System.out.println("Clipboard set to: " + newFilename);
                        }
                    }

                    // Resets the watch key everytime for continuing to use it for further event retrieval.
                    boolean valid = key.reset();

                    if (!valid) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Restores interrupted status.
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createNewFilename(String filenameString) {
        String[] filenameParts = filenameString.split(" \\(");

        // Example: Very Cool Car (21).jpg => "Very Cool Car"
        String filenameBeforeWhitespaceAndFirstBracket = filenameParts[0];

        String filenameAfterFirstBracket = filenameParts[1];

        int filenameCounter = Integer.parseInt(filenameAfterFirstBracket.replaceAll("^\\D*?(-?\\d+).*$", "$1"));

        return filenameBeforeWhitespaceAndFirstBracket + " (" + (filenameCounter + 1) + ")";
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
        System.out.println("Starting a background thread for watching folder");

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(new WatchCallable());

        System.out.println("After submitting Callable for watching folder");
    }
}
