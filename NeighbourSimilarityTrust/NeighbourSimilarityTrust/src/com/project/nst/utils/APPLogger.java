package com.project.nst.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class APPLogger extends Logger {

	private static Logger logger = Logger.getLogger(APPLogger.class.getName());

	protected APPLogger(String name, String resourceBundleName) {
		super(name, null);
		// TODO Auto-generated constructor stub
	}

	public static enum LOGCAT {
		INFO("I"), ERROR("E"), WARNING("W");

		private final String key;

		LOGCAT(String key) {
			this.key = key;
		}

		public String getType() {
			return this.key;
		}
	}

	public static void LOG(LOGCAT type, String msg) {
		switch (type) {
			case INFO:
				logger.log(Level.INFO, msg);
				break;
			case ERROR:
				logger.log(Level.SEVERE, msg);
				break;
			case WARNING:
				logger.log(Level.WARNING, msg);
				break;
			default:
				logger.log(Level.INFO, msg);
				break;
		}
	}
}
