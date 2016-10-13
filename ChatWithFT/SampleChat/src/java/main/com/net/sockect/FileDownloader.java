package com.net.sockect;

import java.util.logging.Logger;

public interface FileDownloader extends Runnable {

	Logger logger=Logger.getLogger("FileDownloader");
	int FileserverPort = 5218;
	int byteSize=FileSender.byteSize;

	public void startDownload();
	
	public void stopDownload();
}
