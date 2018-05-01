package com.oddlabs.tt.resource;

import java.util.HashMap;
import java.util.Map;

public final strictfp class Resources {

	private static final Map<ResourceDescriptor, Object> LOADED_RESOURCES = new HashMap<ResourceDescriptor, Object>();

	public static Object findResource(ResourceDescriptor resourceDescriptor) {
		Object result = LOADED_RESOURCES.get(resourceDescriptor);
		if (result == null) {
			result = resourceDescriptor.newInstance();
			LOADED_RESOURCES.put(resourceDescriptor, result);
		}
		return result;
	}
}
