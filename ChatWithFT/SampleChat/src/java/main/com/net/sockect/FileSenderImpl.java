package com.net.sockect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class FileSenderImpl implements FileSender {

	private String ServerIp = null;
	private int port;
	private InputStream inputStream = null;
	private BufferedReader bufferedReader;
	private Thread fileSenderThread = null;
	private boolean threadRunning = false;

	FileSenderImpl(String Filename, InputStream inputStream, String ServerIp) {
		this.inputStream = inputStream;
		this.ServerIp = ServerIp;
		this.port = FileDownloader.FileserverPort;
	}

	FileSenderImpl(String Filename, BufferedReader inputStream, String ServerIp) {
		this.bufferedReader = inputStream;
		this.ServerIp = ServerIp;
		this.port = FileDownloader.FileserverPort;
	}

	@Override
	public void start() {
		if (this.threadRunning)
			return;
		this.fileSenderThread = new Thread(this);
		this.fileSenderThread.start();

	}

	@Override
	public void stop() {
		if (!threadRunning)
			return;
		this.threadRunning = false;

	}

	@Override
	public void run() {
		// while (this.threadRunning) {
		logger.info("send file called and send to server");
		sendFile();
		// }

	}


	public void sendFile() {
		Socket clSocket;
		try {
			logger.info("file send to server");
			clSocket = new Socket(ServerIp, port);
			sendNon_SerializeObject(clSocket);
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	private void sendNon_SerializeObject(Socket clSocket) throws IOException {
		logger.info("file extraxcted as byte and writting in socket ");
		byte[] cbuf = new byte[byteSize];
		DataOutputStream dataOutputStream = new DataOutputStream(clSocket.getOutputStream());
		int len = 0;
		while ((len = inputStream.read(cbuf)) != -1) {
			dataOutputStream.write(cbuf, 0, len);// .write(cbuf, 0, h);
		}
		dataOutputStream.flush();
		inputStream.close();
		dataOutputStream.close();
		clSocket.close();
	}

	
	@SuppressWarnings("unused")
	private void sendNon_SerializeObjectUsingBUFReader(Socket clSocket) throws IOException {
		logger.info("file extraxcted as byte and writting in socket ");
		char[] cbuf = new char[10240];
		DataOutputStream dataOutputStream = new DataOutputStream(clSocket.getOutputStream());
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(dataOutputStream));
		int len = 0;
		while ((len = this.bufferedReader.read(cbuf)) != -1) {
			bufferedWriter.write(cbuf, 0, len);
		}
		this.bufferedReader.close();
		bufferedWriter.flush();
		bufferedWriter.close();
		clSocket.close();
	}
}
