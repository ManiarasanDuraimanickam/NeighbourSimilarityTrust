package com.gui.screen.ui.frame.component;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

public interface FrameUIComponent {



	/**
	 * Method createTextArea
	 * 
	 * this provide the textarea with scrollbar
	 * 
	 * @return
	 */
	public JScrollPane createTextArea();

	/**
	 * Method showMsgLabel
	 * 
	 * Its is used to diaplay received message.
	 * 
	 * @return
	 */
	public JScrollPane showMsgLabel();

	/**
	 * Method sendButton
	 * 
	 * this button send the msg to server
	 * 
	 * @return
	 */
	public JButton sendButton();

	/**
	 * Method refershContactList
	 * 
	 * this method refresh the contact List
	 * 
	 * @return
	 */
	public JButton refershContactList();

	/**
	 * Method
	 * 
	 * @return
	 */
	public JScrollPane showNeighboursList();


	public TableModel MapTableModel();

	public JButton fileChooserBtn();

	public JLabel fileNameShowLbl();

	public JFileChooser fileChooser();
}
