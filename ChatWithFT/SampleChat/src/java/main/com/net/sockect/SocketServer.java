package com.net.sockect;

import java.io.Serializable;

import com.net.Connector.UIConnector;
import com.net.Connector.UIConnectorImpl;

public interface SocketServer extends Serializable, Runnable {

	String ServerId = System.getProperty("user.name");

	int serverPort = 5217;

	public UIConnector uiConnector = new UIConnectorImpl();

	public void start();

	public void stop();

	void receiveMsg();
}
