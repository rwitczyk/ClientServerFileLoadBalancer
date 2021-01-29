import client.WatchFileService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class StartApplication {

    public static void main(String[] args) {
        log.info("Start aplikacji");
        WatchFileService watchFileService = new WatchFileService();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        executor.execute(() -> watchFileService.init("user1"));
        executor.execute(() -> watchFileService.init("user2"));
        executor.execute(() -> watchFileService.init("user3"));
    }
}
