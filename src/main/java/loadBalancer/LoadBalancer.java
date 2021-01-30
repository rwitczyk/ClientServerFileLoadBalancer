package loadBalancer;

import common.FileMock;
import lombok.extern.slf4j.Slf4j;
import server.Server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class LoadBalancer {

    private static final int NUMBER_OF_THREADS = 3;

    private Server server = new Server();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private BlockingQueue<FileMockLoadBalancer> collectionsFiles = new PriorityBlockingQueue<>(1024);

    private static LoadBalancer INSTANCE;

    public static LoadBalancer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoadBalancer();
        }

        return INSTANCE;
    }


    public void addFileToSend(FileMock fileMock) {
        try {
            collectionsFiles.put(new FileMockLoadBalancer(fileMock));
            log.info("Thread-" + Thread.currentThread().getId() + " | Dodano plik: " + fileMock.getFileName() + " do kolejki. Aktualna liczba plikow do przetworzenia: " + collectionsFiles.size());
        } catch (InterruptedException e) {
            log.error("Blad dodawania pliku na kolejke", e);
        }
    }

    public void initSendFilesToServer() {
        executor.submit(() -> {
            while (true) {
                if (executor.getActiveCount() < NUMBER_OF_THREADS) {
                    executor.submit(() -> {
                        recalculatePriorityToAllFiles();
                        try {
                            FileMockLoadBalancer fileMockLoadBalancer = collectionsFiles.take();
                            log.info("Thread-" + Thread.currentThread().getId() + " | Zdejmuje jeden plik z kolejki");
                            log.info("Thread-" + Thread.currentThread().getId() + " | Aktualna liczba plikow w kolejce: " + collectionsFiles.size());
                            log.info("Thread-" + Thread.currentThread().getId() + " | Aktualna liczba zajętych wątków: " + (executor.getActiveCount() - 1)); // odejmuje jeden bo jeden watek jest zawsze dla dzialania petli while
                            log.info("Thread-" + Thread.currentThread().getId() + " | Rozpoczęto przetwarzanie pliku: " + fileMockLoadBalancer.getFileMock().toString());
                            Thread.sleep(fileMockLoadBalancer.getFileMock().getSize() * 200);
                            server.uploadFile(fileMockLoadBalancer.getFileMock());
                        } catch (InterruptedException e) {
                            log.error("Thread-" + Thread.currentThread().getId() + " | Blad przetwarzania pliku");
                        }
                    });
                }
            }
        });
    }

    private void recalculatePriorityToAllFiles() {
        log.info("Thread-" + Thread.currentThread().getId() + " | Przeliczam priorytet dla kolekcji plikow");
        for (FileMockLoadBalancer fileMockLoadBalancer : collectionsFiles) {
            fileMockLoadBalancer.setPriority(calculatePriority(fileMockLoadBalancer));
        }

        FileMockLoadBalancer fileMockLoadBalancer;
        BlockingQueue<FileMockLoadBalancer> tempQueue = new PriorityBlockingQueue<>(1024);
        while ((fileMockLoadBalancer = collectionsFiles.poll()) != null) {
            tempQueue.add(fileMockLoadBalancer);
        }
        collectionsFiles.addAll(tempQueue);
    }

    private int calculatePriority(FileMockLoadBalancer fileMockLoadBalancer) {
        int fileSize = fileMockLoadBalancer.getFileMock().getSize();
        LocalDateTime sentDate = fileMockLoadBalancer.getFileMock().getSentDate();
        Duration durationBetweenNowAndSentDate = Duration.between(sentDate, LocalDateTime.now());
        return (Integer.valueOf(String.valueOf(durationBetweenNowAndSentDate.getSeconds() + 1)) * 100) / fileSize;
    }
}
