package com.net.sockect.Peoples;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.TreeMap;

public interface NeighbourList {


	public Map<String, String> UserList = new TreeMap<String, String>();

	public String MyID = System.getProperty("user.name");

	public void addNeighbours(String name, String value);

	public void RemoveNeighbours(String removingIp);

	public void searchNeighbours() throws IOException;

	public void addCtsUsersInList();

	public void removeAllNeighbours();

	public boolean isCiscoVpnConnected() throws SocketException;
}
