package demo.jeromq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

import java.util.UUID;

public class SubPushForwarder implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ZMQ.Context CTX;
    private String id;
    private boolean isRunning;

    private String inEndPoint;
    private String outEndPoint;
    private String controlEndPoint;

    public SubPushForwarder(ZMQ.Context ctx, String inEndPoint,
                            String outEndPoint, String controlEndPoint) {
        this.CTX = ctx;
        this.inEndPoint = inEndPoint;
        this.outEndPoint = outEndPoint;
        this.controlEndPoint = controlEndPoint;
        this.id = "subpushforwarder-" + UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        isRunning = true;

        ZMQ.Socket sub = CTX.socket(ZMQ.SUB);
        sub.setHWM(0L);
        sub.connect(inEndPoint);
        sub.subscribe("".getBytes());

        ZMQ.Socket push = CTX.socket(ZMQ.PUSH);
        push.setHWM(0L);
        push.bind(outEndPoint);

        ZMQ.Socket internalSocket = CTX.socket(ZMQ.PAIR);
        internalSocket.bind("inproc://" + id);

        ZMQ.Poller poller = new ZMQ.Poller(2);
        poller.register(internalSocket, ZMQ.Poller.POLLIN);
        poller.register(sub, ZMQ.Poller.POLLIN);

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
                if ((msg = sub.recvStr()) != null) {
                    push.send(msg);
                }
            }
        }

        sub.close();
        push.close();
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
