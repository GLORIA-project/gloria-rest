package eu.gloria.gs.services.api.mail;

import org.springframework.context.ApplicationContext;

import eu.gloria.gs.services.core.tasks.ServerTask;
import eu.gloria.gs.services.core.tasks.ServerThread;

public class VerificationTask extends ServerTask {

	@Override
	protected ServerThread createServerThread(ApplicationContext context) {

		System.out.println("Verification task online!");
		return (ServerThread) context.getBean("verificationMonitor");
	}

}
