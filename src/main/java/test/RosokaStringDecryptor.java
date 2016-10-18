package test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;

public class RosokaStringDecryptor {

    public static void main(String[] args) {
        String iv = "3e20abf2fa66b06c";
        String encrypted = "665496336ecb34c4fdd28af419c88b93520d46742da3d76c80d855c2c95eeab1a6cf3b679dddedede97bf0e82effc555a0562303cbb36b3177a7c9c1832be637075328fedf2462d92e42e64bff7e808aa47519747c1cfbe85398d37e4b934a677ad6818412102ebcb188ea9834d0d940669b4a7f0265807c4550278eb94f28329b8701727c0e144dc900d4de584415875c89728f4d1a763247e4c970ecb314e31ed264f161e9b5dab080cd7445da96d275f999b592cce57943cb6d86b87a8c997539f078047650658853ffdfdcfe96a5218d3d297bf69ec6fbccccfcd87efcf5a1e1163c41433d79de4d7ef5d129162b41030d7b7a78cba68aae40b578df6e1c7749b0f15267343465b5275ae47c5e4036274518fdb1f479c9609af714b629e44a87c25125446a52d3744fe39b1fc32bc64c3bf2965551f4";
        String password = "bananas123";

        String decrypted = decrypt(iv, encrypted, password);
        System.out.println(decrypted);
        decrypted.replaceAll("Oct 21, 2016", "Oct 21, 3016");
        String reEncrypted = encrypt(iv, decrypted, password);
        assert (encrypted.equals(reEncrypted));
    }

    public static String decrypt(String iv, String encrypted, String password) {
        String decrypted = null;
        try {
            byte[] desKeyData = password.getBytes();
            DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec ivps = new IvParameterSpec(toByteArray(iv));

            Cipher des = Cipher.getInstance("DES/CBC/PKCS5Padding");
            des.init(Cipher.DECRYPT_MODE, desKey, ivps);

            byte[] nohex = toByteArray(encrypted);
            byte[] output = des.update(nohex, 0, nohex.length);
            decrypted = new String(output);

            byte[] output2 = des.doFinal();
            if (output2 != null) {
                decrypted += new String(output2);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static String encrypt(String iv, String decrypted, String password) {
        String encrypted = null;
        try {
            byte[] desKeyData = password.getBytes();
            DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec ivps = new IvParameterSpec(toByteArray(iv));

            Cipher des = Cipher.getInstance("DES/CBC/PKCS5Padding");
            des.init(Cipher.ENCRYPT_MODE, desKey, ivps);
            byte[] finalBytes = des.doFinal(decrypted.getBytes());
            encrypted = bytesToHex(finalBytes);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static byte[] toByteArray(String no) {
        byte[] number = new byte[no.length() / 2];
        for (int i = 0; i < no.length(); i += 2) {
            int j = Integer.parseInt(no.substring(i, i + 2), 16);
            number[(i / 2)] = ((byte) (j & 0xFF));
        }
        return number;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
