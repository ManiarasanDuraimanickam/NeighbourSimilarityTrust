package com.net.sockect;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.net.sockect.Models.DataObject;

public class SocketServerImpl implements SocketServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getAnonymousLogger();

	boolean ServerRunning = false;

	Thread ServerThread = null;

	private ServerSocket serverSocket = null;

	private Socket socket = null;

	public String ClientMsg = null;

	// private Thread fileDowloadThread = null;

	public SocketServerImpl() {
		try {
			serverSocket = new ServerSocket(serverPort);

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public void run() {
		while (ServerRunning) {
			logger.info("Server Running...");
			receiveMsg();
		}

	}

	@Override
	public void start() {
		if (!ServerRunning) {
			ServerRunning = true;
			ServerThread = new Thread(this);
			ServerThread.start();

		}
	}

	@Override
	public void stop() {
		if (ServerRunning)
			ServerRunning = false;
	}


	@Override
	public void receiveMsg() {
		try {
			socket = serverSocket.accept();
			logger.info("read client Msg");
			DataObject SendServerId = new DataObject();
			SendServerId.setServerID(ServerId);
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			DataObject dataObject = (DataObject) objectInputStream.readObject();
			ClientMsg = dataObject.getTextMgs();
			if (ClientMsg != null) {
				uiConnector.UpdateMessages(ClientMsg, dataObject.getMyID());
			}
			if (dataObject.isRequestServerID())
				sendserverID(socket, SendServerId);
			if (dataObject.isFileTransfer())
				startFileDownload(dataObject);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}


	public void sendserverID(Socket socket, DataObject sendServerId) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(sendServerId);
		objectOutputStream.flush();
	}

	private void startFileDownload(DataObject dataObject) throws IOException {
		FileDownloader fileDownloader = new FileDownloaderImpl(dataObject);
		fileDownloader.startDownload();
	}

	@Deprecated
	public void ReceiveMsgFromClient() { // logger.info("reading Client Msg");

		try {
			Socket seSocket = serverSocket.accept();
			java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(seSocket.getOutputStream());
			dataOutputStream.writeUTF("serverId=#?!" + ServerId);
			DataInputStream dataInputStream = new DataInputStream(seSocket.getInputStream());
			if (dataInputStream != null) {
				ClientMsg = dataInputStream.readUTF();
				// new TextTypingArea().showMgs(ClientMsg);
			}

		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}



}
