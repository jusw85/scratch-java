package uva;

import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidUtil {

    public static void main(String[] args) {
        UUID origUuid = UUID.randomUUID();
        String base64 = uuidToBase64URLSafe(origUuid);
        UUID newUuid = base64ToUuid(base64);
        assert (origUuid.equals(newUuid));

        System.out.println(origUuid);
        System.out.println(base64);
    }


    public static UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static String uuidToBase64URLSafe(UUID uuid) {
        byte[] bytes = uuidToBytes(uuid);
        return Base64.encodeBase64URLSafeString(bytes);
    }

    public static UUID base64ToUuid(String base64) {
        byte[] bytes = Base64.decodeBase64(base64);
        return bytesToUuid(bytes);
    }

}
