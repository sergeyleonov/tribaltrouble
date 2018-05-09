package com.oddlabs.net;

public strictfp interface ARMIEventBroker {

	void handle(Object sender, ARMIEvent event);
}
