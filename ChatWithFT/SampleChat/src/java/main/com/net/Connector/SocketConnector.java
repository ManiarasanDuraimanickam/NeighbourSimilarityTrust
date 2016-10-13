package com.net.Connector;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;

public interface SocketConnector extends Serializable {


	public void startServer();

	public void SendMessage();

	public void SearchUser() throws IOException;

	public void loadCtsUsers();

	public boolean isCiscoConnected() throws SocketException;
}
