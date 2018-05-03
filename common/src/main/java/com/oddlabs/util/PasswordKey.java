package com.oddlabs.util;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

public final strictfp class PasswordKey {

	public static PrivateKey readPrivateKey(Cipher decryptCipher, String keyFile, String algorithm) throws IOException, ClassNotFoundException, GeneralSecurityException {
		ObjectInputStream is = new ObjectInputStream(PasswordKey.class.getResource("/" + keyFile).openStream());
		SealedObject sealedKey = (SealedObject) is.readObject();
		byte[] encodedRegistrationKey = (byte[]) sealedKey.getObject(decryptCipher);
		return KeyManager.readPrivateKey(encodedRegistrationKey, algorithm);
	}
}
