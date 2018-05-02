package com.oddlabs.util;

import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

public final strictfp class Utils {

	public final static String EMAIL_PATTERN = "(.+@.+\\.[a-z]+)?";
	public final static String STD_OUT = "std.out";
	public final static String STD_ERR = "std.err";
	public final static String EVENT_LOG = "event.log";

	public final static String[] LOG_FILES = {STD_OUT, STD_ERR, EVENT_LOG};

	public static InetAddress getLoopbackAddress() {
		try {
			return tryGetLoopbackAddress();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static InetAddress tryGetLoopbackAddress() throws IOException {
		Enumeration interfaces;
		interfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress best_address = null;
		// todo: [] label?
		out:
		while (interfaces.hasMoreElements()) {
			NetworkInterface network_interface = (NetworkInterface) interfaces.nextElement();
			Enumeration addresses = network_interface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = (InetAddress) addresses.nextElement();
				if (address.isLoopbackAddress()) {
					best_address = address;
					// Prefer ipv4 addresses because of BUG 6230761
					if (!(address instanceof Inet6Address)) {
						break out;
					}
				}
			}
		}
		if (best_address != null) {
			System.out.println("loopback address = " + best_address);
			return best_address;
		}
		throw new IOException("Could not find a loopback address");
	}

	public static Object loadObject(URL url) {
		return loadObject(url, false);
	}

	public static int powerOf2Log2(int n) {
		assert isPowerOf2(n) : n + " is not a power of 2";
		for (int i = 0; i < 31; i++) {
			if ((n & 1) == 1) {
				return i;
			}
			n >>= 1;
		}
		return 0;
	}

	public static boolean isPowerOf2(int n) {
		return n == 0 || (n > 0 && (n & (n - 1)) == 0);
	}

	private static Object loadObject(URL url, boolean zipped) {
		try {
			return tryLoadObject(url, zipped);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static int nextPowerOf2(int n) {
		int x = 1;
		while (x < n) {
			x <<= 1;
		}
		return x;
	}

	public static void flip(byte[] bytes, int width, int height) {
		byte[] line = new byte[width];

		for (int i = 0; i < height / 2; i++) {
			System.arraycopy(bytes, i * width, line, 0, width);
			System.arraycopy(bytes, (height - i - 1) * width, bytes, i * width, width);
			System.arraycopy(line, 0, bytes, (height - i - 1) * width, width);
		}
	}

	public static void flip(ByteBuffer bytes, int width, int height) {
		byte[] line = new byte[width];
		byte[] line2 = new byte[width];

		for (int i = 0; i < height / 2; i++) {
			bytes.position(i * width);
			bytes.get(line);
			bytes.position((height - i - 1) * width);
			bytes.get(line2);
			bytes.position(i * width);
			bytes.put(line2);
			bytes.position((height - i - 1) * width);
			bytes.put(line);
		}
	}

	public static Object tryLoadObject(URL url) throws IOException, ClassNotFoundException {
		return tryLoadObject(url, false);
	}

	private static Object tryLoadObject(URL url, boolean zipped) throws IOException, ClassNotFoundException {
		InputStream input_stream = new BufferedInputStream(url.openStream());
		if (zipped)
			input_stream = new GZIPInputStream(input_stream);
		ObjectInputStream obj_stream;

		obj_stream = new ObjectInputStream(input_stream);
		Object obj = obj_stream.readObject();
		obj_stream.close();
		return obj;
	}

	public static URL makeURL(String location) {
		URL url = Utils.class.getResource(location);
		if (url == null) {
			throw new RuntimeException(location + " not found");
		}
		return url;
	}

	public static String resourceToString(String resourcePath) {
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			inputStream = makeURL(resourcePath).openStream();
			byteArrayOutputStream = new ByteArrayOutputStream(inputStream.available());
			byte[] buffer = new byte[1024];
			int count;
			while (true) {
				count = inputStream.read(buffer);
				if (count < 0) {
					break;
				}
				byteArrayOutputStream.write(buffer, 0, count);
			}
			return new String(byteArrayOutputStream.toByteArray());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
