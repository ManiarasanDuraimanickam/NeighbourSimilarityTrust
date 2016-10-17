package com.project.nst.socket;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.project.nst.Model.PeerDetails;
import com.project.nst.gui.ClientConsole;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class NSTPeerServer extends Thread {

  private ServerSocket serverSocket;
  private int port;
  private boolean runNSTPeerServer = true;
  private Socket socket = null;
  private ClientConsole clientConsole;
  public List<PeerDetails> peerDetailList = null;

  public NSTPeerServer(int port, ClientConsole clientConsole) throws IOException {
    this.port = port;
    this.serverSocket = new ServerSocket(this.port);
    this.clientConsole = clientConsole;
  }

  @SuppressWarnings("unchecked")
  public void run() {
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;
    String requestMsg = null;
    try {
      while (runNSTPeerServer) {
        socket = serverSocket.accept();
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        requestMsg = (String) objectInputStream.readObject();
        if (APPConstans.SERVER_UPDATED_CONNECTED_PEER.equalsIgnoreCase(requestMsg)) {
          updatedConnetcedPeerContactList(objectInputStream);
          objectOutputStream.writeObject("peers contact updated successfully");
        } else if (APPConstans.ORDER_PLACING.equalsIgnoreCase(requestMsg)) {
          NSTPeerClient nstClient = (NSTPeerClient) objectInputStream.readObject();
          placingOrderUsingAlgorithm(nstClient, objectOutputStream);
        } else if (APPConstans.VALIDATE_PEER.equalsIgnoreCase(requestMsg)) {
          NSTPeerClient nstClient = (NSTPeerClient) objectInputStream.readObject();
          String peerID = p2pTransacationTableLookup(nstClient);
          if (peerID == null) {
            objectOutputStream.writeObject(APPConstans.VALIDATE_PEER_FLR);
            objectOutputStream.writeObject(nstClient);
          } else {
            objectOutputStream.writeObject(APPConstans.VALIDATE_PEER_SUS);
            nstClient.setPeerID(peerID);
            objectOutputStream.writeObject(nstClient);
          }
        } else if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.SERVER_UPDATE_FAILED_TRANS_REQ)) {
          List<NSTPeerClient> failedTransactions = (List<NSTPeerClient>) objectInputStream.readObject();
          updateP2PTableWithServerFailedTrans(failedTransactions);
        } else if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.SERVER_ALGORITHM_CHANGE_REQUEST)) {
          String detectionAlgorithm = (String) objectInputStream.readObject();
          synchronized (this.clientConsole.nstClient.getDetectionAlgorithm()) {
            this.clientConsole.nstClient.setDetectionAlgorithm(detectionAlgorithm);
          }
        }
      }

    } catch (Exception e) {
      APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
      }
    }
    socket = null;
    serverSocket = null;
    objectInputStream = null;
    objectOutputStream = null;
  }

  private void placingOrderUsingAlgorithm(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream)
      throws NoSuchAlgorithmException, IOException {
    if (this.clientConsole.nstClient.getDetectionAlgorithm().equalsIgnoreCase(APPConstans.Algorithm.ALGORITHM.getType())) {
      peerOrderPlacingUsingJACCARD(nstClient, objectOutputStream);
    }

    else {
      peerOrderPlacingUsingEUC(nstClient, objectOutputStream);
    }
  }

  private void peerOrderPlacingUsingEUC(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream) throws IOException {
    if (nstClient.getOrderID() == null && !nstClient.isOrderConfirmed()) {
      String orderId = this.clientConsole.getPeerPosition() + "_ODR_000" + this.clientConsole.p2pTableModel.getRowCount();
      nstClient.setOrderID(orderId);
      int requestPeerDistance =
          (APPConstans.getPortByPosition(this.clientConsole.getPeerPosition()) - APPConstans.getPortByPosition(nstClient
              .getPeerPosition()));
      String connetedPeerID = APPConstans.getNameByDistyance(this.peerDetailList, requestPeerDistance);
      APPLogger.LOG(LOGCAT.INFO, "Detected distance " + requestPeerDistance
          + " so check the peer details list using distance and detected peer " + connetedPeerID);

      if (connetedPeerID != null && connetedPeerID.equalsIgnoreCase(nstClient.getPeerName())) {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, this.clientConsole.p2pTableModel, APPConstans.ORDER_PENDING_STS);
        this.clientConsole.insertRowInP2PTable(orders, null);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM_REQ_MSG);
      } else {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, this.clientConsole.p2pTableModel, APPConstans.ORDER_FAILED_STS);
        this.clientConsole.insertRowInP2PTable(orders, null);
        objectOutputStream.writeObject(APPConstans.ORDER_FAILED_REQ_MSG);
      }
      objectOutputStream.writeObject(nstClient);
    } else if (nstClient.getOrderID() != null && nstClient.isOrderConfirmed()) {
      int requestPeerDistance =
          (APPConstans.getPortByPosition(this.clientConsole.getPeerPosition()) - APPConstans.getPortByPosition(nstClient
              .getPeerPosition()));
      String connetedPeerID = APPConstans.getNameByDistyance(this.peerDetailList, requestPeerDistance);
      APPLogger.LOG(LOGCAT.INFO, "Detected distance " + requestPeerDistance
          + " so check the peer details list using distance and detected peer " + connetedPeerID);

      if (connetedPeerID != null && connetedPeerID.equalsIgnoreCase(nstClient.getPeerName())) {
        this.clientConsole.updateRecords(this.clientConsole.p2pTableModel, nstClient, APPConstans.SERVER_ORDER_COL,
            APPConstans.SERVER_STATUS_COL, APPConstans.ORDER_CONFIRM_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM);
        objectOutputStream.writeObject(nstClient);
      } else {
        objectOutputStream.writeObject(APPConstans.ORDER_FAILURE);
      }
    }
    this.clientConsole.setP2pTableUpdated(true);
  }

  private void updateP2PTableWithServerFailedTrans(List<NSTPeerClient> failedTransactions) {
    synchronized (this.clientConsole.p2pTableModel) {
      DefaultTableModel p2pTable = this.clientConsole.p2pTableModel;
      boolean isEntryExists = false;
      for (NSTPeerClient peerClient : failedTransactions) {
        isEntryExists = false;
        if (p2pTable.getRowCount() == 0) {
          Object[] orders =
              APPConstans.getOrderToArray(peerClient, peerClient.getOrderID(), p2pTable, peerClient.getOrderStatus());
          this.clientConsole.addOrderEntryInJaccardList(orders[0]);
          this.clientConsole.insertRowInP2PTable(orders, Color.cyan);
        }
        for (int i = 0; i < p2pTable.getRowCount(); i++) {
          if (p2pTable.getValueAt(i, APPConstans.SERVER_ORDER_COL).toString().equalsIgnoreCase(peerClient.getOrderID())) {
            isEntryExists = true;
            if (p2pTable.getValueAt(i, APPConstans.SERVER_STATUS_COL).toString().equalsIgnoreCase(peerClient.getOrderStatus())) {
              break;
            } else {
              p2pTable.setValueAt(peerClient.getOrderStatus(), i, APPConstans.SERVER_STATUS_COL);
            }

          }
        }
        if (!isEntryExists) {
          Object[] orders =
              APPConstans.getOrderToArray(peerClient, peerClient.getOrderID(), p2pTable, peerClient.getOrderStatus());
          this.clientConsole.addOrderEntryInJaccardList(orders[0]);
          this.clientConsole.insertRowInP2PTable(orders, Color.cyan);

        }
      }
    };
  }

  private String p2pTransacationTableLookup(NSTPeerClient nstClient) {
    if (this.peerDetailList == null) {
      return null;
    }
    for (PeerDetails peerDetail : this.peerDetailList) {
      if (nstClient.getPeerPosition().equalsIgnoreCase(peerDetail.getPeerPosition())) {
        return peerDetail.getPeerID();
      }
    }
    return null;
  }

  private void peerOrderPlacingUsingJACCARD(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream) throws IOException,
      NoSuchAlgorithmException {
    if (nstClient.getOrderID() == null && !nstClient.isOrderConfirmed()) {
      String orderId = this.clientConsole.getPeerPosition() + "_ODR_000" + this.clientConsole.p2pTableModel.getRowCount();
      nstClient.setOrderID(orderId);
      String validatedPeerID = validateWithNearestPeer(nstClient);
      String peerID = APPConstans.getPeerNameHashing(nstClient.getPeerName());
      if (peerID != null && validatedPeerID != null && peerID.equalsIgnoreCase(validatedPeerID)) {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, this.clientConsole.p2pTableModel, APPConstans.ORDER_PENDING_STS);
        this.clientConsole.insertRowInP2PTable(orders, null);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM_REQ_MSG);
      } else {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, this.clientConsole.p2pTableModel, APPConstans.ORDER_FAILED_STS);
        this.clientConsole.insertRowInP2PTable(orders, null);
        objectOutputStream.writeObject(APPConstans.ORDER_FAILED_REQ_MSG);
      }
      objectOutputStream.writeObject(nstClient);
    } else if (nstClient.getOrderID() != null && nstClient.isOrderConfirmed()) {
      String validatedPeerID = validateWithNearestPeer(nstClient);
      String peerID = APPConstans.getPeerNameHashing(nstClient.getPeerName());
      if (validatedPeerID != null && peerID != null && peerID.equalsIgnoreCase(validatedPeerID)) {
        this.clientConsole.updateRecords(this.clientConsole.p2pTableModel, nstClient, APPConstans.SERVER_ORDER_COL,
            APPConstans.SERVER_STATUS_COL, APPConstans.ORDER_CONFIRM_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM);
        objectOutputStream.writeObject(nstClient);
      } else {
        objectOutputStream.writeObject(APPConstans.ORDER_FAILURE);
      }
    }
    this.clientConsole.setP2pTableUpdated(true);
  }

  private String validateWithNearestPeer(NSTPeerClient nstClient) {
    nstClient.setPeerName(nstClient.getPeerName());
    nstClient.setPeerPosition(nstClient.getPeerPosition());
    nstClient.setPeerID(nstClient.getPeerID());
    String peerID = null;
    String[] ports = getNearestPeers(nstClient, APPConstans.getPortByPosition(nstClient.getPeerPosition()));
    for (String port : ports) {
      if (port == null) {
        continue;
      } else if (port.equalsIgnoreCase("" + APPConstans.getPortByPosition(nstClient.getPeerPosition()))) {
        continue;
      }
      APPLogger.LOG(LOGCAT.INFO, "For validation connecting to " + APPConstans.getPositionByPort(port));
      peerID = nstClient.validatePeerDetail(Integer.parseInt(port), APPConstans.VALIDATE_PEER);
      if (peerID != null) {
        return peerID;
      }
    }
    return peerID;
  }

  private String[] getNearestPeers(NSTPeerClient nstClient, int selectedPort) {
    int myPort = APPConstans.getPortByPosition(nstClient.getPeerPosition());
    String leftNPeer = null, rightNPeer = null;
    int leftPeerDiff = 0, rightPeerDiff = 0, curPeerDiff = 0, actualDiff = 0;
    if (this.clientConsole.connectedPeersDetails.getRowCount() <= 2) {
      return new String[] {leftNPeer, rightNPeer, String.valueOf(APPConstans.SERVER_DEFAULT_PORT)};
    }
    String cursorPort;
    actualDiff = Math.abs((myPort) - APPConstans.SERVER_DEFAULT_PORT);
    for (int i = 0; i < clientConsole.connectedPeersDetails.getRowCount(); i++) {
      cursorPort = clientConsole.connectedPeersDetails.getValueAt(i, APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL).toString();
      // APPLogger.LOG(LOGCAT.INFO, "" + clientConsole.connectedPeersDetails.getValueAt(i, 0) +
      // "----" + cursorPort);
      if (!cursorPort.equalsIgnoreCase("" + myPort) && !cursorPort.equalsIgnoreCase("" + selectedPort)
          && !cursorPort.equalsIgnoreCase("" + APPConstans.SERVER_DEFAULT_PORT)) {
        if (leftNPeer == null) {
          leftNPeer = cursorPort;
          leftPeerDiff = Math.abs(Integer.parseInt(cursorPort) - (myPort));
          leftPeerDiff = actualDiff - leftPeerDiff;

        } else if (rightNPeer == null) {
          rightNPeer = cursorPort;
          rightPeerDiff = Math.abs(Integer.parseInt(cursorPort) - (myPort));
          rightPeerDiff = actualDiff - rightPeerDiff;
        } else {
          curPeerDiff = Math.abs(Integer.parseInt(cursorPort) - (myPort));
          curPeerDiff = actualDiff - curPeerDiff;
          if (leftPeerDiff < curPeerDiff) {
            if (leftPeerDiff < rightPeerDiff) {
              rightNPeer = leftNPeer;
              rightPeerDiff = leftPeerDiff;
            }
            leftNPeer = cursorPort;
            leftPeerDiff = curPeerDiff;

          } else if (rightPeerDiff < curPeerDiff && !leftNPeer.equalsIgnoreCase(cursorPort)) {
            rightNPeer = cursorPort;
            rightPeerDiff = curPeerDiff;
          }
        }
      }
    }
    return new String[] {leftNPeer, rightNPeer, String.valueOf(APPConstans.SERVER_DEFAULT_PORT)};
  }

  @SuppressWarnings("unchecked")
  private void updatedConnetcedPeerContactList(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
    peerDetailList = new ArrayList<PeerDetails>();
    peerDetailList = (List<PeerDetails>) objectInputStream.readObject();
    List<PeerDetails> newPeerDetailsList = new ArrayList<PeerDetails>();
    int thisClntPort = APPConstans.getPortByPosition(clientConsole.getPeerPosition());
    boolean isRemove = true;
    for (int i = 0; i < clientConsole.connectedPeersDetails.getRowCount(); i++) {
      isRemove = true;
      for (PeerDetails peerDetail : peerDetailList) {
        int port = APPConstans.getPortByPosition(peerDetail.getPeerPosition());
        if (clientConsole.connectedPeersDetails.getValueAt(i, 1).toString()
            .equalsIgnoreCase("" + APPConstans.SERVER_DEFAULT_PORT)
            || clientConsole.connectedPeersDetails.getValueAt(i, 1).toString().equalsIgnoreCase("" + port)) {
          isRemove = false;
        }
      }
      if (isRemove) {
        clientConsole.connectedPeersDetails.removeRow(i);
        clientConsole.connectedPeersDetails.fireTableRowsInserted(clientConsole.connectedPeersDetails.getRowCount(),
            clientConsole.connectedPeersDetails.getRowCount());
      }
    }
    boolean isAddNew = true;
    for (PeerDetails peerDetail : peerDetailList) {
      isAddNew = true;
      int port = APPConstans.getPortByPosition(peerDetail.getPeerPosition());
      for (int j = 0; j < clientConsole.connectedPeersDetails.getRowCount(); j++) {
        if (clientConsole.connectedPeersDetails.getValueAt(j, 1).toString().equalsIgnoreCase("" + port) || thisClntPort == port) {
          isAddNew = false;
          break;
        }
      }
      if (isAddNew) {
        newPeerDetailsList.add(peerDetail);
      }
      peerDetail.setPeerDistance((thisClntPort - APPConstans.getPortByPosition(peerDetail.getPeerPosition())));
    }
    if (newPeerDetailsList.size() > 0) {
      for (PeerDetails details : newPeerDetailsList) {
        int port = APPConstans.getPortByPosition(details.getPeerPosition());
        clientConsole.connectedPeersDetails.addRow(new Object[] {details.getPeerName(), port});
        clientConsole.connectedPeersDetails.fireTableRowsInserted(clientConsole.connectedPeersDetails.getRowCount(),
            clientConsole.connectedPeersDetails.getRowCount());

      }
    }
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public boolean isRunNSTPeerServer() {
    return runNSTPeerServer;
  }

  public void setRunNSTPeerServer(boolean runNSTPeerServer) {
    this.runNSTPeerServer = runNSTPeerServer;
  }
}
