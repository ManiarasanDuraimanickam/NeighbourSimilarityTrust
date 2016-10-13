package com.gui.screen.ui.frame.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultStyledDocument;

import com.gui.screen.ui.frame.component.Events.UIComponentEvents;
import com.gui.screen.ui.frame.component.Events.UIComponentEventsImpl;
import com.net.Connector.UIConnectorImpl;
import com.net.sockect.Peoples.NeighbourList;

public class FrameUIComponentImpl implements FrameUIComponent {

	Logger logger = Logger.getLogger("FrameUIComponent");

	@Deprecated
	public static JLabel msgDiaplayLbl = new JLabel();

	public static JLabel fileNameDiaplayLbl = null;

	public static DefaultStyledDocument styledDocument = new DefaultStyledDocument();

	public static JTextPane msgDiaplayTxtPane = new JTextPane(styledDocument);

	public static JTextArea typingTextArea = new JTextArea();

	public static DefaultTableModel defaultTableModel = null;

	public static JTable JTextIpAddress = null;

	public static JFileChooser fileChooser = null;
	UIComponentEvents componentEvents = new UIComponentEventsImpl();


	@Override
	public JScrollPane createTextArea() {
		typingTextArea.setBackground(Color.LIGHT_GRAY);
		typingTextArea.setForeground(Color.BLACK);
		typingTextArea.setEditable(true);
		typingTextArea.setAutoscrolls(true);
		typingTextArea.setPreferredSize(new Dimension(100, 100));
		typingTextArea.addKeyListener(componentEvents.textAreaKeyListener());
		JScrollPane textScrollPane = new JScrollPane(typingTextArea);
		return textScrollPane;
	}

	@Override
	public JScrollPane showMsgLabel() {
		msgDiaplayTxtPane.setForeground(Color.DARK_GRAY);
		Font font = new Font("Times New Roman", 12, 16);
		msgDiaplayTxtPane.setFont(font);
		msgDiaplayTxtPane.setEditable(false);
		msgDiaplayTxtPane.setBackground(Color.WHITE);
		msgDiaplayTxtPane.setPreferredSize(new Dimension(300, 300));
		JScrollPane msgShowScroll = new JScrollPane(msgDiaplayTxtPane);
		return msgShowScroll;
	}

	@Override
	public JButton sendButton() {
		JButton jSendButton = new JButton("Send");
		jSendButton.addActionListener(componentEvents.sendBtnClickEvent());
		jSendButton.setPreferredSize(new Dimension(100, 100));
		return jSendButton;
	}

	public JButton refershContactList() {
		JButton jRefresh = new JButton("Refresh List");
		jRefresh.setPreferredSize(new Dimension(100, 100));
		jRefresh.addActionListener(componentEvents.refreshContract());
		return jRefresh;
	}

	public JScrollPane showNeighboursList() {
	//	JTextIpAddress = new JTable(MapTableModel());
		JTextIpAddress = new JTable();
		JTextIpAddress.setSize(200, 200);
		JTextIpAddress.setBackground(Color.DARK_GRAY);
		JTextIpAddress.setForeground(Color.CYAN);
		JTextIpAddress.setModel(MapTableModel());
		JScrollPane JTableScroll = new JScrollPane(JTextIpAddress);
		new UIConnectorImpl().hideIpColumn();
		return JTableScroll;
	}


	@Override
	public TableModel MapTableModel() {
		defaultTableModel = new DefaultTableModel(new Object[] {"Name", "UserIp"}, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		for (Map.Entry<?, ?> userlst : NeighbourList.UserList.entrySet()) {
			defaultTableModel.addRow(new Object[] {userlst.getKey(), userlst.getValue()});
		}
		return defaultTableModel;
	}

	@Override
	public JButton fileChooserBtn() {
		JButton filechooseBtn = new JButton("File Chooser");
		filechooseBtn.setPreferredSize(new Dimension(107, 30));
		filechooseBtn.addActionListener(componentEvents.fileChooserBtn());
		return filechooseBtn;
	}

	@Override
	public JLabel fileNameShowLbl() {
		fileNameDiaplayLbl = new JLabel("file not choosed");
		fileNameDiaplayLbl.setPreferredSize(new Dimension(200, 50));
		fileNameDiaplayLbl.setForeground(Color.BLACK);
		Font font = new Font("Times New Roman", 18, 17);
		fileNameDiaplayLbl.setFont(font);
		return fileNameDiaplayLbl;
	}

	@Override
	public JFileChooser fileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setVisible(true);
		fileChooser.setAutoscrolls(true);
		fileChooser.setRequestFocusEnabled(true);
		fileChooser.setName("Choose the file to send");
		fileChooser.setEnabled(true);
		fileChooser.addActionListener(componentEvents.fileChooserEvent());
		return fileChooser;
	}
	
	
}
