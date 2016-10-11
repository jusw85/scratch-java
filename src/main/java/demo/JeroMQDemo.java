package demo;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class JeroMQDemo {

    public static void main(String[] args) throws InterruptedException {
//        testReqRep();
//        testPushPull();
//        testBasicPubSub();
//        testDynamicPubSub();
//        testDynamicPubSubSynchronised();
    }

    private static void testReqRep() throws InterruptedException {
        ZContext ctx = new ZContext(0);
        Thread server = new Thread(() -> {
            ZMQ.Socket rep = ctx.createSocket(ZMQ.REP);
            rep.bind("inproc://channel");
            System.out.println("Server waiting for requests...");
            String msg;
            while (!(msg = rep.recvStr()).equals("")) {
                System.out.println("  Server received " + msg);
                msg = "World!";
                System.out.println("Server sending " + msg);
                rep.send(msg);
            }
            rep.send("");
            System.out.println("Server stopping");
        });
        Thread client = new Thread(() -> {
            ZMQ.Socket req = ctx.createSocket(ZMQ.REQ);
            req.connect("inproc://channel");
            String msg = "Hello!";
            System.out.println("Client sending " + msg);
            req.send(msg);
            msg = req.recvStr();
            System.out.println("  Client received " + msg);
            System.out.println("Client stopping");
        });

        server.start();
        Thread.sleep(1000); // wait for server to bind
        client.start();
        client.join();

//        send sentinel value
        ZMQ.Socket req = ctx.createSocket(ZMQ.REQ);
        req.connect("inproc://channel");
        req.send("");
        req.recvStr();

        ctx.close();
    }

    public static void testPushPull() throws InterruptedException {
        ZContext ctx = new ZContext(0);
        final int numJobs = 10;

        Thread ventilator = new Thread(() -> {
            ZMQ.Socket push = ctx.createSocket(ZMQ.PUSH);
//            push.bind("tcp://*:50000");
            push.bind("inproc://channel");
            System.out.println("Press Enter to start sending jobs after workers are ready...");
            try {
                System.in.read();
            } catch (IOException e) {
            }
            System.out.println("Pushing " + numJobs + " jobs");
            for (int i = 0; i < numJobs; i++) {
                push.send(".");
            }
            System.out.println("Press Enter to stop processes...");
            try {
                System.in.read();
            } catch (IOException e) {
            }
            for (int i = 0; i < 3; i++) {
                push.send("");
            }
            System.out.println("Ventilator stopped");
        });
        ventilator.start();
        Thread.sleep(1000); // wait for ventilator to bind

        Runnable worker = () -> {
            ZMQ.Socket pull = ctx.createSocket(ZMQ.PULL);
//            pull.connect("tcp://127.0.0.1:50000");
            pull.connect("inproc://channel");
            System.out.println("Worker waiting for jobs...");
            int msgsReceived = 0;
            while (pull.recvStr().length() > 0) {
                System.out.println("  Job Received");
                msgsReceived++;
            }
            System.out.println("Worker stopped");
            System.out.println("Jobs received: " + msgsReceived);
        };

        Thread worker1 = new Thread(worker);
        worker1.start();
        Thread worker2 = new Thread(worker);
        worker2.start();
        Thread worker3 = new Thread(worker);
        worker3.start();

        worker1.join();
        worker2.join();
        worker3.join();
        ctx.close();
    }

    /**
     * Single threaded; no waiting necessary
     */
    private static void testBasicPubSub() {
        ZContext ctx = new ZContext(0);

        ZMQ.Socket pub = ctx.createSocket(ZMQ.PUB);
        pub.bind("inproc://channel");

        ZMQ.Socket sub1 = ctx.createSocket(ZMQ.SUB);
        sub1.connect("inproc://channel");
        sub1.subscribe("".getBytes());

        ZMQ.Socket sub2 = ctx.createSocket(ZMQ.SUB);
        sub2.connect("inproc://channel");
        sub2.subscribe("".getBytes());

        pub.send("Hello World!");
        System.out.println(sub1.recvStr());
        System.out.println(sub2.recvStr());

        ctx.close();
    }

    /**
     * Publishers and subscribers can come and go at any time
     * Naive synchronisation using sleep
     */
    private static void testDynamicPubSub() throws InterruptedException {
        final ZContext ctx = new ZContext(0);

        Thread proxy = new Thread(() -> {
            System.out.println("Starting proxy");
            ZMQ.Socket proxySub = ctx.createSocket(ZMQ.XSUB);
            proxySub.bind("inproc://publisher-endpoint");

            ZMQ.Socket proxyPub = ctx.createSocket(ZMQ.XPUB);
            proxyPub.bind("inproc://subscriber-endpoint");

            ZMQ.proxy(proxySub, proxyPub, null); // blocks until context closed
            System.out.println("Stopping proxy");
        });

        Thread publisher = new Thread(() -> {
            System.out.println("Starting publisher");
            ZMQ.Socket pub = ctx.createSocket(ZMQ.PUB);
            pub.connect("inproc://publisher-endpoint");
            try {
                Thread.sleep(500); // wait for publisher to connect before sending
            } catch (InterruptedException e) {
            }
            for (int i = 0; i < 1000; i++) {
                pub.send(String.valueOf(i));
            }
            System.out.println("Stopping publisher");
        });

        Thread subscriber = new Thread(() -> {
            System.out.println("Starting subscriber");
            ZMQ.Socket sub = ctx.createSocket(ZMQ.SUB);
            sub.connect("inproc://subscriber-endpoint");
            sub.subscribe("".getBytes());

            while (true) {
                String msg = sub.recvStr();
                System.out.println(msg);
                if (msg.equals("999")) {
                    break;
                }
            }
            System.out.println("Stopping subscriber");
        });

        proxy.start();
//        wait for proxy to bind (for inproc only)
//        inproc is a connected transport and has to bind before any connect
//        compared to e.g. tcp which is a disconnected transport - bind("tcp://*:5562")
        Thread.sleep(1000);

        subscriber.start();
        Thread.sleep(1000); // wait for subscriber to connect before starting publisher

        publisher.start();
        Thread.sleep(2000); // wait for publisher to finish spouting

        ctx.close();
    }

    /**
     * Publishers and subscribers can come and go at any time
     * Synchronisation using REQ/REP
     * Synchronisation is between pub-proxy, and proxy-sub; pub-sub is not synchronised
     */
    private static void testDynamicPubSubSynchronised() throws InterruptedException {
        final ZContext ctx = new ZContext(0);

        Thread proxy = new Thread(() -> {
            System.out.println("Starting proxy");
            ZMQ.Socket proxySub = ctx.createSocket(ZMQ.XSUB);
            proxySub.bind("inproc://publisher-endpoint");

            ZMQ.Socket proxyPub = ctx.createSocket(ZMQ.XPUB);
            proxyPub.bind("inproc://subscriber-endpoint");

            new Thread(() -> {
                ZMQ.proxy(proxySub, proxyPub, null); // blocks until context closed
            }).start();

            ZMQ.Socket rep = ctx.createSocket(ZMQ.REP);
            rep.bind("inproc://proxy-sync");
            String msg;
            do {
                msg = rep.recvStr();
                rep.send("");
            } while (!msg.equals(""));
            System.out.println("Stopping proxy");
        });

        Thread publisher = new Thread(() -> {
            System.out.println("Starting publisher");
            ZMQ.Socket pub = ctx.createSocket(ZMQ.PUB);
            pub.connect("inproc://publisher-endpoint");

            ZMQ.Socket req = ctx.createSocket(ZMQ.REQ);
            req.connect("inproc://proxy-sync");
            req.send(".");
            req.recvStr();
//            pub is definitely connected to proxy at this point

            for (int i = 0; i < 1000; i++) {
                pub.send(String.valueOf(i));
            }
            System.out.println("Stopping publisher");
        });

        Thread subscriber = new Thread(() -> {
            System.out.println("Starting subscriber");
            ZMQ.Socket sub = ctx.createSocket(ZMQ.SUB);
            sub.connect("inproc://subscriber-endpoint");
            sub.subscribe("".getBytes());

            ZMQ.Socket req = ctx.createSocket(ZMQ.REQ);
            req.connect("inproc://proxy-sync");
            req.send(".");
            req.recvStr();
//            sub is definitely connected to proxy at this point

            while (true) {
                String msg = sub.recvStr();
                System.out.println(msg);
                if (msg.equals("999")) {
                    break;
                }
            }
            System.out.println("Stopping subscriber");
        });

        proxy.start();
//        wait for proxy to bind (for inproc only)
//        inproc is a connected transport and has to bind before any connect
//        compared to e.g. tcp which is a disconnected transport - bind("tcp://*:5562")
        Thread.sleep(1000);

        subscriber.start();
        Thread.sleep(1000); // wait for subscriber to connect before starting publisher

        publisher.start();
        Thread.sleep(2000); // wait for publisher to finish spouting

        ZMQ.Socket req = ctx.createSocket(ZMQ.REQ);
        req.connect("inproc://proxy-sync");
        req.send("");
        req.recvStr();

        ctx.close();
    }

}
