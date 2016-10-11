package demo.jeromq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

import java.util.UUID;

public class PullPubForwarder implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ZMQ.Context CTX;
    private String id;
    private boolean isRunning;

    private String inEndPoint;
    private String outEndPoint;
    private String controlEndPoint;

    public PullPubForwarder(ZMQ.Context ctx, String inEndPoint,
                            String outEndPoint, String controlEndPoint) {
        this.CTX = ctx;
        this.inEndPoint = inEndPoint;
        this.outEndPoint = outEndPoint;
        this.controlEndPoint = controlEndPoint;
        this.id = "pullpubforwarder-" + UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        isRunning = true;

        ZMQ.Socket pull = CTX.socket(ZMQ.PULL);
        pull.setHWM(0L);
        pull.bind(inEndPoint);

        ZMQ.Socket pub = CTX.socket(ZMQ.PUB);
        pub.setHWM(0L);
        pub.connect(outEndPoint);

        ZMQ.Socket internalSocket = CTX.socket(ZMQ.PAIR);
        internalSocket.bind("inproc://" + id);

        ZMQ.Poller poller = new ZMQ.Poller(2);
        poller.register(internalSocket, ZMQ.Poller.POLLIN);
        poller.register(pull, ZMQ.Poller.POLLIN);

        ZMQ.Socket req = CTX.socket(ZMQ.REQ);
        req.connect(controlEndPoint);
        req.send("");
        req.recv();
        req.close();

        String msg;
        while (true) {
            poller.poll();
            if (poller.pollin(0)) {
                break;
            }
            if (poller.pollin(1)) {
                if ((msg = pull.recvStr()) != null) {
                    pub.send(msg);
                }
            }
        }

        pull.close();
        pub.close();
        internalSocket.close();
    }

    public void stop() {
        if (isRunning) {
            LOGGER.info("Stopping {}", id);
            ZMQ.Socket internalSocket = CTX.socket(ZMQ.PAIR);
            internalSocket.connect("inproc://" + id);
            internalSocket.send("");
            internalSocket.close();
            isRunning = false;
        }
    }

}
