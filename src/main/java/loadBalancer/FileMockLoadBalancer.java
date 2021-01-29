package loadBalancer;

import common.FileMock;
import lombok.Data;

@Data
public class FileMockLoadBalancer implements Comparable<FileMockLoadBalancer> {

    private FileMock fileMock;
    private int priority;

    public FileMockLoadBalancer(FileMock fileMock) {
        this.fileMock = fileMock;
    }

    @Override
    public int compareTo(FileMockLoadBalancer o) {
        return o.getPriority() > o.getPriority() ? 1 : -1;
    }
}
