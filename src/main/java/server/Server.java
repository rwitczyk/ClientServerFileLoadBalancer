package server;

import common.FileMock;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Server {

    public void uploadFile(FileMock fileMock) {
        new File("server/" + fileMock.getUserName()).mkdir();
        File createFile = new File("server/" + fileMock.getUserName() + "/" + fileMock.getFileName());
        try {
            saveFileOnServer(fileMock, createFile);
        } catch (IOException e) {
            log.error("Blad tworzenia pliku usera: " + fileMock.getUserName() + " na serwerze");
        }
    }

    private void saveFileOnServer(FileMock fileMock, File createFile) throws IOException {
        if (createFile.createNewFile()) {
            log.info("Plik zostal poprawnie dodany na serwer - " + fileMock.toString());
        } else {
            log.info("Plik juz istnieje!");
        }
    }
}
