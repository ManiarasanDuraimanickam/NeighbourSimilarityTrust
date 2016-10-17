package com.project.nst.gui;

import java.util.Random;

import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class BatchRunner extends Thread {

  private ClientConsole clientConsole;
  private String peerName;
  private boolean isThreadRun = true;
  private Random random = new Random();

  public BatchRunner(ClientConsole clientConsole) {
    this.clientConsole = clientConsole;
    peerName = this.clientConsole.nodeNameTxt.getText();
  }

  public void run() {
    int selectedPeer = 0;
    int selectedOrder = 0;
    int totalTrans = 0;

    while (isThreadRun) {
      try {
        sleep(APPConstans.CLIENT_BATCHRUN_WAIT_TIME_L);
      } catch (InterruptedException e) {
        APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
      }
      if (!isThreadRun) {
        return;
      }
      selectedOrder = random.nextInt(APPConstans.Available_Items.size());
      if (selectedOrder == 0) {
        selectedOrder = 1;
      }
      if (totalTrans == 10) {
        this.clientConsole.nodeNameTxt.setText(this.clientConsole.nodeNameTxt.getText().concat("AA"));
      }
      this.clientConsole.selectedOrder.setSelectedIndex(selectedOrder);
      synchronized (this.clientConsole.connectedPeersDetails) {
        selectedPeer = random.nextInt(this.clientConsole.connectedPeersDetails.getRowCount());
        this.clientConsole.connectedPeersDetailsTBL.changeSelection(selectedPeer, selectedPeer, true, false);
      }
      APPLogger.LOG(LOGCAT.INFO, "My pos-" + this.clientConsole.getPeerPosition() + " order " + selectedOrder + "   peer "
          + selectedPeer+"  totTrans "+totalTrans);
      this.clientConsole.orderPlacingFunction(true);
      if (totalTrans == 12) {
        this.clientConsole.nodeNameTxt.setText(peerName);
        totalTrans=0;
      }
      totalTrans=totalTrans+1;
    }
  }

  public void startThread() {
    this.start();
  }

  public void stopThread() {
    this.isThreadRun = false;
  }
}
