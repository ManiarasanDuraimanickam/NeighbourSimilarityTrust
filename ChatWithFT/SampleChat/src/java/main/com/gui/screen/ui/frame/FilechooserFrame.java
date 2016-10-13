package com.gui.screen.ui.frame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.gui.screen.ui.frame.component.FrameUIComponent;
import com.gui.screen.ui.frame.component.FrameUIComponentImpl;

public class FilechooserFrame {

	public static JFrame frame;
	public static String selectedFile = null;

	public void showFileChooser() {
		if (frame != null) {
			frame.requestFocus();
		} else {
			FrameUIComponent uiComponent = new FrameUIComponentImpl();
			FrameUIComponentImpl.fileChooser = uiComponent.fileChooser();
			frame = new JFrame("Choose the file to send");
			frame.setResizable(false);
			frame.setAlwaysOnTop(false);
			frame.setSize(new Dimension(400, 400));
			frame.setBackground(Color.WHITE);
			frame.setFocusable(true);
			frame.setLocationRelativeTo(null);
			frame.setTitle("Choose the file..");
			frame.add(FrameUIComponentImpl.fileChooser);
			frame.pack();
			frame.setVisible(true);
			frame.addWindowListener(new FilechooserFrameEvents());
		}
	}
}
