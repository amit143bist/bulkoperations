package com.docusign.jwt.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;

public class ProservUtilitiesCommandTests {

	private static String PS_PROCESS = "ps_bulkops_process --userIds 5060f41e-7b6b-46f9-afb3-8e6a43b92e3e --integratorKey 46a921cc-704e-4f6b-a1be-4d23f1fae49a --scope signature --tokenExpiryLimit 3600 --privatePemPath C:\\SWSetup\\Sabre\\mydata\\DemoOAuth-private.pem --publicPemPath C:\\SWSetup\\Sabre\\mydata\\DemoOAuth-public.pem --env demo --inDirpath C:\\SWSetup\\Sabre\\mydata\\input --outDirpath C:\\SWSetup\\Sabre\\mydata\\output --operationNames NOTIFICATIONCHANGES --appMaxThreadPoolSize 1 --appCoreThreadPoolSize 1 --appReminderAllowed false --appExpirationAllowed true --validAccountGuids f87f6342-ecfd-4100-96d8-9efda13d298e";

	@Test
	public void psProcessHappyPath() {
		CommandResult cr = executeCommand(PS_PROCESS);
		assertEquals(true, cr.isSuccess());
	}

	private CommandResult executeCommand(String commandType) {
		Bootstrap bootstrap = new Bootstrap();
		JLineShellComponent shell = bootstrap.getJLineShellComponent();
		return shell.executeCommand(commandType);
	}
}