/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import java.util.Date;

public class HelloWorldConsumerThread extends DefaultConsumer {

    private final HelloWorldEndpoint endpoint;
    private Thread thread;

    public HelloWorldConsumerThread(HelloWorldEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Exchange exchange = getEndpoint().createExchange();

                    Date now = new Date();
                    exchange.getIn().setBody("(thread) Hello World! The time is " + now);

                    getProcessor().process(exchange);
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void doStop() throws Exception {
        thread.interrupt();
        super.doStop();
    }

}
