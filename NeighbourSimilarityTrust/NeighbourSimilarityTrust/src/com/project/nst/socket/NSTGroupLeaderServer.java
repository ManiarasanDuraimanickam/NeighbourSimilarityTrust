package com.project.nst.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.project.nst.Model.PeerDetails;
import com.project.nst.gui.ServerConsole;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPConstans.Algorithm;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class NSTGroupLeaderServer extends Thread {

  private static final NSTGroupLeaderServer NSTSERVER_INSTANCE;
  private ServerConsole console = new ServerConsole(APPConstans.CONSOLE_WIDTH, APPConstans.CONSOLE_HEIGHT,
      APPConstans.ServerName, 0, 0);

  static {
    synchronized (NSTGroupLeaderServer.class) {
      NSTSERVER_INSTANCE = new NSTGroupLeaderServer();
    }
  }

  private ServerSocket serverSocket = null;

  private Socket socket = null;
  private NSTGroupLeaderClient leaderClient = new NSTGroupLeaderClient();

  private NSTGroupLeaderServer() {

    if (NSTSERVER_INSTANCE != null) {
      throw new AssertionError("You can't create object. please use getNSTServerObject() function");
    }
    try {
      APPConstans.LOCAL_IP = Inet4Address.getLocalHost().getHostAddress();
      serverSocket = new ServerSocket(APPConstans.SERVER_DEFAULT_PORT);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static synchronized final NSTGroupLeaderServer getNSTServerObject() {
    return NSTSERVER_INSTANCE;
  }

  public void run() {
    getOrderFromClient();
  }

  private synchronized void getOrderFromClient() {

    console.showWindow();
    this.startSynchronizerThread();
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;
    String requestMsg = null;
    try {
      while (true) {
        socket = serverSocket.accept();
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        requestMsg = (String) objectInputStream.readObject();
        NSTPeerClient nstClient = (NSTPeerClient) objectInputStream.readObject();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.PEER_REGISTRATION)) {
          this.console.updatedTotalPeerCount(null, APPConstans.SERVER_POSITION_COL, nstClient.getPeerPosition());
          peerRegistration(nstClient, objectOutputStream);
          toSendConnectedPeersDetailToAllPeers(nstClient);
          setTransactionUpdatedTBLFlag();
        } else if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.PEER_DE_REGISTRATION)) {
          peerDeRegistration(nstClient);
          toSendConnectedPeersDetailToAllPeers(nstClient);
          objectOutputStream.writeObject(APPConstans.PEER_DE_REGISTRATION_SUS);
        } else if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.ORDER_PLACING)) {
          placingOrderUsingAlgorithm(nstClient, objectOutputStream);
        } else if (APPConstans.VALIDATE_PEER.equalsIgnoreCase(requestMsg)) {
          String peerID = getregisteredPeerByPosition(nstClient);
          if (peerID != null) {
            objectOutputStream.writeObject(APPConstans.VALIDATE_PEER_SUS);
            nstClient.setPeerID(peerID);
            objectOutputStream.writeObject(nstClient);
          } else {
            objectOutputStream.writeObject(APPConstans.VALIDATE_PEER_FLR);
            // nstClient.setPeerID(peerID);
            objectOutputStream.writeObject(nstClient);
          }
        } else if (requestMsg != null && requestMsg.equalsIgnoreCase(APPConstans.P2P_TRANSACTION_UPDATE_REQ)) {
          updatePeerP2pTransaction(objectInputStream, objectOutputStream);
          setTransactionUpdatedTBLFlag();
        }
      }

    } catch (Exception e) {
      APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
    } finally {
      try {
        // objectOutputStream.flush();
        // objectOutputStream.close();
        // objectInputStream.close();
        socket.close();
      } catch (IOException e) {
        APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
      }
    }

  }

  private void placingOrderUsingAlgorithm(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream)
      throws NoSuchAlgorithmException, IOException {
    if (this.console.isSelectedJACCARD) {
      peerOrderPlacingUsingJACCARD(nstClient, objectOutputStream);
    }

    else {
      peerOrderPlacingUsingEUC(nstClient, objectOutputStream);
    }
  }

  private void peerOrderPlacingUsingEUC(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream)
      throws NoSuchAlgorithmException, IOException {
    if (nstClient.getOrderID() == null && !nstClient.isOrderConfirmed()) {
      String orderId = "GD0_ODR_000" + console.serverTableModel.getRowCount();
      nstClient.setOrderID(orderId);
      int requestPeerDistance =
          Math.abs(APPConstans.getPortByPosition(nstClient.getPeerPosition()) - APPConstans.SERVER_DEFAULT_PORT);

      String connetedPeerID = APPConstans.getNameByDistyance(this.console.getPeerDetailList(), requestPeerDistance);
      APPLogger.LOG(LOGCAT.INFO, "Detected distance " + requestPeerDistance
          + " so check the peer details list using distance and detected peer " + connetedPeerID);

      if (connetedPeerID != null && connetedPeerID.equalsIgnoreCase(nstClient.getPeerName())) {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, console.serverTableModel, APPConstans.ORDER_PENDING_STS);
        console.insertRow(orders);
        console.paintServerTable();
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM_REQ_MSG);
      } else {
        Object[] orders = APPConstans.getOrderToArray(nstClient, orderId, console.serverTableModel, APPConstans.ORDER_FAILED_STS);
        console.insertRow(orders);
        console.paintServerTable();
        objectOutputStream.writeObject(APPConstans.ORDER_FAILED_REQ_MSG);
      }
      objectOutputStream.writeObject(nstClient);
    } else if (nstClient.getOrderID() != null && nstClient.isOrderConfirmed()) {
      int requestPeerDistance =
          Math.abs(APPConstans.getPortByPosition(nstClient.getPeerPosition()) - APPConstans.SERVER_DEFAULT_PORT);

      String connetedPeerID = APPConstans.getNameByDistyance(this.console.getPeerDetailList(), requestPeerDistance);
      APPLogger.LOG(LOGCAT.INFO, "Detected distance " + requestPeerDistance
          + " so check the peer details list using distance and detected peer " + connetedPeerID);
      if (connetedPeerID != null && connetedPeerID.equalsIgnoreCase(nstClient.getPeerName())) {
        console.updateRecords(console.serverTableModel, nstClient, APPConstans.SERVER_ORDER_COL, APPConstans.SERVER_STATUS_COL,
            APPConstans.ORDER_CONFIRM_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM);
        objectOutputStream.writeObject(nstClient);
      } else {
        console.updateRecords(console.serverTableModel, nstClient, APPConstans.SERVER_ORDER_COL, APPConstans.SERVER_STATUS_COL,
            APPConstans.ORDER_FAILED_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_FAILURE);
        objectOutputStream.writeObject(nstClient);
      }

    }
    setTransactionUpdatedTBLFlag();
  }

  private void startSynchronizerThread() {
    GLSynchronizer glSynchronizer = new GLSynchronizer(this.console, this.leaderClient);
    glSynchronizer.start();
  }

  @SuppressWarnings("unchecked")
  private void updatePeerP2pTransaction(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream)
      throws ClassNotFoundException, IOException {
    List<NSTPeerClient> p2pTransDetails = (List<NSTPeerClient>) objectInputStream.readObject();
    this.console.insertP2pTransaction(p2pTransDetails);
    console.paintServerTable();
    objectOutputStream.writeObject(APPConstans.P2P_TRANSACTION_UPDATE_SUS);
  }

  private void toSendConnectedPeersDetailToAllPeers(NSTPeerClient nstClient) {
    int curNSTPeerServer = APPConstans.getPortByPosition(nstClient.getPeerPosition());
    for (PeerDetails peerDetail : this.console.getPeerDetailList()) {
      int port = APPConstans.getPortByPosition(peerDetail.getPeerPosition());
      if (port != curNSTPeerServer) {
        leaderClient.updatedAllPeersContacts(port, this.console.getPeerDetailList());
      }
    }
  }

  private void peerDeRegistration(NSTPeerClient nstClient) throws IOException {
    removePeerDetail(nstClient);
  }

  private void removePeerDetail(NSTPeerClient nstClient) {
    int removeIndex = -1;
    synchronized (this.console.getPeerDetailList()) {
      for (int i = 0; i < this.console.getPeerDetailList().size(); i++) {
        if (this.console.getPeerDetailList().get(i).getPeerPosition().equalsIgnoreCase(nstClient.getPeerPosition())) {
          removeIndex = i;
        }
      }
      if (removeIndex >= 0) {
        this.console.getPeerDetailList().remove(removeIndex);
      }
    }
  }

  private void peerRegistration(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream) throws NoSuchAlgorithmException,
      IOException {
    String peerID = APPConstans.getPeerNameHashing(nstClient.getPeerName());
    nstClient.setPeerID(peerID);
    updatePeerDetails(nstClient);
    nstClient.setDetectionAlgorithm(this.console.isSelectedJACCARD ? Algorithm.ALGORITHM.getType() : Algorithm.ALGORITHM2.getType());
    objectOutputStream.writeObject(nstClient);
    objectOutputStream.writeObject(this.console.getPeerDetailList());
  }

  private String getregisteredPeerByPosition(NSTPeerClient nstClient) {
    for (PeerDetails peerDetails : this.console.getPeerDetailList()) {
      if (peerDetails.getPeerPosition().equalsIgnoreCase(nstClient.getPeerPosition())) {
        return peerDetails.getPeerID();
      }
    }
    return null;
  }

  private void updatePeerDetails(NSTPeerClient nstClient) {
    boolean isExsist = false;
    synchronized (this.console.getPeerDetailList()) {
      for (PeerDetails peerDetails : this.console.getPeerDetailList()) {
        if (peerDetails.getPeerPosition().equalsIgnoreCase(nstClient.getPeerPosition())) {
          isExsist = true;
        }
      }
      if (!isExsist) {
        PeerDetails peerDetails = new PeerDetails();
        peerDetails.setPeerID(nstClient.getPeerID());
        peerDetails.setPeerName(nstClient.getPeerName());
        peerDetails.setPeerPosition(nstClient.getPeerPosition());
        peerDetails.setPeerDistance(Math.abs(APPConstans.getPortByPosition(nstClient.getPeerPosition())
            - APPConstans.SERVER_DEFAULT_PORT));
        this.console.getPeerDetailList().add(peerDetails);
      }
    }
  }

  private void peerOrderPlacingUsingJACCARD(NSTPeerClient nstClient, ObjectOutputStream objectOutputStream) throws IOException,
      NoSuchAlgorithmException {
    if (nstClient.getOrderID() == null && !nstClient.isOrderConfirmed()) {
      String orderId = "GD0_ODR_000" + console.serverTableModel.getRowCount();
      nstClient.setOrderID(orderId);
      // this.console.updatedTotalPeerCount(this.console.serverTableModel,
      // APPConstans.SERVER_POSITION_COL, nstClient.getPeerPosition());
      String peerID = getregisteredPeerByPosition(nstClient);
      String connetedPeerID = APPConstans.getPeerNameHashing(nstClient.getPeerName());
      if (peerID != null && connetedPeerID != null && peerID.equalsIgnoreCase(connetedPeerID)) {
        Object[] orders =
            APPConstans.getOrderToArray(nstClient, orderId, console.serverTableModel, APPConstans.ORDER_PENDING_STS);
        console.addOrderEntryInJaccardList(orders[0]);
        console.insertRow(orders);
        console.paintServerTable();
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM_REQ_MSG);
      } else {
        Object[] orders = APPConstans.getOrderToArray(nstClient, orderId, console.serverTableModel, APPConstans.ORDER_FAILED_STS);
        console.addOrderEntryInJaccardList(orders[0]);
        console.insertRow(orders);
        console.paintServerTable();
        objectOutputStream.writeObject(APPConstans.ORDER_FAILED_REQ_MSG);
      }
      objectOutputStream.writeObject(nstClient);
    } else if (nstClient.getOrderID() != null && nstClient.isOrderConfirmed()) {
      String peerID = getregisteredPeerByPosition(nstClient);
      String connetedPeerID = APPConstans.getPeerNameHashing(nstClient.getPeerName());
      if (peerID != null && connetedPeerID != null && peerID.equalsIgnoreCase(connetedPeerID)) {
        console.updateRecords(console.serverTableModel, nstClient, APPConstans.SERVER_ORDER_COL, APPConstans.SERVER_STATUS_COL,
            APPConstans.ORDER_CONFIRM_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_CONFIRM);
        objectOutputStream.writeObject(nstClient);
      } else {
        console.updateRecords(console.serverTableModel, nstClient, APPConstans.SERVER_ORDER_COL, APPConstans.SERVER_STATUS_COL,
            APPConstans.ORDER_FAILED_STS);
        objectOutputStream.writeObject(APPConstans.ORDER_FAILURE);
        objectOutputStream.writeObject(nstClient);
      }

    }
    setTransactionUpdatedTBLFlag();
  }

  private void setTransactionUpdatedTBLFlag() {
    console.setServerTransationTBLUpdated(true);
  }
}
