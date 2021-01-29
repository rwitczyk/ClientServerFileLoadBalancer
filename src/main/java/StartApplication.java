import client.WatchFileService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartApplication {

    public static void main(String[] args) {
        log.info("Start aplikacji");
        WatchFileService watchFileService = new WatchFileService();
        watchFileService.init();
    }
}
