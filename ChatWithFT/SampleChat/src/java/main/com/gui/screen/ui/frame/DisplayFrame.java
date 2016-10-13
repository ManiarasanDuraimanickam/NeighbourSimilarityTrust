package com.gui.screen.ui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gui.screen.ui.frame.component.FrameUIComponent;
import com.gui.screen.ui.frame.component.FrameUIComponentImpl;
import com.net.Connector.SocketConnector;
import com.net.Connector.SocketConnectorImpl;

/**
 * @author Maniarasan Duraimanickam
 * @Date 12/4/2014 : 3.45pm
 */
public class DisplayFrame {

	static Logger logger = Logger.getAnonymousLogger();
	public static int Width;
	public static int Heigth;
	private FrameUIComponent uiComponent = new FrameUIComponentImpl();


	public DisplayFrame(int width, int height) {
		Width = width;
		Heigth = height;

	}

	public static void main(String[] arg) throws IOException {
		DisplayFrame displayFrame = new DisplayFrame(600, 400);
		JFrame frame = new JFrame();
		displayFrame.setDisplay(frame);
		SocketConnector socketConnector = new SocketConnectorImpl();
		try {
			logger.info("server Started");
			socketConnector.startServer();
			logger.info("Search Neighbours");
			socketConnector.SearchUser();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}

	private void setDisplay(Frame frame) {
		Box boxTextShow = new Box(BoxLayout.X_AXIS);
		Box boxTextEnter = new Box(BoxLayout.X_AXIS);
		Box boxSendBtn = new Box(BoxLayout.X_AXIS);
		Box boxIpAddress = new Box(BoxLayout.X_AXIS);
		Box boxRefereshIpAddress = new Box(BoxLayout.X_AXIS);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(500, 100));

		boxTextShow.setAutoscrolls(true);
		boxTextShow.setPreferredSize(new Dimension(200, 100));
		boxTextShow.setBackground(Color.pink);
		boxTextShow.setVisible(true);
		boxTextShow.add(uiComponent.showMsgLabel());


		boxTextEnter.setAutoscrolls(true);
		boxTextEnter.setPreferredSize(new Dimension(200, 100));
		boxTextEnter.setBackground(Color.pink);
		boxTextEnter.setVisible(true);
		boxTextEnter.add(uiComponent.createTextArea());


		boxIpAddress.add(uiComponent.showNeighboursList());
		boxIpAddress.setPreferredSize(new Dimension(100, 100));
		boxIpAddress.setAutoscrolls(true);

		boxSendBtn.add(uiComponent.sendButton());
		boxSendBtn.setPreferredSize(new Dimension(99, 100));

		boxRefereshIpAddress.add(uiComponent.refershContactList());
		boxRefereshIpAddress.setPreferredSize(new Dimension(103, 100));

		buttonPanel.add(boxSendBtn, BorderLayout.PAGE_START);
		buttonPanel.add(boxRefereshIpAddress, BorderLayout.LINE_END);
		buttonPanel.add(uiComponent.fileChooserBtn());
		buttonPanel.add("File : ", uiComponent.fileNameShowLbl());

		frame.setAlwaysOnTop(false);
		frame.setSize(Width, Heigth);
		frame.setBackground(Color.WHITE);
		frame.setFocusable(true);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Connect Me.....");

		frame.add(boxTextShow, BorderLayout.CENTER);
		frame.add(boxIpAddress, BorderLayout.LINE_START);
		frame.add(boxTextEnter, BorderLayout.LINE_END);
		frame.add(buttonPanel, BorderLayout.PAGE_END);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(Width, Heigth);
		frame.setPreferredSize(new Dimension(Width, Heigth));
		frame.addWindowListener(new FrameEvents());
	}
}
