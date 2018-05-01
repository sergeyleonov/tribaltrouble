package com.oddlabs.util;

import java.lang.reflect.*;

public final strictfp class WindowsRegistryInterface {

	public final static String queryRegistrationKey(String root, String subkey, String value)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
		Class windowsRegistryClass = Class.forName("org.lwjgl.opengl.WindowsRegistry");
		Field rootKeyEnumField = windowsRegistryClass.getDeclaredField(root);
		rootKeyEnumField.setAccessible(true);
		int rootKeyEnum = rootKeyEnumField.getInt(null);
		Method queryRegistrationKeyMethod = windowsRegistryClass.getDeclaredMethod("queryRegistrationKey", new Class[]{int.class, String.class, String.class});
		queryRegistrationKeyMethod.setAccessible(true);
		Object result = queryRegistrationKeyMethod.invoke(null, new Object[]{new Integer(rootKeyEnum), subkey, value});
		return (String)result;
	}
}
