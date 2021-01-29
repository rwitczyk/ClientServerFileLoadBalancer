package common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Random;

@Builder
@Getter
@ToString
public class FileMock {

    private final String fileName;

    private final int size = new Random().nextInt(10000);
}
