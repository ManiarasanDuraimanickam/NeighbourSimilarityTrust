package com.net.sockect.Peoples;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.net.sockect.SocketServer;
import com.net.sockect.Models.DataObject;

public class NeighbourSearchImpl implements NeighbourSearch {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger("NeighbouresSearch");

	private String searchIp = null;

	DataObject dataObject = null;

	private boolean threadRunning = false;

	private Thread searchThread = null;

	private NeighbourList neighbourList = new NeighbourListImpl();

	public NeighbourSearchImpl(String SearchIp, String msg, boolean requestingId) {
		this.searchIp = SearchIp;
		dataObject = new DataObject(msg, requestingId);
	}

	@Override
	public void start() {
		if (!threadRunning) {
			threadRunning = true;
			searchThread = new Thread(this);
			searchThread.start();
		}

	}

	@Override
	public void stop() {
		if (threadRunning)
			threadRunning = false;

	}


	@Override
	public void run() {

		try {
			Socket socket = new Socket();
			socket.setSoTimeout(5000);
			socket.setKeepAlive(false);
			socket.setTcpNoDelay(true);
			socket.connect(new InetSocketAddress(this.searchIp, SocketServer.serverPort));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.writeObject(dataObject);
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			DataObject respDataObject = (DataObject) objectInputStream.readObject();
			if (respDataObject.getServerID() != null) {
				neighbourList.addNeighbours(respDataObject.getServerID(), this.searchIp);
			}
			socket.close();

		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, e.getMessage());
			// removeNeighbours(this.searchIp);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			// removeNeighbours(this.searchIp);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
			// removeNeighbours(this.searchIp);
		}
	}

	@Override
	public void search() {
		start();
	}

	@SuppressWarnings("unused")
	private void removeNeighbours(String Ip) {
		neighbourList.RemoveNeighbours(Ip);
	}


}
