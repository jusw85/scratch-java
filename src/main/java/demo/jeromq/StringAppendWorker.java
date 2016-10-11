package demo.jeromq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

import java.util.UUID;

public class StringAppendWorker implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ZMQ.Context CTX;
    private String id;
    private boolean isRunning;

    private String inEndPoint;
    private String outEndPoint;
    private String controlEndPoint;
    private String stringToAppend;

    public StringAppendWorker(ZMQ.Context ctx, String inEndPoint, String outEndPoint,
                              String controlEndPoint, String stringToAppend) {
        this.CTX = ctx;
        this.inEndPoint = inEndPoint;
        this.outEndPoint = outEndPoint;
        this.controlEndPoint = controlEndPoint;
        this.stringToAppend = stringToAppend;
        this.id = "appendworker-" + UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        isRunning = true;

        ZMQ.Socket inSocket = CTX.socket(ZMQ.PULL);
        inSocket.setHWM(0L);
        inSocket.connect(inEndPoint);

        ZMQ.Socket outSocket = CTX.socket(ZMQ.PUSH);
        outSocket.setHWM(0L);
        outSocket.connect(outEndPoint);

        ZMQ.Socket internalSocket = CTX.socket(ZMQ.PAIR);
        internalSocket.bind("inproc://" + id);

        ZMQ.Poller poller = new ZMQ.Poller(2);
        poller.register(internalSocket, ZMQ.Poller.POLLIN);
        poller.register(inSocket, ZMQ.Poller.POLLIN);

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
                if ((msg = inSocket.recvStr()) != null && msg.length() > 0) {
                    msg += stringToAppend;
                    outSocket.send(msg);
                }
            }
        }
        inSocket.close();
        outSocket.close();
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
