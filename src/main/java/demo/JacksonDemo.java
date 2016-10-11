package demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.MoreObjects;

import java.io.IOException;

public class JacksonDemo {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.addMixIn(User.class, UserMixin.class);

        ObjectReader reader = mapper.readerFor(User.class);
        ObjectWriter writer = mapper.writer();

        String inJson = "{\"data\":{\"name\":\"John Doe\"}}";
        User user = reader.readValue(inJson);
        user.setId("1234");
        String outJsonUser = writer.writeValueAsString(user);
        System.out.println(inJson);
        System.out.println(user);
        System.out.println(outJsonUser);

        ObjectMapper mapper2 = new ObjectMapper();
        Container<User> container = mapper2.readValue(outJsonUser, new TypeReference<Container<User>>() {
        });
        String outJsonContainer = mapper2.writeValueAsString(container);
        System.out.println(container);
        System.out.println(outJsonContainer);
    }

    private static class Container<T> {
        private T obj;

        @JsonProperty("data")
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

    @JsonRootName("data")
    private static abstract class UserMixin {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String description;
        @JsonIgnore
        private String meta;
    }

}
