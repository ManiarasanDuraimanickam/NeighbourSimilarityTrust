package com.project.nst.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.project.nst.socket.NSTPeerClient;
import com.project.nst.socket.NSTPeerServer;
import com.project.nst.socket.PeerTransactionSynchronizer;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class ClientConsole extends GUICommonWindows {

  public final JTextField nodeNameTxt = new JTextField(8);
  public JComboBox<String> selectedOrder = new JComboBox<String>(APPConstans.Available_Items);
  private String peerName;
  private String peerPosition;
  private String peerID;
  private Component component;
  public NSTPeerClient nstClient;
  public NSTPeerServer nstPeerServer;
  String[] colHeader = {"Peer Name", "port"};
  public DefaultTableModel connectedPeersDetails = new DefaultTableModel(colHeader, 0);
  public JTable connectedPeersDetailsTBL = new JTable(connectedPeersDetails);
  public DefaultTableModel p2pTableModel;
  private JTable p2pTable;
  private boolean p2pTableUpdated;
  PeerTransactionSynchronizer peerSynchronizer;
  private JLabel peerIDLBL = new JLabel();
  public BatchRunner batchRunner;

  public ClientConsole(int width, int height, String name, Color color, int xPos, int yPos) {
    super(width, height, name, color, xPos, yPos);
    this.nstClient = new NSTPeerClient(this);
  }

  public void showWindow() {
    JPanel jpanel = getConsole();
    component = jpanel;
    jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
    Dimension componentPanelDimension = new Dimension(this.width, this.heigth - 10);
    JPanel componentPanel = new JPanel();
    componentPanel.setPreferredSize(componentPanelDimension);
    componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
    componentPanel.setBackground(Color.white);
    setClientElementsInConsole(componentPanel);
    componentPanel.add(getOrderPlacingBtn());
    jpanel.add(componentPanel);
  }

  public void startPeerServer(int port) throws IOException {
    if (nstPeerServer == null) {
      this.nstPeerServer = new NSTPeerServer(port, this);
      this.nstPeerServer.start();
    } else
      APPLogger.LOG(LOGCAT.WARNING, "Already you peer Server is Running and the port is " + nstPeerServer.getPort());
  }

  public void startSynchronizerThread() {
    peerSynchronizer = new PeerTransactionSynchronizer(this, this.nstClient);
    peerSynchronizer.start();

  }

  public void startBatchRunner() {
    batchRunner = new BatchRunner(this);
    batchRunner.startThread();
  }

  private Component getOrderPlacingBtn() {
    JPanel orderSubmissionPanel = getInnerPanelWithTitle(new JPanel(), Color.white, null, 100, 0);
    JButton orderSubmission = new JButton(APPConstans.CLIENT_PLACE_ORDER_TXT);
    JButton logout = new JButton(APPConstans.CLIENT_LOGOUT_TXT);
    orderSubmission.addActionListener(getOrderSubmissionEvent());
    orderSubmissionPanel.add(orderSubmission);
    logout.addActionListener(getLogoutAction(component, false, this));
    orderSubmissionPanel.add(logout);
    return orderSubmissionPanel;
  }

  private ActionListener getOrderSubmissionEvent() {
    return new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        orderPlacingFunction(false);
      }
    };
  }

  public void orderPlacingFunction(boolean isBatch) {
    if (selectedOrder.getSelectedItem() == null
        || selectedOrder.getSelectedItem().toString().equalsIgnoreCase(APPConstans.CLIENT_ITEM_SELECT_ORDER_TXT)
        || nodeNameTxt.getText() == null || nodeNameTxt.getText().length() == 0) {
      if (!isBatch) {
        showMessageBox(component, APPConstans.CLIENT_ITEM_NOTSELECT_ERROR_MSG);
      }
    } else {
      int selectedPeerRow = connectedPeersDetailsTBL.getSelectedRow();
      if (connectedPeersDetailsTBL.getSelectedRow() == -1) {
        JOptionPane.showMessageDialog(component, APPConstans.CLIENT_PEER_NOTSELECT_ERROR_MSG);
        return;
      } else {
        int selectedPort =
            (int) connectedPeersDetailsTBL.getValueAt(selectedPeerRow, APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL);
        nstClient.setPeerName(nodeNameTxt.getText());
        nstClient.setPeerPosition(peerPosition);
        nstClient.setSelectedOrder(selectedOrder.getSelectedItem().toString());
        nstClient.setOrderConfirmed(false);
        nstClient.placeOrder(selectedPort, APPConstans.ORDER_PLACING,isBatch);
        connectedPeersDetailsTBL.clearSelection();
      }
    }
  }

  public void updatedWindowPosition(int xPosition, int yPosition) {
    frame.setLocation(xPosition, yPosition);
  }

  private void setClientElementsInConsole(JPanel componentPanel) {
    componentPanel.add(getPeerAddingPanel());
    JPanel totalClientsInfo = new JPanel();
    totalClientsInfo.setLayout(new BoxLayout(totalClientsInfo, BoxLayout.Y_AXIS));
    totalClientsInfo = getInnerPanelWithTitle(totalClientsInfo, Color.white, "Order Details", 100, 250);

    JPanel totalPeersPanel = new JPanel();
    totalPeersPanel.add(new Label(APPConstans.CLIENT_SELECT_YOUR_ITEM_TXT));
    totalPeersPanel.add(this.selectedOrder);
    totalPeersPanel.setBackground(Color.white);
    totalPeersPanel.add(new JLabel("      "));

    totalPeersPanel.add(getPeerSelectingTable());
    totalClientsInfo.add(totalPeersPanel);


    totalClientsInfo.add(getPeerTransactionsPanel("Transactions History", false, APPConstans.CLNT_TRANS_TABLE_WIDTH,
        APPConstans.CLNT_TRANS_TABLE_HEIGTH));
    totalClientsInfo.add(getP2PTransactionPanel("P2P History", APPConstans.CLNT_TRANS_TABLE_WIDTH,
        APPConstans.CLNT_TRANS_TABLE_HEIGTH));
    componentPanel.add(totalClientsInfo);
  }

  private Component getP2PTransactionPanel(String Title, int clntTransTableWidth, int clntTransTableHeigth) {
    JPanel transactionDetailsPNL = new JPanel();
    transactionDetailsPNL = getInnerPanelWithTitle(transactionDetailsPNL, Color.white, Title, 100, 200);
    transactionDetailsPNL.add(getp2pTransactionTable(clntTransTableWidth, clntTransTableHeigth));
    return transactionDetailsPNL;
  }

  protected JScrollPane getp2pTransactionTable(int width, int height) {
    this.p2pTableModel = new DefaultTableModel(this.serverCols, 0);
    this.p2pTable = new JTable(this.p2pTableModel);
    JScrollPane jScrollPane = new JScrollPane(p2pTable);
    jScrollPane.setAutoscrolls(true);
    jScrollPane.setPreferredSize(new Dimension(width, height));
    return jScrollPane;
  }

  private Component getPeerSelectingTable() {

    connectedPeersDetailsTBL.getColumn(APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL_TXT).setPreferredWidth(0);
    connectedPeersDetailsTBL.getColumn(APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL_TXT).setWidth(0);
    connectedPeersDetailsTBL.getColumn(APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL_TXT).setMaxWidth(0);
    connectedPeersDetailsTBL.getColumn(APPConstans.CLIENT_CONNECTED_PEERS_PORT_COL_TXT).setMinWidth(0);
    connectedPeersDetailsTBL.setFont(APPConstans.STD_FONT_SIZE);
    connectedPeersDetailsTBL.setForeground(APPConstans.SKU_BLUE);
    connectedPeersDetailsTBL.setAutoscrolls(false);
    connectedPeersDetailsTBL.setColumnSelectionAllowed(false);
    JScrollPane jScrollPane = new JScrollPane(connectedPeersDetailsTBL);
    jScrollPane.setAutoscrolls(true);
    jScrollPane.setPreferredSize(new Dimension(220, 90));
    jScrollPane.setBackground(Color.white);
    jScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "connected Peers"));
    return jScrollPane;
  }

  private JPanel getPeerAddingPanel() {
    JPanel clientRequirementPanel = new JPanel();
    clientRequirementPanel = getInnerPanelWithTitle(clientRequirementPanel, Color.white, "Your Details", 80, 15);
    clientRequirementPanel.setBackground(Color.white);

    clientRequirementPanel.add(new JLabel(APPConstans.CLIENT_CONSOLE_PEER_NAME_LBL));
    nodeNameTxt.setText(getPeerName());
    clientRequirementPanel.add(nodeNameTxt);

    clientRequirementPanel.add(new JLabel(APPConstans.CLIENT_CONSOLE_PEER_ID_LBL));
    peerIDLBL.setText(getPeerID());
    clientRequirementPanel.add(peerIDLBL);
    return clientRequirementPanel;
  }

  public void insertRowInP2PTable(Object[] val2, final Color cyan) {
    int existsRowCount = this.p2pTableModel.getRowCount();
    this.p2pTableModel.addRow(val2);
    this.p2pTableModel.fireTableRowsInserted(this.p2pTableModel.getRowCount(), this.p2pTableModel.getRowCount());
    setRowColor(cyan, this.p2pTable, existsRowCount);
  }

  public void insertOrderRecored(NSTPeerClient client, String orderStatus) {
    Object[] orderVals =
        {this.serverTableModel.getRowCount() + 1, client.getPeerName(), client.getOrderID(), client.getSelectedOrder(),
            orderStatus};
    this.insertRow(orderVals);
  }

  public String getPeerPosition() {
    return peerPosition;
  }

  public void setPeerPosition(String peerPosition) {
    this.peerPosition = peerPosition;
  }

  public String getPeerName() {
    return peerName;
  }

  public void setPeerName(String peerName) {
    this.peerName = peerName;
  }

  public String getPeerID() {
    return peerID;
  }

  public void setPeerID(String peerID) {
    this.peerID = peerID;
  }

  public void closewindow() {
    this.frame.dispose();
  }

  public Component getComponent() {
    return component;
  }

  public void setComponent(Component component) {
    this.component = component;
  }

  public boolean isP2pTableUpdated() {
    return p2pTableUpdated;
  }

  public void setP2pTableUpdated(boolean p2pTableUpdated) {
    this.p2pTableUpdated = p2pTableUpdated;
  }
}
