package com.gui.screen.ui.frame.component.Events;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.event.ListSelectionListener;

public interface UIComponentEvents {

	public KeyListener textAreaKeyListener();

	public ActionListener sendBtnClickEvent();

	public ActionListener refreshContract();

	public ActionListener fileChooserBtn();

	public ActionListener fileChooserEvent();

	@Deprecated
	public ListSelectionListener getSelectedUser();
}
