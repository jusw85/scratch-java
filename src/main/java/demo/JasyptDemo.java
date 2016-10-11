package demo;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

public class JasyptDemo {

    public static void main(String[] args) {
        // digest password
        String passwordPlain = "mypassword";
        StrongPasswordEncryptor passwordDigester = new StrongPasswordEncryptor();
        String passwordDigest = passwordDigester.encryptPassword(passwordPlain);
        assert (passwordDigester.checkPassword(passwordPlain, passwordDigest));

        // encrypt password
        String secretKey = "mysecretkey";
        BasicTextEncryptor passwordEncryptor = new BasicTextEncryptor();
        passwordEncryptor.setPassword(secretKey);
        String passwordEncrypted = passwordEncryptor.encrypt(passwordPlain);

        // decrypt password
        BasicTextEncryptor passwordDecryptor = new BasicTextEncryptor();
        passwordDecryptor.setPassword(secretKey);
        String passwordDecrypted = passwordDecryptor.decrypt(passwordEncrypted);
        assert (passwordDecrypted.equals(passwordPlain));

        System.out.println("plaintext: " + passwordPlain);
        System.out.println("digest: " + passwordDigest);
        System.out.println("encrypted: " + passwordEncrypted);
    }

}
