package com.gui.screen.ui.frame;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.gui.screen.ui.SystemTray.TrayIconPath;
import com.gui.screen.ui.SystemTray.TrayPopUp;
import com.net.sockect.SocketServer;

public class FrameEvents implements WindowListener {
	Logger LOGGER = Logger.getAnonymousLogger();
	public static TrayIcon trayIcon = null;

	@Override
	public void windowActivated(WindowEvent arg0) {
		// LOGGER.info("windowActivated");

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// LOGGER.info("windowClosed");
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// LOGGER.info("windowClosing");
		arg0.getWindow().setVisible(false);
		System.exit(0);

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// LOGGER.info("windowDeactivated");

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// LOGGER.info("windowDeiconified");

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// LOGGER.info("windowIconified");
		Frame frame = (Frame) arg0.getWindow();
		frame.setExtendedState(JFrame.NORMAL);
		frame.requestFocus();
		// int result = JOptionPane.showConfirmDialog(frame, "Can I Minimize to System Tray", "OK",
		// 2);
		// LOGGER.info("result--" + result);
		if (SystemTray.isSupported()) {
			SystemTray systemTray = SystemTray.getSystemTray();
			if (systemTray.getTrayIcons().length <= 0) {
				URL imageUrl = TrayIconPath.class.getResource("trayIcon.png");
				Image imageIcon = new ImageIcon(imageUrl).getImage();
				trayIcon = new TrayIcon(imageIcon, "Connect Me...");
				trayIcon.setPopupMenu(new TrayPopUp().SysteTrayPopup(frame));
				try {
					systemTray.add(trayIcon);
					arg0.getWindow().setVisible(false);
					trayIcon.displayMessage("Hi : ".concat(String.valueOf(SocketServer.ServerId)), "I am  Here..!", MessageType.INFO);
				} catch (AWTException e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, e.getMessage());

				}

			} else {
				arg0.getWindow().setVisible(false);
				trayIcon.displayMessage("Hi : ".concat(String.valueOf(SocketServer.ServerId)), "I am  Here..!", MessageType.INFO);
			}
		}


	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// LOGGER.info("windowOpened");

	}
}
