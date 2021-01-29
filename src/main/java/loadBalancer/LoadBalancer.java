package loadBalancer;

import common.FileMock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.Server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RequiredArgsConstructor(staticName = "getInstance")
public class LoadBalancer {

    private Server server = new Server();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    private List<FileMockLoadBalancer> collectionsFiles = new ArrayList<>();

    public void sendFile(FileMock fileMock) {
        collectionsFiles.add(new FileMockLoadBalancer(fileMock));
        executor.submit(() -> {
            try {
                recalculatePriorityToAllFiles();
                Collections.sort(collectionsFiles);

//                collectionsFiles.sort((o1, o2) -> o1.getPriority() > o2.getPriority() ? 1 : 0);
                FileMockLoadBalancer fileMockLoadBalancer = collectionsFiles.remove(0);
                log.info("Aktualna liczba zajętych wątków: " + executor.getActiveCount());
                log.info("Rozpoczęto przetwarzanie pliku: " + fileMockLoadBalancer.getFileMock().toString());
                Thread.sleep(fileMockLoadBalancer.getFileMock().getSize() * 200);
                server.uploadFile(fileMockLoadBalancer.getFileMock());
            } catch (InterruptedException e) {
                log.error("Blad przetwarzania pliku: " + fileMock.toString());
            }
        });
    }

    private void recalculatePriorityToAllFiles() {
        for (FileMockLoadBalancer fileMockLoadBalancer : collectionsFiles) {
            fileMockLoadBalancer.setPriority(calculatePriority(fileMockLoadBalancer));
        }
    }

    private int calculatePriority(FileMockLoadBalancer fileMockLoadBalancer) {
        int fileSize = fileMockLoadBalancer.getFileMock().getSize();
        LocalDateTime sentDate = fileMockLoadBalancer.getFileMock().getSentDate();
        Duration durationBetweenNowAndSentDate = Duration.between(sentDate, LocalDateTime.now());
        return (Integer.valueOf(String.valueOf(durationBetweenNowAndSentDate.getSeconds())) * 100) / fileSize;
    }
}
