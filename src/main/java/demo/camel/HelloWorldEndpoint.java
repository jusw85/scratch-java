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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a HelloWorld endpoint.
 */
public class HelloWorldEndpoint extends DefaultEndpoint {

    private String consumerType;

    public HelloWorldEndpoint(String uri, HelloWorldComponent component) {
        super(uri, component);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        if (consumerType == null || consumerType.equals("polling")) {
            return new HelloWorldConsumerPolling(this, processor);
        } else if (consumerType.equals("thread")) {
            return new HelloWorldConsumerThread(this, processor);
        } else {
            throw new IllegalArgumentException("Unknown consumer type: " + consumerType);
        }
    }

    public Producer createProducer() throws Exception {
//        throw new UnsupportedOperationException("You can't send messages to this producer");
        return new HelloWorldProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

}
