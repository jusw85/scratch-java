package demo.jeromq;

import org.zeromq.ZMQ;

public class PubSubProxy implements Runnable {

    private final ZMQ.Context CTX;

    private String inEndPoint;
    private String outEndPoint;
    private String controlEndPoint;

    public PubSubProxy(ZMQ.Context ctx, String inEndPoint, String outEndPoint,
                       String controlEndPoint) {
        this.CTX = ctx;
        this.inEndPoint = inEndPoint;
        this.outEndPoint = outEndPoint;
        this.controlEndPoint = controlEndPoint;
    }

    @Override
    public void run() {
        ZMQ.Socket xSub = CTX.socket(ZMQ.XSUB);
        xSub.setHWM(0L);
        xSub.bind(inEndPoint);

        ZMQ.Socket xPub = CTX.socket(ZMQ.XPUB);
        xPub.setHWM(0L);
        xPub.bind(outEndPoint);

        ZMQ.Socket req = CTX.socket(ZMQ.REQ);
        req.connect(controlEndPoint);
        req.send("");
        req.recv();
        req.close();

        ZMQ.proxy(xSub, xPub, null);

        xSub.close();
        xPub.close();
    }

}
