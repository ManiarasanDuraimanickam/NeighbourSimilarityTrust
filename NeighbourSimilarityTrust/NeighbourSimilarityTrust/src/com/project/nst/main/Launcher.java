package com.project.nst.main;

import java.io.IOException;

import com.project.nst.socket.NSTGroupLeaderServer;

public class Launcher {

	private static NSTGroupLeaderServer nstServer = NSTGroupLeaderServer.getNSTServerObject();

	public static void main(String[] args) throws IOException {
		nstServer.start();
		// ClientConsole clientConsole =
		// new ClientConsole(APPConstans.CONSOLE_WIDTH, APPConstans.CONSOLE_HEIGHT,
		// APPConstans.ClientName, APPConstans.SKU_BLUE, 0,0);
		// clientConsole.showWindow();
	//	APPLogger.LOG(LOGCAT.INFO, "Launched..");
	}

}
