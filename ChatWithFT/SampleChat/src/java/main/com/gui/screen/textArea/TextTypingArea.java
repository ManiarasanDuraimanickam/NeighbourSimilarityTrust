package com.gui.screen.textArea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.net.sockect.Peoples.NeighbourListImpl;

/**
 * Class TextTypingArea
 * 
 * Its is a deprecated class so don't be use it.
 * 
 * @author 449432
 * 
 */
@Deprecated
public class TextTypingArea {
	static Logger logger = Logger.getAnonymousLogger();

	@Deprecated
	public static JLabel jLabel;
	@Deprecated
	private static JTextArea textArea;
	@Deprecated
	private static JTable JTextIpAddress;
	@Deprecated
	private static JScrollPane msgShowScroll = null;
	@Deprecated
	private DefaultTableModel defaultTableModel = null;

	@Deprecated
	public JScrollPane createTextArea() {
		textArea = new JTextArea();

		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setForeground(Color.BLACK);
		textArea.setEditable(true);
		textArea.setAutoscrolls(true);
		textArea.setPreferredSize(new Dimension(100, 100));
		textArea.addKeyListener(textAreaKey());
		JScrollPane textScrollPane = new JScrollPane(textArea);
		return textScrollPane;
	}

	@Deprecated
	public JScrollPane showMsgLable() {
		jLabel = new JLabel();
		// jLabel.setText(server.ClientMsg);
		logger.info("set label text--" + jLabel.getText());
		jLabel.setForeground(Color.DARK_GRAY);
		jLabel.setBackground(Color.magenta);
		jLabel.setSize(300, 100);
		msgShowScroll = new JScrollPane(jLabel);
		return msgShowScroll;
	}

	@Deprecated
	public synchronized void showMgs(String Msg) {
		String previousTxt = jLabel.getText();

		String withoutTags = previousTxt.replace("<html>", "").replace("</html>", "");
		String appendNewMsg =
				withoutTags.concat(String.valueOf("<p>")).concat(String.valueOf(getMsgTime()))
						.concat(String.valueOf("&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;")).concat(Msg).concat(String.valueOf("</p>"));
		String AddTags = String.valueOf("<html>").concat(appendNewMsg).concat("</html>");
		jLabel.setText(AddTags);
		jLabel.setAutoscrolls(true);
		msgShowScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				arg0.getAdjustable().setValue(arg0.getAdjustable().getMaximum());

			}
		});
	}

	@Deprecated
	private String getMsgTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return dateFormat.format(calendar.getTime());
	}

	@Deprecated
	public JButton SendButton() {
		JButton jSendButton = new JButton("Send");
		jSendButton.addActionListener(sendBtnAction());
		jSendButton.setPreferredSize(new Dimension(100, 100));
		return jSendButton;
	}

	@Deprecated
	public JButton refershContactList() {
		JButton jRefresh = new JButton("Refresh List");
		jRefresh.setPreferredSize(new Dimension(100, 100));
		jRefresh.addActionListener(refreshContract());
		return jRefresh;
	}

	@Deprecated
	public JScrollPane IpTextField() {
		JTextIpAddress = new JTable(MapTableModel());
		JTextIpAddress.setSize(200, 200);
		JTextIpAddress.setBackground(Color.DARK_GRAY);
		JTextIpAddress.setForeground(Color.pink);
		JTextIpAddress.getSelectionModel().addListSelectionListener(SelectedUserRowId());
		JScrollPane JTableScroll = new JScrollPane(JTextIpAddress);
		return JTableScroll;
	}

	@Deprecated
	private ListSelectionListener SelectedUserRowId() {
		return new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// SocketClientImpl.ServerIp =
				// JTextIpAddress.getValueAt(JTextIpAddress.getSelectedRow(), 1).toString();
				// logger.info("Client.ServerIp--" + SocketClientImpl.ServerIp);

			}
		};
	}

	@Deprecated
	private TableModel MapTableModel() {
		defaultTableModel = new DefaultTableModel(new Object[] {"Name", "UserIp"}, 0);
		for (Map.Entry<?, ?> userlst : NeighbourListImpl.UserList.entrySet()) {
			defaultTableModel.addRow(new Object[] {userlst.getKey(), userlst.getValue()});
		}
		return defaultTableModel;
	}

	@Deprecated
	public ActionListener sendBtnAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				postMsg();
			}
		};

	}

	@Deprecated
	public ActionListener refreshContract() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshList();
			}
		};

	}

	@Deprecated
	private void refreshList() {
		// TODO Auto-generated method stub
		logger.info("refreshList");
		NeighbourListImpl usersearch = new NeighbourListImpl();
		try {
			usersearch.searchNeighbours();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	@Deprecated
	private void postMsg() {
		// SocketClientImpl.MyMsg = textArea.getText();
		textArea.setText(null);
		// if (SocketClientImpl.ServerIp != null && SocketClientImpl.MyMsg != null)
		// new SocketClientImpl().sendMgsToServer();
		// else
		// JOptionPane.showMessageDialog(null, "please Choose sender or Enter Message..");
	}

	public void ShowUserList() {
		if (NeighbourListImpl.UserList != null)
			IpTextField();
	}

	@Deprecated
	public void addUsersInUserTable() {
		JTextIpAddress.setModel(MapTableModel());
		defaultTableModel.fireTableDataChanged();
	}

	@Deprecated
	private KeyListener textAreaKey() {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10)
					showAlterToSend();

			}

			@Deprecated
			private void showAlterToSend() {
				int alertRes = JOptionPane.showConfirmDialog(null, "Sending Alert", "ok", 2);
				if (alertRes == 0) {
					postMsg();
				}
			}
		};
	}
}
