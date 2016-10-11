package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class GsonDemo {
    private static String inJson = "{\"data\":{\"id\":\"1\",\"name\":\"John Doe\"}}";

    public static void main(String[] args) throws Exception {
        testBasicDeserialize();
        testBasicSerialize();
        testBasicSerializeDeserialize();
        testAdapter();
    }

    private static void testBasicDeserialize() {
        System.out.println("Basic Deserialize Test");
        JsonParser parser = new JsonParser();
        JsonObject dataObj = parser.parse(inJson).getAsJsonObject();
        JsonObject userObj = dataObj.getAsJsonObject("data");

        Gson gson = new Gson();
        User user = gson.fromJson(userObj, User.class);
        System.out.println(user);
        System.out.println();
    }

    private static void testBasicSerialize() {
        System.out.println("Basic Serialize Test");
        User user = new User();
        user.setId("1");
        user.setName("John Doe");
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        JsonObject userObj = gson.toJsonTree(user).getAsJsonObject();
        JsonObject dataObj = new JsonObject();
        dataObj.add("data", userObj);
        String dataJson = dataObj.toString();

        System.out.println(userJson);
        System.out.println(dataJson);
        System.out.println();
    }

    private static void testBasicSerializeDeserialize() {
        System.out.println("Basic Serialize/Deserialize Test");
        Gson gson = new Gson();
        Type t = new TypeToken<Container<User>>() {
        }.getType();

        Container container = gson.fromJson(inJson, t);
        String containerJson = gson.toJson(container);

        System.out.println(container);
        System.out.println(containerJson);
        System.out.println();
    }

    private static void testAdapter() {
        System.out.println("Adapter Test");
        String inJson = "{\"data\":{\"user\":\"id=1,name=John Doe\"}}";
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(User.class, new UserTypeAdapter().nullSafe())
                .create();
        Type t = new TypeToken<Container<User>>() {
        }.getType();
        Container container = gson.fromJson(inJson, t);
        String containerJson = gson.toJson(container);

        System.out.println(container);
        System.out.println(containerJson);
        System.out.println();
    }

    private static class UserTypeAdapter extends TypeAdapter<User> {
        @Override
        public void write(JsonWriter out, User user) throws IOException {
            Map<String, String> props = new ObjectMapper().convertValue(user, Map.class);
            props.values().removeIf(Objects::isNull);
            String userStr = Joiner.on(",").withKeyValueSeparator("=").join(props);
            out.beginObject();
            out.name("user").value(userStr);
            out.endObject();
        }

        @Override
        public User read(JsonReader in) throws IOException {
            User user = null;
            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals("user")) {
                    String str = in.nextString();
                    Map<String, String> props = Splitter.on(",").withKeyValueSeparator("=").split(str);
                    user = new ObjectMapper().convertValue(props, User.class);
                }
            }
            in.endObject();
            return user;
        }
    }

    private static class Container<T> {
        @SerializedName("data")
        private T obj;

        public T getObj() {
            return obj;
        }

        public void setObj(T obj) {
            this.obj = obj;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("obj", obj)
                    .toString();
        }
    }

    private static class User {
        private String id;
        private String name;
        private String dob;
        private String description;
        private String meta;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .add("name", name)
                    .add("dob", dob)
                    .add("description", description)
                    .add("meta", meta)
                    .toString();
        }
    }

}
