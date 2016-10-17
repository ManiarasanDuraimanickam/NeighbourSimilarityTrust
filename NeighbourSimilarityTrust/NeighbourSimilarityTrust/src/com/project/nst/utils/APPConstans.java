package com.project.nst.utils;

import java.awt.Color;
import java.awt.Font;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.project.nst.Model.PeerDetails;
import com.project.nst.socket.NSTPeerClient;

public class APPConstans {

  public enum Algorithm {
    ALGORITHM("JACCARD"), ALGORITHM2("EUC");
    private final String algorithType;

    Algorithm(String key) {
      this.algorithType = key;
    }

    public String getType() {
      return this.algorithType;
    }
  }

  public static final int CONSOLE_WIDTH = 550;
  public static final int CONSOLE_HEIGHT = 450;
  public static final int CLIENT_CONSOLE_WIDTH = 550;
  public static final int CLIENT_CONSOLE_HEIGHT = 550;
  public static final int TRANS_TABLE_WIDTH = 520;
  public static final int TRANS_TABLE_HEIGTH = 171;
  public static final int CLNT_TRANS_TABLE_WIDTH = 520;
  public static final int CLNT_TRANS_TABLE_HEIGTH = 111;
  public static final int SERVER_DEFAULT_PORT = 5214;
  public static final int SERVER_POSITION_COL = 1;
  public static final int SERVER_PEERNAME_COL = 2;
  public static final int SERVER_ORDER_COL = 3;
  public static final int SERVER_ITEM_COL = 4;
  public static final int SERVER_STATUS_COL = 5;
  public static final int CLIENT_STATUS_COL = SERVER_ITEM_COL;
  public static final int CLIENT_ORDER_COL = SERVER_PEERNAME_COL;
  public static final int CLIENT_CONNECTED_PEERS_PORT_COL = SERVER_POSITION_COL;
  public static final int MAXMINUM_FAILED_TRANSACTIONS = SERVER_STATUS_COL;
  public static final int CLIENT_SYNCHRONIZER_WAIT_TIME_L = 10000;
  public static final int GL_SYNCHRONIZER_WAIT_TIME_L = 15000;
  public static final int CLIENT_BATCHRUN_WAIT_TIME_L = 2000;

  public static String LOCAL_IP = null;
  public static final String ServerName = "Group Leader Peer";
  public static final String SERVER_UPDATED_CONNECTED_PEER = "Updated the connetced peer details";
  public static final String SERVER_UPDATE_FAILED_TRANS_REQ = "Updating the peers failed transactions";
  public static final String SER_Algorithm_BtnTxt = "Change the Algorithm";
  public static final String SERVER_CONSOLE_PEER_ADD_TXT = "Enter Number of Peer to Add : ";
  public static final String SERVER_CONSOLE_PEER_ADD_ERROR_TXT = "Please enter digit only.";
  public static final String SERVER_CONSOLE_ALGORITHM_NOTSELECT_TXT =
      "Please choose any one of the detection Alogithm before start";
  public static final String SERVER_ALGORITHM_CHANGE_REQUEST="please change your detection algorithm";

  public static final String REGISTRATION_BTN_TXT = "Click to Register";
  public static final String REGISTRATION_WINDOW_TXT = "To Start Your Transaction, Please Register..!";
  public static final String PEER_REGISTRATION = "Peer Registration";
  public static final String PEER_REGISTRATION_SUS = "Peer Registration successfully";

  public static final String ClientName = "Peer Node";
  public static final String ORDER_CONFIRM = "your order confirmed";
  public static final String ORDER_NOTCONFIRM = "your order notconfirmed";
  public static final String ORDER_FAILURE = "your order is failed";
  public static final String ORDER_PLACING = "peerOrderPlace";
  public static final String ORDER_CONFIRM_STS = "Confirmed";
  public static final String ORDER_PENDING_STS = "Pending";
  public static final String ORDER_FAILED_STS = "Failed";
  public static final String PEER_DE_REGISTRATION = "Peer DeRegistration";
  public static final String PEER_DE_REGISTRATION_SUS = "Peer DeRegistration successfully";
  public static final String CONNECT_GROUP_LEADER_TXT = "Group Leader";
  public static final String VALIDATE_PEER = "validate this peer";
  public static final String VALIDATE_PEER_FLR = "Cannot validate this Peer";
  public static final String VALIDATE_PEER_SUS = "Peer Validated Successfully";
  public static final String ORDER_CONFIRM_REQ_MSG = "Please Confirm your Order.";
  public static final String ORDER_FAILED_REQ_MSG = "Because of Invaid Credential your order has failed";
  public static final String ORDER_CONFIRM_CLIENT_POPUP_MSG = "Please Confirm your Order.Your order is {0}";
  public static final String P2P_TRANSACTION_UPDATE_REQ = "Request to updated p2p transactions";
  public static final String P2P_TRANSACTION_UPDATE_SUS = "P2p transactions updated successfully";
  public static final String CLIENT_ITEM_NOTSELECT_ERROR_MSG = "Please select a Valid Item";
  public static final String CLIENT_PLACE_ORDER_TXT = "Place your Order";
  public static final String CLIENT_LOGOUT_TXT = "Log out";
  public static final String CLIENT_ITEM_SELECT_ORDER_TXT = "Select Order";
  public static final String CLIENT_SELECT_YOUR_ITEM_TXT = "Select your Item : ";
  public static final String CLIENT_CONNECTED_PEERS_PORT_COL_TXT = "port";
  public static final String CLIENT_ITEM_FAILED_MSG_POPUP =
      "Sorry your order has been Failed. \n Your Order is {0} and order-Id is {1}";
  public static final String CLIENT_PEER_NOTSELECT_ERROR_MSG = "Please select the Peer.";
  public static final String SERVER_CONSOLE_ALGORITHM_CHANGE_MSG = "Are you sure,You want change the Detection Algorithm?";
  public static final String CHART_JACCARD_XAxis_TXT = "Number of Transactions";
  public static final String CHART_JACCARD_YAxis_TXT = "Detection percent";

  public static final Vector<String> Available_Items = new Vector<String>(Arrays.asList("Select Order", "Electronic",
      "Furnitures", "Home Appliance","Vechile","Books","Foods","Foot Wears","Dresses","Accessories"));

  public static final Color SKU_BLUE = new Color(1, 200, 255);
  public static final Font STD_FONT_SIZE = new Font("Calibri", Font.BOLD, 14);
  public static final String CLIENT_CONSOLE_PEER_NAME_LBL = "Name of the Node : ";
  public static final String CLIENT_CONSOLE_PEER_ID_LBL = "Peer ID : ";


  public static final Color getAlgorithmColor(String type) {
    if (type != null && type.equalsIgnoreCase(Algorithm.ALGORITHM.getType())) {
      return Color.magenta;
    } else if (type != null && type.equalsIgnoreCase(Algorithm.ALGORITHM2.getType())) {
      return Color.orange;
    } else {
      return Color.white;
    }
  }

  public static int getPortByPosition(String position) {
    return APPConstans.SERVER_DEFAULT_PORT + Integer.parseInt(position.substring(1, position.length()));
  }

  public static String getPeerNameHashing(String peerName) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.update(peerName.getBytes());
    String peerID = new BigInteger(1, messageDigest.digest()).toString(16);
    return peerID;
  }

  public static Object[] getOrderToArray(NSTPeerClient nstClient, String orderId, DefaultTableModel serverTableModel,
      String status) {
    return new Object[] {serverTableModel.getRowCount() + 1, nstClient.getPeerPosition(), nstClient.getPeerName(), orderId,
        nstClient.getSelectedOrder(), status};
  }

  public static String getPositionByPort(String port) {
    int pos = Integer.parseInt(port) - APPConstans.SERVER_DEFAULT_PORT;
    String peerPos = "P00" + pos;
    if (peerPos.equalsIgnoreCase("P000")) {
      peerPos = "Group Leader";
    }
    return peerPos;
  }

  public static String getNameByDistyance(List<PeerDetails> peerDetailList, int requestPeerDistance) {
    synchronized (peerDetailList) {
      for (PeerDetails peerDetails : peerDetailList) {
        if (peerDetails.getPeerDistance() == requestPeerDistance) {
          return peerDetails.getPeerName();
        }
      }
      return null;
    }
  }

}
