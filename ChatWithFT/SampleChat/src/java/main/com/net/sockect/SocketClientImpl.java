package com.net.sockect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gui.screen.ui.frame.component.FrameUIComponentImpl;
import com.net.sockect.Models.DataObject;

public class SocketClientImpl implements SocketClient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4428909000955993908L;

	/**
	 * 
	 */


	Logger logger = Logger.getAnonymousLogger();

	private DataObject dataObject = null;

	private String ServerIp = null;
	private Thread clientThread = null;
	private boolean threadRunning = false;

	public SocketClientImpl(String ServerIp, String Mgs, boolean requetsServerID) {
		this.ServerIp = ServerIp;
		dataObject = new DataObject(Mgs, requetsServerID);
	}

	public SocketClientImpl(String ServerIp, String Mgs, boolean requetsServerID, String myId) {
		this.ServerIp = ServerIp;
		dataObject = new DataObject(Mgs, requetsServerID, myId);
	}

	public SocketClientImpl(String ServerIp, String Mgs, boolean requetsServerID, String myId, boolean fileTransfer, String fileName,
			BufferedReader fileDatas) {
		this.ServerIp = ServerIp;
		dataObject = new DataObject(Mgs, requetsServerID, myId, fileTransfer, fileName, fileDatas);
	}

	public SocketClientImpl(String ServerIp, String Mgs, boolean requetsServerID, String myId, boolean fileTransfer, String fileName,
			InputStream fileDatas) {
		this.ServerIp = ServerIp;
		dataObject = new DataObject(Mgs, requetsServerID, myId, fileTransfer, fileName, fileDatas);
	}

	public SocketClientImpl(String ServerIp, String Mgs, boolean requetsServerID, String myId, boolean fileTransfer, String fileName, Object fileDatas) {
		this.ServerIp = ServerIp;
		dataObject = new DataObject(Mgs, requetsServerID, myId, fileTransfer, fileName, fileDatas);
	}

	private void start() {
		if (!this.threadRunning) {
			this.threadRunning = true;
			this.clientThread = new Thread(this);
			this.clientThread.start();
		}
	}

	/**
	 * private void stop() { if (this.threadRunning) this.threadRunning = false;
	 * 
	 * }
	 */

	@Override
	public void run() {
		try {
			logger.info("ServerIp---" + ServerIp);
			Socket clSocket = new Socket(ServerIp, SocketServer.serverPort);
			sendSerializeObject(clSocket);
			Thread.sleep(3000);
			sendFile();
		} catch (UnknownHostException e) {
			logger.log(Level.WARNING, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}

	private void sendFile() {
		if (dataObject.isFileTransfer()) {
			logger.info("send file created");
			FileSender fileSender = new FileSenderImpl(dataObject.getFileName(),dataObject.getInStreamFileDatas(), ServerIp);
			fileSender.start();
		}
	}

	@Override
	public void postMsg() {
		start();
		FrameUIComponentImpl.typingTextArea.setText(null);
		FrameUIComponentImpl.typingTextArea.setCaretPosition(0);
	}

	@Deprecated
	public void sendMgsToServer() {
		logger.info("Msg Sending to Server");
		try {
			Socket clSocket = new Socket(ServerIp, SocketServer.serverPort);
			DataOutputStream dataOutputStream = new DataOutputStream(clSocket.getOutputStream());
			// dataOutputStream.writeUTF(Client.MyMsg);
			dataOutputStream.flush();
			dataOutputStream.close();
			clSocket.close();
		} catch (UnknownHostException e) {
			logger.log(Level.WARNING, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		}

	}

	@SuppressWarnings("unused")
	private void sendNon_SerializeObject(Socket clSocket) throws IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(clSocket.getOutputStream());
		// dataOutputStream.writeUTF(Client.MyMsg);
		dataOutputStream.flush();
		dataOutputStream.close();
		clSocket.close();
	}

	private void sendSerializeObject(Socket clSocket) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
		objectOutputStream.writeObject(dataObject);
		objectOutputStream.flush();
		// objectOutputStream.close();
		clSocket.close();
	}

}
