package com.cegedim.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerUtils {

	private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

	/**
	 * Used to log exceptions handled by global transitions on-exception handler.
	 * These exceptions aren't propagated either to
	 * FlowHandlerAdapter.handleException nor to
	 * AbstractFlowHandler.handleException.
	 * 
	 * @param rootCauseException
	 * @param stateException
	 */
	public void logFlowException(Exception rootCauseException, Exception stateException) {
		if (rootCauseException != null) {
			logger.error("logFlowException(): Unexpected flow exception.", rootCauseException);
		} else if (stateException != null) {
			logger.error("logFlowException(): Unexpected flow exception.", stateException);
		} else {
			logger.error(
					"logFlowException(): Unexpected flow exception, but exception detail is missing. This shouldn't happen.");
		}
	}
}
