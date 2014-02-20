package eu.gloria.gs.services.api.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import eu.gloria.gs.services.core.tasks.ServerTask;
import eu.gloria.gs.services.core.tasks.ServerThread;

public class VerificationTask extends ServerTask {


	private static Logger log;
	
	static {
		log = LoggerFactory.getLogger(VerificationTask.class.getSimpleName());
	}
	
	@Override
	protected ServerThread createServerThread(ApplicationContext context) {

		log.info("Online");
		return (ServerThread) context.getBean("verificationMonitor");
	}

}
