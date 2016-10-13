package com.net.sockect;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import com.net.sockect.Models.DataObject;

public class FileDownloaderImpl implements FileDownloader {

	DataObject dataObject = null;

	private ServerSocket serverSocket = null;

	private Thread filedownloaderThread = null;
	private boolean threadRun = false;

	FileDownloaderImpl(DataObject dataObject) throws IOException {
		this.dataObject = dataObject;
		serverSocket = new ServerSocket(FileserverPort);
	}

	@Override
	public void startDownload() {
		if (this.threadRun)
			return;
		this.threadRun = true;
		this.filedownloaderThread = new Thread(this);
		this.filedownloaderThread.start();

	}

	@Override
	public void stopDownload() {
		if (!this.threadRun)
			return;
		this.threadRun = false;
	}

	@Override
	public void run() {

		try {
			logger.info("FileDownloader started");
			Socket seSocket = serverSocket.accept();
			receiveFile(seSocket);
		} catch (IOException e1) {
			logger.log(Level.SEVERE, e1.getMessage());
		}
	}

	private void receiveFile(Socket seSocket) throws IOException {
		logger.info("FileDownloader received file");
		String path = null;
		File connectMe = new File("d:/ConnectMe Files/");
		if (!connectMe.exists())
			connectMe.mkdir();
		path = String.valueOf(connectMe.getAbsolutePath() + "\\").concat(String.valueOf(this.dataObject.getFileName()));
		DataInputStream dataInputStream = new DataInputStream(seSocket.getInputStream());
		OutputStream outputStream = new FileOutputStream(path);
		byte[] buf = new byte[byteSize];
		int len;
		while ((len = dataInputStream.read(buf)) != -1) {
			outputStream.write(buf, 0, len);
		}
		dataInputStream.close();
		outputStream.flush();
		outputStream.close();
	}

}
