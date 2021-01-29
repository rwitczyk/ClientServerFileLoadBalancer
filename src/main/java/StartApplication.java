import client.WatchFileService;

public class StartApplication {

    public static void main(String[] args) {
        WatchFileService watchFileService = new WatchFileService();
        watchFileService.init();
    }
}
