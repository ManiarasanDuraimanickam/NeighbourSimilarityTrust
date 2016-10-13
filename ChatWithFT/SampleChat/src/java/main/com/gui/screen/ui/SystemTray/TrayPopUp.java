package com.gui.screen.ui.SystemTray;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class TrayPopUp {

	public PopupMenu SysteTrayPopup(final Frame frame) {
		PopupMenu popupmenu = new PopupMenu();
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem openItem = new MenuItem("Open");
		popupmenu.add(openItem);
		popupmenu.add(exitItem);


		exitItem.addActionListener(exitListener);
		openItem.addActionListener(openListener(frame));
		return popupmenu;
	}


	private ActionListener exitListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);

		}
	};


	private ActionListener openListener(final Frame frame) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(true);
				frame.setExtendedState(JFrame.NORMAL);
				frame.requestFocus();

			}
		};
	}

}
