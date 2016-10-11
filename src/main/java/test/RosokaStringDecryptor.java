package test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;

/**
 * Reverse engineering Rosoka License
 */
public class RosokaStringDecryptor {

    public static byte[] toByteArray(String no) {
        byte[] number = new byte[no.length() / 2];
        for (int i = 0; i < no.length(); i += 2) {
            int j = Integer.parseInt(no.substring(i, i + 2), 16);
            number[(i / 2)] = ((byte) (j & 0xFF));
        }
        return number;
    }

    public static void main(String[] args) {
//        String extraction = StringDecryptor.fileDecryptor("b1a7178892ba5f85",
//                "665496336ecb34c4fdd28af419c88b93e0aa49b30586ad81522587b0b5a825c9d6a8c0443147d578b458c38dcdbc34097684a0fa2ddf7a3c7a406927292d6ef88808e1ecb733d2ccb8ae1aba5c2b7eee02d1f9c883b3fbfc46c65270d73c05d46bcc645cb81fe60d83fb294c9c9f7500cdc978b173f7eebf98f7f6fc72dcd01a37aa3c50805e293f2874bcb9815421437e19f27ec6c77359742ffd6dd7f9726dd7f70fe15537a448a29347b823b5ee9cb2bb003f1a676fd88817d4e5cea621af47197d928510898e6db800005a2b84775c1b0021b1523d70a508a322ce5de2e1f43a9d5faaf00e157b7db0f4f71af626fd75592a0df9d1c3265ff1d74c6223fddcb64751e51f85354bd2c0ee4bed237d737c30fcc1471c03b9fc24417daa3d8a35bff0732bd8b21d",
//                "bananas123");
        String toolkit = RosokaStringDecryptor.fileDecryptor("3e20abf2fa66b06c",
                "665496336ecb34c4fdd28af419c88b93520d46742da3d76c80d855c2c95eeab1a6cf3b679dddedede97bf0e82effc555a0562303cbb36b3177a7c9c1832be637075328fedf2462d92e42e64bff7e808aa47519747c1cfbe85398d37e4b934a677ad6818412102ebcb188ea9834d0d940669b4a7f0265807c4550278eb94f28329b8701727c0e144dc900d4de584415875c89728f4d1a76321991999b01b727cfd4dab38a4663c20373158587e7565792f194616baa6863bd1f36854eaccbaa330d74a8ab7ee812dc94851109bb649220fe7068078f034d8430e13f8d9407b909907299c7a8fed24408a6c86fd52b9d741da87fade11e15323c624e9eba381a48a05212d2539b2c373e7fc227813d7c5b032c4fa03ce913dcc72c790fd33030aa48701d7d40e6eed42b4726f6da23bb94c7987368a0823cb0",
                "bananas123");
//        System.out.println(x);
    }

    public static String fileDecryptor(String iv, String input, String password) {
        String rawkey = null;
        if (password.length() < 8) {
            System.err.println("Password must be at least eight characters long");
        }
        try {
            byte[] desKeyData = password.getBytes();
            DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = keyFactory.generateSecret(desKeySpec);

            IvParameterSpec ivps = new IvParameterSpec(toByteArray(iv));

            Cipher des = Cipher.getInstance("DES/CBC/PKCS5Padding");
            des.init(2, desKey, ivps);

            byte[] nohex = toByteArray(input);
            byte[] output = des.update(nohex, 0, nohex.length);
            rawkey = new String(output);

            byte[] output2 = des.doFinal();
            if (output2 != null) {
                rawkey = rawkey + new String(output2);
            }

            rawkey = rawkey.replaceAll("Oct 21, 2016", "Oct 21, 3016");
            System.out.println(rawkey);

            Cipher des2 = Cipher.getInstance("DES/CBC/PKCS5Padding");
            des2.init(Cipher.ENCRYPT_MODE, desKey, ivps);
            byte[] finalBytes = des2.doFinal(rawkey.getBytes());
            System.out.println(bytesToHex(finalBytes));
//            byte[] nohex2 = toByteArray(rawkey);
//            byte[] nohex2 = rawkey.getBytes();
//            byte[] update = des2.update(nohex2, 0, nohex.length);
//            String rawkey2 = new String(update);
//            System.out.println(rawkey2);
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
        }
        return rawkey;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
