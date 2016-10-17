package com.project.nst.socket;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JOptionPane;

import com.project.nst.Model.PeerDetails;
import com.project.nst.gui.ClientConsole;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class NSTPeerClient implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -8559003307432919601L;
  private String peerName;
  private String peerPosition;
  private String selectedOrder;
  private boolean orderConfirmed;
  private String orderID;
  private String peerID;
  private String orderStatus;
  private String detectionAlgorithm;
  private transient final ClientConsole clientConsole;
  public String ORDER_CONFIRM_CLIENT_POPUP_MSG = "Please Confirm your Order.Your order is '{0}'";

  public NSTPeerClient() {
    this(null);
  }

  public NSTPeerClient(ClientConsole clientConsole) {
    this.clientConsole = clientConsole;
  }

  public synchronized void placeOrder(int port, String requestMsg, boolean isBatch) {
    ObjectOutputStream objectOutputStream = null;
    ObjectInputStream objectInputStream = null;
    Socket clSocket = null;
    try {
      String message;
      clSocket = new Socket(APPConstans.LOCAL_IP, port);
      objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
      objectOutputStream.writeObject(requestMsg);
      objectOutputStream.writeObject(this);
      objectInputStream = new ObjectInputStream(clSocket.getInputStream());
      message = (String) objectInputStream.readObject();
      NSTPeerClient client = (NSTPeerClient) objectInputStream.readObject();
      String requestStsMsg = null;
      clSocket.close();
      objectOutputStream.close();
      objectInputStream.close();
      if (message.equalsIgnoreCase(APPConstans.ORDER_CONFIRM_REQ_MSG)) {
        this.clientConsole.insertOrderRecored(client, APPConstans.ORDER_PENDING_STS);
        Component component = this.clientConsole.getComponent();
        String formatedMessag = MessageFormat.format(APPConstans.ORDER_CONFIRM_CLIENT_POPUP_MSG, client.getSelectedOrder());
        int selected = 0;
        if (!isBatch) {
          selected = JOptionPane.showConfirmDialog(component, formatedMessag, "Order Confirmation", 2);
        }

        if (selected == 0) {
          client.setOrderConfirmed(true);
          clSocket = new Socket(APPConstans.LOCAL_IP, port);
          objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
          objectOutputStream.writeObject(requestMsg);
          objectOutputStream.writeObject(client);
          objectInputStream = new ObjectInputStream(clSocket.getInputStream());
          requestStsMsg = ((String) objectInputStream.readObject());
          if (requestStsMsg.equalsIgnoreCase(APPConstans.ORDER_CONFIRM)) {
            client = (NSTPeerClient) objectInputStream.readObject();
            this.clientConsole.updateRecords(this.clientConsole.serverTableModel, client, APPConstans.CLIENT_ORDER_COL,
                APPConstans.CLIENT_STATUS_COL, APPConstans.ORDER_CONFIRM_STS);
          } else if (requestStsMsg.equalsIgnoreCase(APPConstans.ORDER_FAILURE)) {
            client = (NSTPeerClient) objectInputStream.readObject();
            this.clientConsole.updateRecords(this.clientConsole.serverTableModel, client, APPConstans.CLIENT_ORDER_COL,
                APPConstans.CLIENT_STATUS_COL, APPConstans.ORDER_FAILED_STS);
          }
          clSocket.close();
          objectOutputStream.close();
          objectInputStream.close();
        }
      } else if (message.equalsIgnoreCase(APPConstans.ORDER_FAILED_REQ_MSG)) {
        this.clientConsole.insertOrderRecored(client, APPConstans.ORDER_FAILED_STS);
        if (!isBatch) {
          JOptionPane.showMessageDialog(this.clientConsole.getComponent(),
              MessageFormat.format(APPConstans.CLIENT_ITEM_FAILED_MSG_POPUP, client.getSelectedOrder(), client.getOrderID()));
        }
      }
    } catch (Exception e) {
      APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
    }
    clSocket = null;
    objectInputStream = null;
    objectOutputStream = null;
  }

  @SuppressWarnings("unchecked")
  public Object[] peerRegisterAndDeRegister(String peerRegistration) throws UnknownHostException, IOException,
      ClassNotFoundException {
    Object[] response = new Object[3];
    Socket clSocket = new Socket(APPConstans.LOCAL_IP, APPConstans.SERVER_DEFAULT_PORT);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
    ObjectInputStream objectInputStream = null;
    objectOutputStream.writeObject(peerRegistration);
    objectOutputStream.writeObject(this);
    if (peerRegistration.equalsIgnoreCase(APPConstans.PEER_REGISTRATION)) {
      objectInputStream = new ObjectInputStream(clSocket.getInputStream());
      NSTPeerClient nstClientResp = (NSTPeerClient) objectInputStream.readObject();
      List<PeerDetails> peerDetailsList = (List<PeerDetails>) objectInputStream.readObject();
      response[0] = APPConstans.PEER_REGISTRATION_SUS;
      response[1] = nstClientResp;
      response[2] = peerDetailsList;

    } else if (peerRegistration.equalsIgnoreCase(APPConstans.PEER_DE_REGISTRATION)) {
      objectOutputStream.writeObject(APPConstans.PEER_DE_REGISTRATION);
      objectInputStream = new ObjectInputStream(clSocket.getInputStream());
      response[0] = APPConstans.PEER_DE_REGISTRATION_SUS;
    }
    clSocket.close();
    objectOutputStream.close();
    objectInputStream.close();
    return response;
  }

  public synchronized String validatePeerDetail(int port, String requestMsg) {
    ObjectOutputStream objectOutputStream = null;
    ObjectInputStream objectInputStream = null;
    Socket clSocket = null;
    String peerID = null;
    try {
      String responseMsg;
      clSocket = new Socket(APPConstans.LOCAL_IP, port);
      objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
      objectOutputStream.writeObject(requestMsg);
      objectOutputStream.writeObject(this);
      objectInputStream = new ObjectInputStream(clSocket.getInputStream());
      responseMsg = (String) objectInputStream.readObject();
      if (responseMsg.equalsIgnoreCase(APPConstans.VALIDATE_PEER_SUS)) {
        NSTPeerClient client = (NSTPeerClient) objectInputStream.readObject();
        peerID = client.getPeerID();
      } else if (responseMsg.equalsIgnoreCase(APPConstans.VALIDATE_PEER_FLR)) {
        peerID = null;
      }
      clSocket.close();
      objectOutputStream.close();
      objectInputStream.close();
    } catch (Exception e) {
      APPLogger.LOG(LOGCAT.INFO, "Error on validatePeerDetail in NSTPeerClient  error msg " + e.getMessage());
    }
    return peerID;
  }

  public synchronized void updateP2pTransactionToGD(List<NSTPeerClient> p2pClientsDetails, String p2pTransactionUpdateReq)
      throws IOException, ClassNotFoundException {
    Socket clSocket = new Socket(APPConstans.LOCAL_IP, APPConstans.SERVER_DEFAULT_PORT);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
    ObjectInputStream objectInputStream = null;
    objectOutputStream.writeObject(p2pTransactionUpdateReq);
    objectOutputStream.writeObject(this);
    objectOutputStream.writeObject(p2pClientsDetails);
    objectInputStream = new ObjectInputStream(clSocket.getInputStream());
    String responseMsg = (String) objectInputStream.readObject();
    if (responseMsg.equalsIgnoreCase(APPConstans.P2P_TRANSACTION_UPDATE_SUS)) {
      APPLogger.LOG(LOGCAT.INFO, "P2P Transaction updated Successfully");
    }
    clSocket.close();
    objectOutputStream.close();
    objectInputStream.close();
  }

  public String getPeerName() {
    return peerName;
  }

  public void setPeerName(String peerName) {
    this.peerName = peerName;
  }

  public String getPeerPosition() {
    return peerPosition;
  }

  public void setPeerPosition(String peerPosition) {
    this.peerPosition = peerPosition;
  }

  public String getSelectedOrder() {
    return selectedOrder;
  }

  public void setSelectedOrder(String selectedOrder) {
    this.selectedOrder = selectedOrder;
  }

  public boolean isOrderConfirmed() {
    return orderConfirmed;
  }

  public void setOrderConfirmed(boolean orderConfirmed) {
    this.orderConfirmed = orderConfirmed;
  }

  public String getOrderID() {
    return orderID;
  }

  public void setOrderID(String orderID) {
    this.orderID = orderID;
  }

  public String getPeerID() {
    return peerID;
  }

  public void setPeerID(String peerID) {
    this.peerID = peerID;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getDetectionAlgorithm() {
    return detectionAlgorithm;
  }

  public void setDetectionAlgorithm(String detectionAlgorithm) {
    this.detectionAlgorithm = detectionAlgorithm;
  }

}
