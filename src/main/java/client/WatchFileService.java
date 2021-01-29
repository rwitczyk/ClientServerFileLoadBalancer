package client;

import common.FileMock;
import loadBalancer.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Slf4j
public class WatchFileService {

    private LoadBalancer loadBalancer = LoadBalancer.getInstance();

    public void init(String userName) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("clients/" + userName);
            new File("clients/" + userName).mkdir();

            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    FileMock fileMock = FileMock.builder()
                            .fileName(event.context().toString())
                            .sentDate(LocalDateTime.now())
                            .userName(userName)
                            .build();
                    log.info("Wykryto nowy plik - " + fileMock.toString());
                    loadBalancer.sendFile(fileMock);
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
