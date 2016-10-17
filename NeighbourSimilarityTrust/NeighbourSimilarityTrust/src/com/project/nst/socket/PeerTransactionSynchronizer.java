package com.project.nst.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.project.nst.gui.ClientConsole;
import com.project.nst.utils.APPConstans;

public class PeerTransactionSynchronizer extends Thread {

	private boolean threadRunner = true;
	private ClientConsole clientConsole;
	private NSTPeerClient peerclient;

	public PeerTransactionSynchronizer(ClientConsole clientConsole, NSTPeerClient peerclient) {
		this.clientConsole = clientConsole;
		this.peerclient = peerclient;
	}

	public void run() {
		while (threadRunner) {
			try {

				sleep(APPConstans.CLIENT_SYNCHRONIZER_WAIT_TIME_L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!threadRunner) {
				break;
			}
			this.updateP2pTransToGL();
		}
	}

	public void updateP2pTransToGL() {
		if (this.clientConsole.isP2pTableUpdated()) {
			this.clientConsole.setP2pTableUpdated(false);
			try {
				this.sendP2pTableDatasToGB();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendP2pTableDatasToGB() throws IOException, ClassNotFoundException {
		List<NSTPeerClient> p2pClientsDetails = new ArrayList<NSTPeerClient>();
		NSTPeerClient p2pDetail;
		synchronized (this.clientConsole.p2pTableModel) {
			DefaultTableModel p2pTableModel = this.clientConsole.p2pTableModel;
			for (int i = 0; i < p2pTableModel.getRowCount(); i++) {
				p2pDetail = new NSTPeerClient();
				p2pDetail.setPeerPosition((String) p2pTableModel.getValueAt(i, APPConstans.SERVER_POSITION_COL));
				p2pDetail.setPeerID(this.clientConsole.getPeerID());
				p2pDetail.setPeerName((String) p2pTableModel.getValueAt(i, APPConstans.SERVER_PEERNAME_COL));
				p2pDetail.setOrderID((String) p2pTableModel.getValueAt(i, APPConstans.SERVER_ORDER_COL));
				p2pDetail.setSelectedOrder((String) p2pTableModel.getValueAt(i, APPConstans.SERVER_ITEM_COL));
				p2pDetail.setOrderStatus((String) p2pTableModel.getValueAt(i, APPConstans.SERVER_STATUS_COL));
				p2pClientsDetails.add(p2pDetail);
			}
		}
		this.peerclient.updateP2pTransactionToGD(p2pClientsDetails, APPConstans.P2P_TRANSACTION_UPDATE_REQ);

	}

	public void stopProcess() {
		this.threadRunner = false;
	}
}
