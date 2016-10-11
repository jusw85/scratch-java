package demo.protobuf;

import com.google.protobuf.ByteString;
import demo.protobuf.generated.AddressBookProtos.Person;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * $ protoc -I=./etc --java_out=./src/main/java ./etc/addressbook.protos
 */
public class ProtobufDemo {

    public static void main(String[] args) throws Exception {
        Person person1 = Person.newBuilder()
                .setId(1234)
                .setName("John Doe")
                .setEmail("jdoe@example.com")
                .addPhones(
                        Person.PhoneNumber.newBuilder()
                                .setNumber("555-4321")
                                .setType(Person.PhoneType.HOME))
                .build();

        Person person2 = Person.newBuilder()
                .setId(5678)
                .setName("Mary Sue")
                .setEmail("msue@example.com")
                .addPhones(
                        Person.PhoneNumber.newBuilder()
                                .setNumber("555-4321")
                                .setType(Person.PhoneType.HOME))
                .addPhones(
                        Person.PhoneNumber.newBuilder()
                                .setNumber("555-1234")
                                .setType(Person.PhoneType.MOBILE))
                .build();

        List<Person> persons = new LinkedList<>();
        persons.add(person1);
        persons.add(person2);

        try (PipedOutputStream pos = new PipedOutputStream();
             PipedInputStream pis = new PipedInputStream(pos);
             DataOutputStream os = new DataOutputStream(pos);
             DataInputStream is = new DataInputStream(pis)) {
            for (Person person : persons) {
                ByteString byteString = person.toByteString();
                int size = byteString.size();
                byte[] bytes = byteString.toByteArray();
                os.writeInt(size);
                os.write(bytes);
            }
            os.writeInt(-1);
            os.flush();

            List<Person> out = new LinkedList<>();
            int size;
            while ((size = is.readInt()) >= 0) {
                byte[] bytes = new byte[size];
                is.read(bytes);
                Person p = Person.parseFrom(bytes);
                out.add(p);
            }

            assert (out.size() == persons.size());
            for (int i = 0; i < out.size(); i++) {
                assert (out.get(i).equals(persons.get(i)));
            }
        }
    }

}
