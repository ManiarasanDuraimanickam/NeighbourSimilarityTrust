package com.gui.screen.ui.frame.component.Events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gui.screen.ui.frame.FilechooserFrame;
import com.gui.screen.ui.frame.component.FrameUIComponentImpl;
import com.net.Connector.SocketConnector;
import com.net.Connector.SocketConnectorImpl;

public class UIComponentEventsImpl implements UIComponentEvents {

	Logger logger = Logger.getLogger("UIComponentEvents");
	SocketConnector socketConnector = new SocketConnectorImpl();


	@Override
	public KeyListener textAreaKeyListener() {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {	
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10)
					askUserToSendMsg();
			}
		};
	}

	private void askUserToSendMsg() {
		int alertRes = JOptionPane.showConfirmDialog(null, "Click OK To send ", "Sending Alert", 2);
		if (alertRes == 0) {
			logger.info("Sending Msg to Server");
			socketConnector.SendMessage();
		}
	}

	public ActionListener sendBtnClickEvent() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.info("Sending Msg to Server");
				socketConnector.SendMessage();
			}
		};
	}


	public ActionListener refreshContract() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshList();
			}
		};

	}

	private void refreshList() {
		logger.info("refreshList");
		try {

			socketConnector.SearchUser();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public ActionListener fileChooserBtn() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new FilechooserFrame().showFileChooser();
			}
		};
	}

	@Override
	public ActionListener fileChooserEvent() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ("ApproveSelection".equalsIgnoreCase(arg0.getActionCommand())) {
					FrameUIComponentImpl.fileNameDiaplayLbl.setText(FrameUIComponentImpl.fileChooser.getSelectedFile().getName());
					FilechooserFrame.selectedFile = FrameUIComponentImpl.fileChooser.getSelectedFile().getAbsolutePath();
					closeFileChooser();
				} else if ("CancelSelection".equalsIgnoreCase(arg0.getActionCommand())) {
					closeFileChooser();
				}
			}
		};
	}

	private void closeFileChooser() {
		FilechooserFrame.frame.setVisible(false);
		FilechooserFrame.frame = null;
	}


	@Deprecated
	@Override
	public ListSelectionListener getSelectedUser() {
		return new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// SocketClientImpl.ServerIp =
				// JTextIpAddress.getValueAt(JTextIpAddress.getSelectedRow(), 1).toString();
				// logger.info("Client.ServerIp--" + SocketClientImpl.ServerIp);

			}
		};
	}


}
