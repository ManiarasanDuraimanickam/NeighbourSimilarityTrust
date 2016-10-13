package com.net.sockect.Peoples;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.net.Connector.UIConnector;
import com.net.Connector.UIConnectorImpl;
import com.net.sockect.SocketServer;

public class NeighbourListImpl implements NeighbourList {

	Logger logger = Logger.getAnonymousLogger();


	private UIConnector uiConnector = new UIConnectorImpl();

	@Deprecated
	public static Map<String, String> UserList = new TreeMap<String, String>();



	@Override
	public void addNeighbours(String name, String value) {
		 if (!name.equalsIgnoreCase(SocketServer.ServerId)) {
		NeighbourList.UserList.put(name, value);
		uiConnector.updateNeighbourList();
		 }
	}

	@Override
	public void RemoveNeighbours(String removingIp) {
	//	logger.info("removeUser" + removingIp);
		if (NeighbourList.UserList.containsValue(removingIp)) {
			String name = getKeyBasedvalue(removingIp, NeighbourList.UserList);
			if (name != null) {
				NeighbourList.UserList.remove(name);
				uiConnector.updateNeighbourList();
			}
		}
	}

	@Override
	public void searchNeighbours() throws IOException {
		String neighbourIp = null;
		String ip = Inet4Address.getLocalHost().getHostAddress();
		String ipSplit[] = ip.split("\\.");
		String frstThreeDigIp =
				String.valueOf(ipSplit[0]).concat(".").concat(String.valueOf(ipSplit[1])).concat(".").concat(String.valueOf(ipSplit[2])).concat(".");
		for (int i =0; i <= 255; i++) {
			neighbourIp = frstThreeDigIp.concat(String.valueOf(i));
			new NeighbourSearchImpl(neighbourIp, null, true).search();
		}
	}


	@Override
	public void addCtsUsersInList() {
		removeAllNeighbours();
		/** < Maniarasan Duraimanickam> */
		addNeighbours("449432", "10.251.48");
		/** < Madhumitha . > */
		addNeighbours("Madthu", "10.251.48.66");
		/** < Santhanam . > */
		addNeighbours("santhanam", "10.251.57.51");
		/** < Venkat . > */
		addNeighbours("venkat", "10.251.48.94");
		/** < Dinesh . > */
		addNeighbours("Dinesh", "10.251.48.95");
	}

	@Override
	public void removeAllNeighbours() {
		NeighbourList.UserList.clear();
		if (!NeighbourList.UserList.isEmpty()&&NeighbourList.UserList!=null) {
			uiConnector.updateNeighbourList();
		}
	}

	@Override
	public boolean isCiscoVpnConnected() throws SocketException {
		Enumeration<NetworkInterface> enumNetworkInterface = NetworkInterface.getNetworkInterfaces();
		String netInterfaceName = null;
		NetworkInterface networkInterface = null;
		String ciscoWIN_64bit="Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows x64";
		String ciscoWIN_32bit="Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows";
		Enumeration<InetAddress> inetAddress = null;
		while (enumNetworkInterface.hasMoreElements()) {
			networkInterface = enumNetworkInterface.nextElement();
			netInterfaceName = networkInterface.getDisplayName();
			if (ciscoWIN_32bit.contains(netInterfaceName.trim()) || ciscoWIN_64bit.contains(netInterfaceName.trim())) {
				inetAddress = networkInterface.getInetAddresses();
				if (inetAddress.hasMoreElements())
					return true;
			}
		}
		return false;

	}

	private String getKeyBasedvalue(String value, Map<String, String> Users) {
		for (Entry<String, String> userIp : Users.entrySet()) {
			if (userIp.getValue().equals(value))
				return userIp.getKey();
		}
		return null;
	}



	@Deprecated
	public synchronized void AddNewUser(String name, String Value) {
		// if (!name.equalsIgnoreCase(MakeUserList.MyId)) {
		uiConnector.updateNeighbourList();
		// }
	}

	@Deprecated
	public void UpdateUserList() {
		logger.info("UpdateUserList");
	}

	@Deprecated
	public void removeUser() {
		logger.info("removeUser");
	}

	/*
	 * public synchronized void removeUser(String userIp) { logger.info("removeUser" + userIp); if
	 * (UserList.containsValue(userIp)) { String name = getKeyBasedvalue(userIp, UserList); if (name
	 * != null) { UserList.remove(name); new TextTypingArea().addUsersInUserTable(); } } }
	 */
}
