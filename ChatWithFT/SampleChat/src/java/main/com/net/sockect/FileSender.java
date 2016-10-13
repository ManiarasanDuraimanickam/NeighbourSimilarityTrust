package com.net.sockect;

import java.util.logging.Logger;

public interface FileSender extends Runnable{

	Logger logger=Logger.getLogger("FileSender");
	int byteSize=1024000;
	void start();
	void stop();
}
