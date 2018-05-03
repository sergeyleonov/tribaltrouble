package com.oddlabs.regkeygen;

import com.oddlabs.registration.RegServiceInterface;
import com.oddlabs.util.KeyManager;
import com.oddlabs.util.PasswordKey;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;

public final strictfp class RegistrationKeygen {

	private final static int KEY_SIZE = 2048;

	private static KeyPair generateKeyPair() throws GeneralSecurityException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(RegServiceInterface.KEY_ALGORITHM);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keygen.initialize(KEY_SIZE, random);
		return keygen.generateKeyPair();
	}

	// Usage: RegistrationKeygen <keys_path> <password> -generate
	public static void main(String[] args) {
		PrivateKey privateKey;
		File privateKeyFile = new File(args[0] + File.separator + RegServiceInterface.PRIVATE_KEY_FILE);
		File publicKeyFile = new File(args[0] + File.separator + RegServiceInterface.PUBLIC_KEY_FILE);
		try {
			Cipher cipher = KeyManager.createPasswordCipherFromPassword(args[1].toCharArray(), Cipher.ENCRYPT_MODE);
			if (args[2].equals("-generate")) {
				System.out.println("Generating a new key pair for registration keys");
				KeyPair keyPair = generateKeyPair();
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
				System.out.println("Writing public key to " + publicKeyFile);
				os.writeObject(keyPair.getPublic().getEncoded());
				privateKey = keyPair.getPrivate();
			} else {
				System.out.println("Changing password for private registration key");
				Cipher decryptCipher = KeyManager.createPasswordCipherFromPassword(args[2].toCharArray(), Cipher.DECRYPT_MODE);
				privateKey = PasswordKey.readPrivateKey(decryptCipher, RegServiceInterface.PRIVATE_KEY_FILE, RegServiceInterface.KEY_ALGORITHM);
			}
			byte[] privateKeyEncoded = privateKey.getEncoded();
			SealedObject sealedKey = new SealedObject(privateKeyEncoded, cipher);
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
			System.out.println("Writing private key to " + privateKeyFile);
			os.writeObject(sealedKey);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
