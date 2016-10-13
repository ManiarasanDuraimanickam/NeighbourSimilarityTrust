package com.net.Connector;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.gui.screen.ui.frame.FilechooserFrame;
import com.gui.screen.ui.frame.component.FrameUIComponentImpl;
import com.net.sockect.SocketClientImpl;
import com.net.sockect.SocketServer;
import com.net.sockect.SocketServerImpl;
import com.net.sockect.Peoples.NeighbourList;
import com.net.sockect.Peoples.NeighbourListImpl;


public class SocketConnectorImpl implements SocketConnector {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger("SocketConnector");

	private static String refreshedTime = null;

	@Override
	public void startServer() {
		logger.info("Server connected");
		SocketServer socketServer = new SocketServerImpl();
		socketServer.start();
	}

	@Override
	public void SendMessage() {
		logger.info("Client connected to send Msg");
		UIConnector uiConnector = new UIConnectorImpl();
		int selectedUsers[] = FrameUIComponentImpl.JTextIpAddress.getSelectedRows();
		String ServerIp = null;
		String Mgs = FrameUIComponentImpl.typingTextArea.getText().trim();
		String filePath = FilechooserFrame.selectedFile;
		String fileName = FrameUIComponentImpl.fileNameDiaplayLbl.getText().trim();
		if (selectedUsers.length == 0 || Mgs == null || Mgs.isEmpty()) {

			uiConnector.emptyMgsORServerNotSelectedError();
			return;
		}
		clearSelectedFile();
		for (int selecteduser : selectedUsers) {
			ServerIp = selectedUserIp(selecteduser);
			uiConnector.showSendingMsg(Mgs, selectedUserName(selecteduser));
			if ("file not choosed".equals(fileName))
				new SocketClientImpl(ServerIp, Mgs, false, NeighbourList.MyID).postMsg();
			else {
				try {
					if (getFileTransferPermissionOnClientNetork()) {
						InputStream fileDatas = getFileInStream(filePath); 
						if (fileDatas != null)
							new SocketClientImpl(ServerIp, Mgs, false, NeighbourList.MyID, true, fileName, fileDatas).postMsg();
						
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage());
				}

			}
		}

	}

	@Override
	public void SearchUser() throws IOException {
		logger.info("People search connted to search");
		String CurrentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
		if (refreshedTime == null || !refreshedTime.equalsIgnoreCase(CurrentTime)) {
			NeighbourList usersearch = new NeighbourListImpl();
			//if (isCiscoConnected()) {
				usersearch.removeAllNeighbours();
				usersearch.searchNeighbours();
		//	} else {
			//	usersearch.removeAllNeighbours();
			//	loadCtsUsers();
		//	}
			refreshedTime = CurrentTime;
		}


	}

	@Override
	public void loadCtsUsers() {
		NeighbourList usersearch = new NeighbourListImpl();
		usersearch.addCtsUsersInList();

	}


	@Override
	public boolean isCiscoConnected() throws SocketException {
		NeighbourList usersearch = new NeighbourListImpl();
		return usersearch.isCiscoVpnConnected();
	}

	private void clearSelectedFile() {
		FilechooserFrame.selectedFile = null;
		FrameUIComponentImpl.fileNameDiaplayLbl.setText("file not choosed");;
	}

	private InputStream getFileInStream(String filePath) throws IOException {
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
		return inputStream;
	}
	
	@SuppressWarnings("unused")
	private BufferedReader getReaderFileInStream(String filePath) throws IOException {
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		return bufferedReader;
	}

	private String selectedUserIp(int row) {
		return FrameUIComponentImpl.JTextIpAddress.getValueAt(row, 1).toString();
	}

	private String selectedUserName(int row) {
		return FrameUIComponentImpl.JTextIpAddress.getValueAt(row, 0).toString();
	}

	private boolean getFileTransferPermissionOnClientNetork() throws HeadlessException, SocketException {
		if (isCiscoConnected()) {
			int optionResult = JOptionPane.showConfirmDialog(null, "Not Advisable to Transfer Files In client Network\n Click OK to Transfer", "Alert", 2);
			if (optionResult == 0) {
				return true;
			} else {
				clearSelectedFile();
				return false;

			}
		} else
			return true;
	}

}
