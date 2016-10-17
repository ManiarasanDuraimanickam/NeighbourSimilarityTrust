package com.project.nst.socket;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.project.nst.Model.PeerDetails;
import com.project.nst.Model.SybilDetection;
import com.project.nst.gui.ServerConsole;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class GLSynchronizer extends Thread {

	private boolean threadRunner = true;
	private ServerConsole serverConsole;
	private NSTGroupLeaderClient leaderClient;
	private int lastdetectedSNO=-1;

	public GLSynchronizer(ServerConsole serverConsole, NSTGroupLeaderClient leaderClient) {
		this.serverConsole = serverConsole;
		this.leaderClient = leaderClient;
	}

	@Override
	public void run() {
	  int loopCount=0;
		while (this.threadRunner) {
			try {
				sleep(APPConstans.GL_SYNCHRONIZER_WAIT_TIME_L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!this.threadRunner) {
				break;
			}
			APPLogger.LOG(LOGCAT.INFO, "Server synchronizer is running");
			if (serverConsole.isServerTransationTBLUpdated()) {
				serverConsole.setServerTransationTBLUpdated(false);
				loopCount=loopCount+1;
				List<NSTPeerClient> failedTransactions = getFailedTransactionDetails(loopCount);
				if (failedTransactions != null) {
					distributeFailedTransactionToAll(failedTransactions);
				}
			}
		}
	}

	private List<NSTPeerClient> getFailedTransactionDetails(int loopCount) {
		List<NSTPeerClient> failedTransactions = new ArrayList<NSTPeerClient>();
		NSTPeerClient p2pDetail;
		String orderStatus;
		int jaccardTotalTrans=0,jaccardTotalSybli=0,jaccardTotalSus=0, tableSNO=0;
		int eucTotalTrans=0,eucTotalSybli=0,eucTotalSus=0;
		synchronized (serverConsole.serverTableModel) {
			DefaultTableModel serverTable = serverConsole.serverTableModel;
			for (int i = 0; i < serverTable.getRowCount(); i++) {
				orderStatus = (String) serverTable.getValueAt(i, APPConstans.SERVER_STATUS_COL);
				tableSNO=(Integer)serverTable.getValueAt(i, 0);
				if(this.lastdetectedSNO<tableSNO){
				  this.lastdetectedSNO=tableSNO;
				  if(this.serverConsole.isEntryExistsInJaccardList(tableSNO)){
				    jaccardTotalTrans=jaccardTotalTrans+1;
				    if(orderStatus!=null && orderStatus.equalsIgnoreCase(APPConstans.ORDER_CONFIRM_STS)){
				      jaccardTotalSus=jaccardTotalSus+1;
				    }
				    else if(orderStatus!=null && orderStatus.equalsIgnoreCase(APPConstans.ORDER_FAILED_STS)){
				      jaccardTotalSybli=jaccardTotalSybli+1;
				    }
				  }
				  else{
				    eucTotalTrans=eucTotalTrans+1;
				    if(orderStatus!=null && !orderStatus.equalsIgnoreCase(APPConstans.ORDER_FAILED_STS)){
				      eucTotalSus=eucTotalSus+1;
                    }
                    else if(orderStatus!=null && orderStatus.equalsIgnoreCase(APPConstans.ORDER_FAILED_STS)){
                      eucTotalSybli=eucTotalSybli+1;
                    }
				  }
				}
				if (orderStatus != null && !orderStatus.equalsIgnoreCase(APPConstans.ORDER_FAILED_STS)) {
					continue;
				}
				p2pDetail = new NSTPeerClient();
				p2pDetail.setPeerPosition((String) serverTable.getValueAt(i, APPConstans.SERVER_POSITION_COL));
				p2pDetail.setPeerName((String) serverTable.getValueAt(i, APPConstans.SERVER_PEERNAME_COL));
				p2pDetail.setOrderID((String) serverTable.getValueAt(i, APPConstans.SERVER_ORDER_COL));
				p2pDetail.setSelectedOrder((String) serverTable.getValueAt(i, APPConstans.SERVER_ITEM_COL));
				p2pDetail.setOrderStatus(orderStatus);
				failedTransactions.add(p2pDetail);
			}
		}
		if(jaccardTotalTrans>0){
		  SybilDetection detection=new SybilDetection();
		  detection.setDetectedTime(((loopCount*APPConstans.GL_SYNCHRONIZER_WAIT_TIME_L)/1000)+"");
		  detection.setTotaltransaction(jaccardTotalTrans);
		  detection.setSuccessTransactions(jaccardTotalSus);
		  detection.setFailedTransactions(jaccardTotalSybli);
		  this.serverConsole.jarccardSybilDetection.add(detection);
		}
		if(eucTotalTrans>0){
		  SybilDetection detection=new SybilDetection();
          detection.setDetectedTime(((loopCount*APPConstans.GL_SYNCHRONIZER_WAIT_TIME_L)/1000)+"");
          detection.setTotaltransaction(eucTotalTrans);
          detection.setSuccessTransactions(eucTotalSus);
          detection.setFailedTransactions(eucTotalSybli);
          this.serverConsole.eucSybilDetatection.add(detection);
		}
		return failedTransactions.size() > 0 ? failedTransactions : null;
	}
/*	private void findSybillNodes(){
	  
      synchronized (serverTableModel) {
        for (int i = 0; i < serverTableModel.getRowCount(); i++) {
          if (serverTableModel.getValueAt(i, APPConstans.SERVER_STATUS_COL).toString()
              .equalsIgnoreCase(APPConstans.ORDER_FAILED_STS)) {
            totalsybilNode = totalsybilNode + 1;
          }
          totalTransactions = totalTransactions + 1;
        }
      }
	}*/

	private void distributeFailedTransactionToAll(List<NSTPeerClient> failedTransactions) {
		List<NSTPeerClient> failedListBasedPosition;
		for (PeerDetails peerDetail : this.serverConsole.getPeerDetailList()) {
			if (peerDetail == null || peerDetail.getPeerPosition() == null) {
				continue;
			}
			failedListBasedPosition = getFailedListExceptCursorPosition(peerDetail.getPeerPosition(), failedTransactions);
			if (failedListBasedPosition != null && !failedListBasedPosition.isEmpty()) {
				this.leaderClient.updateFailedTransationToAll(APPConstans.getPortByPosition(peerDetail.getPeerPosition()), failedListBasedPosition);
			}
		}
	}

	private List<NSTPeerClient> getFailedListExceptCursorPosition(String position, List<NSTPeerClient> failedTransactions) {
		List<NSTPeerClient> failedListBasedPosition = new ArrayList<NSTPeerClient>();
		for (NSTPeerClient peerClient : failedTransactions) {
			if (!peerClient.getPeerPosition().equalsIgnoreCase(position)) {
				failedListBasedPosition.add(peerClient);
			}
		}
		return failedListBasedPosition.size() > 0 ? failedListBasedPosition : null;
	}

	public void stopProcess() {
		this.threadRunner = false;
	}
}
