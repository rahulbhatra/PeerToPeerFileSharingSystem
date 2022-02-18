package com.logging;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Callable;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher {
    private WatchService watchService;
    private Path path;
    private Boolean keepWatching;
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public DirectoryWatcher(Path directory) {
        try {
            path = directory;
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, new WatchEvent.Kind[]
                    {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
        } catch (IOException ioe) {
            System.err.println(DirectoryWatcher.class.getName() + ": IOException while creating Watch Service: " + ioe);
            ioe.printStackTrace();
        }
    }

    public void beginLogging(Callable<Void> callable) {
        this.keepWatching = true;
        while (this.keepWatching) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
                for (WatchEvent<?> e : watchKey.pollEvents()) {
                    WatchEvent<Path> ev = cast(e);
                    Path filePath = ev.context();
                    callable.call();

                    if (ENTRY_CREATE == e.kind()) {
                        System.out.println(ENTRY_CREATE + "-" + filePath.getFileName());
                    } else if (ENTRY_DELETE == e.kind()) {
                        System.out.println(ENTRY_DELETE + "-" + filePath.getFileName());
                    } else if (ENTRY_MODIFY == e.kind()) {
                        System.out.println(ENTRY_MODIFY + "-" + filePath.getFileName());
                    }
                }
                watchKey.reset();
            } catch (InterruptedException interruptedException) {
                System.err.println(DirectoryWatcher.class.getName() + ": InterruptedException while starting the Watch Service: " + interruptedException);
                interruptedException.printStackTrace();
                return;
            } catch (ClosedWatchServiceException closedWatchServiceException) {
                System.out.println("Closed Watch Service Exception");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void endLogging() {
        try {
            this.keepWatching = false;
            this.watchService.close();
        } catch (IOException ioException) {
            System.err.println(DirectoryWatcher.class.getName() + ": IOException while ending Watch Service: " + ioException);
            ioException.printStackTrace();
        }
    }
}
