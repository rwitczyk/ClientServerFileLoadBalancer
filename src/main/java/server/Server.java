package server;

import common.FileMock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
    public void uploadFile(FileMock fileMock) {
        log.info("Plik zostal poprawnie dodany na serwer - " + fileMock.toString());
    }
}
