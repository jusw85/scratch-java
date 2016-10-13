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
package camel.component;

import com.sis.klaver.util.DateTimeUtil;
import org.json.JSONObject;
import twitter4j.Status;

import java.util.UUID;

public class TestBean {

    private int i = 0;

    public void inc() {
        i += 1;
        System.out.println(i);
    }


    public String twit(Status status) {
        JSONObject obj = new JSONObject();
        obj.put("tweetId", String.valueOf(status.getId()));
        obj.put("tweetCreatedAt", status.getCreatedAt());
        obj.put("tweetText", status.getText());
        obj.put("userName", status.getUser().getName());
        obj.put("userScreename", status.getUser().getScreenName());
        obj.put("userId", String.valueOf(status.getUser().getId()));

        obj.put("eid", UUID.randomUUID());
        obj.put("oid", UUID.randomUUID());

        JSONObject root = new JSONObject();
        root.put("data", obj);
        root.put("source_id", "30");
        root.put("type", "twitter");
        root.put("timestamp", DateTimeUtil.currentDateTimeISO8601());

        return root.toString();
    }
}
