package com.net.Connector;

import java.awt.Color;
import java.awt.TrayIcon.MessageType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.gui.screen.ui.frame.FrameEvents;
import com.gui.screen.ui.frame.component.FrameUIComponent;
import com.gui.screen.ui.frame.component.FrameUIComponentImpl;
import com.net.sockect.Peoples.NeighbourList;

public class UIConnectorImpl implements UIConnector {

	private Logger logger = Logger.getLogger("UIConnector");
	FrameUIComponent uiComponent = new FrameUIComponentImpl();
	private StyleContext sc = new StyleContext();

	@Override
	public void updateNeighbourList() {
		FrameUIComponentImpl.JTextIpAddress.setModel(uiComponent.MapTableModel());
		FrameUIComponentImpl.defaultTableModel.fireTableDataChanged();
		hideIpColumn();
		
	}

	@Override
	public void UpdateMessages(String Msg, String Sender) {
		showRecevingMgs(Msg, Sender);
		showTrayMsg(Msg, Sender);
	}

	@Override
	public void emptyMgsORServerNotSelectedError() {
		JOptionPane.showMessageDialog(null, "Please Choose valid sender or Enter a Message");

	}

	@Override
	public void showTrayMsg(String Msg, String Sender) {
		if (FrameEvents.trayIcon != null)
			FrameEvents.trayIcon.displayMessage(String.valueOf("Msg from :").concat(String.valueOf(Sender)), Msg, MessageType.INFO);
	}

	@Override
	public void showSendingMsg(String Msg, String sender) {
		String AddTags = String.valueOf("\nTo : " + sender).concat(String.valueOf(" : " + getMsgTime())).concat("  ").concat(Msg);
		try {
			Document document = FrameUIComponentImpl.msgDiaplayTxtPane.getDocument();
			int start = document.getLength();
			document.insertString(document.getLength(), AddTags, setReceivingStyle());
			int end = document.getLength();
			FrameUIComponentImpl.styledDocument.setParagraphAttributes(start+1, end, setReceivingStyle(), false);
			FrameUIComponentImpl.msgDiaplayTxtPane.setVerifyInputWhenFocusTarget(true);
			FrameUIComponentImpl.msgDiaplayTxtPane.setCaretPosition(end);
		} catch (BadLocationException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	private void showRecevingMgs(String Msg, String Sender) {
		String AddTags = String.valueOf("\n" + Sender).concat(String.valueOf(" : " + getMsgTime())).concat("  ").concat(Msg);
		try {
			Document document = FrameUIComponentImpl.msgDiaplayTxtPane.getDocument();
			int start = document.getLength();
			document.insertString(document.getLength(), AddTags, setSendingStyle());
			int end = document.getLength();
			FrameUIComponentImpl.styledDocument.setParagraphAttributes(start+1, end, setSendingStyle(), false);
			FrameUIComponentImpl.msgDiaplayTxtPane.setVerifyInputWhenFocusTarget(true);
			FrameUIComponentImpl.msgDiaplayTxtPane.setCaretPosition(end);
		} catch (BadLocationException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}


	private String getMsgTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return dateFormat.format(calendar.getTime());
	}

	private Style setSendingStyle() {
		final Style heading2Style = sc.addStyle("Heading2", null);
		StyleConstants.setAlignment(heading2Style, StyleConstants.ALIGN_LEFT);
		heading2Style.addAttribute(StyleConstants.Foreground, Color.BLUE);
		heading2Style.addAttribute(StyleConstants.FontSize, new Integer(16));
		heading2Style.addAttribute(StyleConstants.FontFamily, "Times New Roman");
		heading2Style.addAttribute(StyleConstants.Bold, new Boolean(false));
		return heading2Style;
	}

	private Style setReceivingStyle() {
		final Style main = sc.addStyle("main", null);
		StyleConstants.setAlignment(main, StyleConstants.ALIGN_RIGHT);
		main.addAttribute(StyleConstants.Foreground, Color.BLACK);
		main.addAttribute(StyleConstants.FontSize, new Integer(16));
		main.addAttribute(StyleConstants.FontFamily, "Times New Roman");
		main.addAttribute(StyleConstants.Bold, new Boolean(false));
		return main;
	}

	@Override
	public void hideIpColumn() {
		if(!NeighbourList.UserList.isEmpty())
		{FrameUIComponentImpl.JTextIpAddress.getColumnModel().getColumn(1).setWidth(0);
		FrameUIComponentImpl.JTextIpAddress.getColumnModel().getColumn(1).setMaxWidth(0);
		FrameUIComponentImpl.JTextIpAddress.getColumnModel().getColumn(1).setMinWidth(0);
		FrameUIComponentImpl.JTextIpAddress.getColumnModel().getColumn(1).setResizable(false);
		FrameUIComponentImpl.JTextIpAddress.getColumnModel().getColumn(1).setPreferredWidth(0);
		}
	}


}
