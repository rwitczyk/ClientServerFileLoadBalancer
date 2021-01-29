package common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Random;

@Builder
@Getter
public class FileMock {

    private final String fileName;

    private final int size = new Random().nextInt(100) + 1;

    private LocalDateTime sentDate;

    @Override
    public String toString() {
        return "fileName: " + fileName + ", data dodania: " + sentDate + ", size: " + size + "MB";
    }
}
