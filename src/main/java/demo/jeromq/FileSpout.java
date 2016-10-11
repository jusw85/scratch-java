package demo.jeromq;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;

import java.io.File;
import java.io.IOException;

public class FileSpout implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ZMQ.Context CTX;
    private String outEndPoint;

    private File inFile;

    public FileSpout(ZMQ.Context ctx, File inFile, String outEndPoint) {
        this.CTX = ctx;
        this.outEndPoint = outEndPoint;
        this.inFile = inFile;
    }

    @Override
    public void run() {
        ZMQ.Socket outSocket = CTX.socket(ZMQ.PUB);
        outSocket.setHWM(0L);
        outSocket.connect(outEndPoint);
        try {
            LineIterator it = FileUtils.lineIterator(inFile, "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                outSocket.send(line);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        outSocket.close();
        LOGGER.info("End of file: {}", inFile);
    }

}
