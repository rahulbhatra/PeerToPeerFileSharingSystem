package com.logging;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;

public class DirectoryWatcher {
    private Path path;
    private Boolean keepWatching;
    private WatchService watchService;

    public DirectoryWatcher(String directory) {
        try {
            this.path = Paths.get(directory);
            this.watchService = FileSystems.getDefault().newWatchService();
            this.path.register(watchService, new WatchEvent.Kind[]
                    {
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY
                    }, SensitivityWatchEventModifier.HIGH);
        } catch (IOException ioException) {
            System.err.println(DirectoryWatcher.class.getName() + ": IOException while creating Watch Service: ");
            ioException.printStackTrace();
        }
    }

    public void beginWatchingChanges(Callable<Void> callable) {
        this.keepWatching = true;
        while (this.keepWatching) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                    Path filePath = watchEvent.context();
                    callable.call();

                    if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
                        System.out.println(StandardWatchEventKinds.ENTRY_CREATE + "-" + filePath.getFileName());
                    } else if (StandardWatchEventKinds.ENTRY_DELETE == event.kind()) {
                        System.out.println(StandardWatchEventKinds.ENTRY_DELETE + "-" + filePath.getFileName());
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind()) {
                        System.out.println(StandardWatchEventKinds.ENTRY_MODIFY + "-" + filePath.getFileName());
                    }
                }
                watchKey.reset();
            } catch (InterruptedException interruptedException) {
                System.err.println(DirectoryWatcher.class.getName() + " Exception while creating the watch service.");
                interruptedException.printStackTrace();
            } catch (ClosedWatchServiceException closedWatchServiceException) {
                System.out.println("Closed Watch Service Exception | completely fine to have this");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void endWatchingChanges() {
        try {
            this.watchService.close();
            this.keepWatching = false;
        } catch (IOException ioException) {
            System.err.println(DirectoryWatcher.class.getName() + " Exception while ending watch service.");
            ioException.printStackTrace();
        }
    }
}
