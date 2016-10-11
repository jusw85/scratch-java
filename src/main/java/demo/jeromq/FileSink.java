package demo.jeromq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class FileSink implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ZMQ.Context CTX;
    private String id;
    private boolean isRunning;

    private String inEndPoint;
    private String controlEndPoint;

    private File outFile;
    private boolean append;

    public FileSink(ZMQ.Context ctx, String inEndPoint, File outFile,
                    String controlEndPoint) {
        this(ctx, inEndPoint, outFile, controlEndPoint, false);
    }

    public FileSink(ZMQ.Context ctx, String inEndPoint, File outFile,
                    String controlEndPoint, boolean append) {
        this.CTX = ctx;
        this.inEndPoint = inEndPoint;
        this.outFile = outFile;
        this.controlEndPoint = controlEndPoint;
        this.append = append;
        this.id = "filesink-" + UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        isRunning = true;

        ZMQ.Socket inSocket = CTX.socket(ZMQ.SUB);
        inSocket.setHWM(0L);
        inSocket.connect(inEndPoint);
        inSocket.subscribe("".getBytes());

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
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(outFile, append)))) {
            while (true) {
                poller.poll();
                if (poller.pollin(0)) {
                    break;
                }
                if (poller.pollin(1)) {
                    if ((msg = inSocket.recvStr()) != null
                            && msg.length() > 0) {
                        out.println(msg);
                        out.flush();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        inSocket.close();
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
