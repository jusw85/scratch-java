package demo.jeromq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*-
 * Pipeline:
 *
 *    FTPSpout(pub)--(sub)ExternalSubscriber                            -(pull)Worker(push)-
 *                 \                                                   /                    \
 * FileSpout(pub)--(xsub)PubSubProxy(xpub)--(sub)SubPushForwarder(push)--(pull)Worker(push)--------(cont)
 *                 /                      \                            \                    /
 * StreamSpout(pub)                        -(sub)ExternalSubscriber     -(pull)Worker(push)-
 *
 *
 *
 *                                          - (sub)ExternalSubscriber
 *                                         /
 *  (cont)------(pull)PullPubForwarder(pub)--(xsub)PubSubProxy(xpub)--(sub)FileSink
 *                                          /                       \
 *                    SupplementaryData(pub)                         -(sub)StreamSink
 */
public class Main {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException {
        final ZMQ.Context ctx = ZMQ.context(0);
        ExecutorService executorService = Executors.newCachedThreadPool();

        String controlId = "control-" + UUID.randomUUID().toString();
        String controlEndPoint = "inproc://" + controlId;
        ZMQ.Socket controlSocket = ctx.socket(ZMQ.REP);
        controlSocket.bind(controlEndPoint);

        Runnable spoutProxy = new PubSubProxy(ctx, "inproc://spout-in", "inproc://spout-out", controlEndPoint);
        Runnable sinkProxy = new PubSubProxy(ctx, "inproc://sink-in", "inproc://sink-out", controlEndPoint);
        executorService.submit(spoutProxy);
        executorService.submit(sinkProxy);
        sync(controlSocket);
        sync(controlSocket);

        SubPushForwarder workerVent = new SubPushForwarder(ctx, "inproc://spout-out", "inproc://worker-in", controlEndPoint);
        PullPubForwarder workerSink = new PullPubForwarder(ctx, "inproc://worker-out", "inproc://sink-in", controlEndPoint);
        executorService.submit(workerVent);
        executorService.submit(workerSink);
        sync(controlSocket);
        sync(controlSocket);

        FileSpout fileSpout = new FileSpout(ctx, new File("etc/jeromq-infile"), "inproc://spout-in");
        FileSink fileSink = new FileSink(ctx, "inproc://sink-out", new File("etc/jeromq-outfile"), controlEndPoint);
        StringAppendWorker appendWorker1 = new StringAppendWorker(ctx, "inproc://worker-in", "inproc://worker-out", controlEndPoint, ";");
        StringAppendWorker appendWorker2 = new StringAppendWorker(ctx, "inproc://worker-in", "inproc://worker-out", controlEndPoint, ";");
        executorService.submit(fileSink);
        executorService.submit(appendWorker1);
        executorService.submit(appendWorker2);
        sync(controlSocket);
        sync(controlSocket);
        sync(controlSocket);

        LOGGER.debug("Spouting file");
        executorService.submit(fileSpout);
        Thread.sleep(1000);

        LOGGER.debug("Stopping pipeline");
        appendWorker1.stop();
        appendWorker2.stop();
        fileSink.stop();
        workerVent.stop();
        workerSink.stop();
        controlSocket.close();
        ctx.close();

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    private static void sync(Socket controlSocket) {
        controlSocket.recv();
        controlSocket.send("");
    }

}
