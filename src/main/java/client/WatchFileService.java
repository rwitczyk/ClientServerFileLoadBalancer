package client;

import common.FileMock;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
public class WatchFileService {

    private WatchService watchService;

    public void init() {
        log.info("Start aplikacji");
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("clients");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    FileMock fileMock = FileMock.builder().fileName(event.context().toString()).build();
                    log.info("Dodano nowy plik - " + fileMock.toString());
                }
                key.reset();
            }
        } catch (IOException e) {
            log.error("Blad dostepu do katalogu", e);
        } catch (InterruptedException e) {
            log.error("Blad pobrania watchService", e);
        }

    }
}
