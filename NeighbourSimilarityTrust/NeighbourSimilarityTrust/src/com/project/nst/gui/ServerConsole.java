package com.project.nst.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.project.nst.Model.PeerDetails;
import com.project.nst.Model.SybilDetection;
import com.project.nst.socket.NSTGroupLeaderClient;
import com.project.nst.socket.NSTPeerClient;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPConstans.Algorithm;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class ServerConsole extends GUICommonWindows {

  private static final JTextField clientNumberTxt = new JTextField(8);
  private static final JButton addClientsBtn = new JButton("Click to Add");
  private static final Pattern numCheckPattern = Pattern.compile("^[0-9]+$");
  private static JLabel totalClientInfoLBL = new JLabel();
  private static int totalConnetcedPeers = 0;
  private static final JRadioButton RADIO_BUTTON_AlGO_JAX = new JRadioButton(Algorithm.ALGORITHM.getType());
  private static final JRadioButton RADIO_BUTTON_AlGO_2 = new JRadioButton(Algorithm.ALGORITHM2.getType());
  private Component component;
  private boolean serverTransationTBLUpdated;
  public boolean isSelectedJACCARD;
  List<PeerDetails> peerDetailList = null;
  public List<SybilDetection> jarccardSybilDetection = new ArrayList<SybilDetection>();
  public List<SybilDetection> eucSybilDetatection = new ArrayList<SybilDetection>();


  public ServerConsole(int width, int height, String name, int xPos, int yPos) {
    super(width, height, name, Color.cyan, xPos, yPos);
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
    addElementsInComponentPanel(componentPanel);
    componentPanel.add(getAlgorithmPanel());
    jpanel.add(componentPanel);
  }

  private void addElementsInComponentPanel(JPanel componentPanel) {

    componentPanel.add(getPeerAddingPanel());

    JPanel totalClientsInfo = new JPanel();
    totalClientsInfo.setLayout(new BoxLayout(totalClientsInfo, BoxLayout.Y_AXIS));
    totalClientsInfo = getInnerPanelWithTitle(totalClientsInfo, Color.white, "Client Info", 100, 180);

    ServerConsole.totalClientInfoLBL.setText("0");
    JPanel totalPeersPanel = new JPanel();
    totalPeersPanel.add(new Label("Total connected Peer Nodes: "));
    totalPeersPanel.add(ServerConsole.totalClientInfoLBL, -1);
    totalClientsInfo.add(totalPeersPanel);

    totalClientsInfo.add(getPeerTransactionsPanel("Peer Transactions", true, APPConstans.TRANS_TABLE_WIDTH,
        APPConstans.TRANS_TABLE_HEIGTH));
    componentPanel.add(totalClientsInfo);

  }

  public ActionListener ClientAddBtnClickEvent() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (!RADIO_BUTTON_AlGO_JAX.isSelected() && !RADIO_BUTTON_AlGO_2.isSelected()) {
          JOptionPane.showMessageDialog(component, APPConstans.SERVER_CONSOLE_ALGORITHM_NOTSELECT_TXT);
          return;
        } else {
          isSelectedJACCARD = RADIO_BUTTON_AlGO_JAX.isSelected();
        }
        int clientNumbers = 0;
        Matcher matcher = numCheckPattern.matcher(clientNumberTxt.getText());
        if (matcher.matches()) {
          clientNumbers = Integer.valueOf(clientNumberTxt.getText());
          updatingPeerInfoInServerConsole(clientNumbers);
          clientNumberTxt.setText("");
        } else {
          showMessageBox(component, APPConstans.SERVER_CONSOLE_PEER_ADD_ERROR_TXT);
          clientNumberTxt.setText("");
        }
      }
    };
  }

  public void updatingPeerInfoInServerConsole(int clientNumbers) {
    Integer totalClients = Integer.valueOf(totalClientInfoLBL.getText());
    totalClients = totalClients + clientNumbers;
    for (int i = 1; i <= clientNumbers; i++) {
      totalConnetcedPeers = totalConnetcedPeers + 1;
      PeerRegistrationConsole registrationConsole =
          new PeerRegistrationConsole(300, 200, APPConstans.PEER_REGISTRATION, APPConstans.SKU_BLUE, ((this.xPosition
              + totalClients + i) * 20), ((this.yPosition + totalClients + i) * 20));
      registrationConsole.setPeerName("P".concat("" + totalConnetcedPeers));
      registrationConsole.setPeerPosition("P00" + totalConnetcedPeers);
      registrationConsole.showWindow();
    }
  }

  private JPanel getAlgorithmPanel() {
    ButtonGroup group = new ButtonGroup();
    RADIO_BUTTON_AlGO_JAX.setBackground(Color.magenta);
    RADIO_BUTTON_AlGO_2.setBackground(Color.orange);
    group.add(RADIO_BUTTON_AlGO_JAX);
    group.add(RADIO_BUTTON_AlGO_2);
    JPanel algorithmSelection = getInnerPanelWithTitle(new JPanel(), Color.white, null, 100, 0);
    algorithmSelection.add(RADIO_BUTTON_AlGO_JAX);
    algorithmSelection.add(RADIO_BUTTON_AlGO_2);
    JButton alogithmBtn = new JButton(APPConstans.SER_Algorithm_BtnTxt);
    alogithmBtn.addActionListener(proceedAlgorithm());
    algorithmSelection.add(alogithmBtn);
    JButton exportDetection = new JButton("Detected & Export");
    exportDetection.addActionListener(findSybilNodes());
    algorithmSelection.add(exportDetection);
    JButton logout = new JButton("Log out");
    logout.addActionListener(getLogoutAction(this.component, true, this));
    algorithmSelection.add(logout);
    return algorithmSelection;
  }

  private JPanel getPeerAddingPanel() {
    JPanel clientRequirementPanel = new JPanel();
    clientRequirementPanel = getInnerPanelWithTitle(clientRequirementPanel, Color.white, "Adding Clients", 80, 15);
    clientRequirementPanel.setBackground(Color.white);
    clientRequirementPanel.add(new Label(APPConstans.SERVER_CONSOLE_PEER_ADD_TXT));
    clientRequirementPanel.add(clientNumberTxt);
    addClientsBtn.addActionListener(ClientAddBtnClickEvent());
    clientRequirementPanel.add(addClientsBtn);
    return clientRequirementPanel;
  }

  protected ActionListener proceedAlgorithm() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (showMessageBoxWithConfirm(component, APPConstans.SERVER_CONSOLE_ALGORITHM_CHANGE_MSG, "Algorithm Chnage Confirm", 2) == 0) {
          broacastAlgorithmChangingToAll();
        }

        /*
         * if (!RADIO_BUTTON_AlGO_JAX.isSelected() && !RADIO_BUTTON_AlGO_2.isSelected()) {
         * 
         * } if (RADIO_BUTTON_AlGO_JAX.isSelected()) { jaccardAlgorithm(); }
         */
      }
    };
  }

  private void broacastAlgorithmChangingToAll() {
    String selectedAlorithm = null;
    // synchronized (isSelectedJACCARD) {
    if (RADIO_BUTTON_AlGO_JAX.isSelected()) {
      isSelectedJACCARD = true;
      selectedAlorithm = Algorithm.ALGORITHM.getType();
    } else {
      isSelectedJACCARD = false;
      selectedAlorithm = Algorithm.ALGORITHM2.getType();
    }
    NSTGroupLeaderClient leaderClient = new NSTGroupLeaderClient();
    for (PeerDetails peerDetail : this.getPeerDetailList()) {
      if (peerDetail == null || peerDetail.getPeerPosition() == null) {
        continue;
      }
      leaderClient.broadcastAlgorithmChange(APPConstans.getPortByPosition(peerDetail.getPeerPosition()), selectedAlorithm);
    }
    // }


  }

  private ActionListener findSybilNodes() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("Detection Result");
        int rowCount = 1;
        String[] defaultRows = {"Detection Time", "TotalTransactions", "SuccessTransactions", "FailedTransactions"};
        Row row = sheet.createRow(rowCount);
        Cell algorithNameCell=row.createCell(3);
        algorithNameCell.setCellValue("JACCARD ALGORITHM");
        rowCount=rowCount+1;
        row = sheet.createRow(rowCount);
        int cellCount = 0;
        for (String defaultRow : defaultRows) {
          Cell cell = row.createCell(cellCount++);
          cell.setCellValue(defaultRow);
        }
        rowCount=rowCount+1;
        for (SybilDetection detection : jarccardSybilDetection) {
          row = sheet.createRow(rowCount);
          Cell cell;
          int i = 0;
          cell = row.createCell(i++);
          cell.setCellValue(detection.getDetectedTime());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getTotaltransaction());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getSuccessTransactions());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getFailedTransactions());
          rowCount=rowCount+1;
          APPLogger.LOG(
              LOGCAT.INFO,
              "ALG-JACCARD  detectedTime - " + detection.getDetectedTime() + " totalTrans - " + detection.getTotaltransaction()
                  + " succesTrans - " + detection.getSuccessTransactions() + " failedTrans - "
                  + detection.getFailedTransactions());
        }
        rowCount = rowCount + 2;
        row= sheet.createRow(rowCount);
        algorithNameCell=row.createCell(3);
        algorithNameCell.setCellValue("EUCLIDIEAN ALGORITHM");
        rowCount = rowCount + 1;
        row= sheet.createRow(rowCount);
        cellCount = 0;
        for (String defaultRow : defaultRows) {
          Cell cell = row.createCell(cellCount++);
          cell.setCellValue(defaultRow);
        }
        rowCount= rowCount+1;
        for (SybilDetection detection : eucSybilDetatection) {
          row = sheet.createRow(rowCount);
          Cell cell;
          int i = 0;
          cell = row.createCell(i++);
          cell.setCellValue(detection.getDetectedTime());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getTotaltransaction());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getSuccessTransactions());
          cell = row.createCell(i++);
          cell.setCellValue(detection.getFailedTransactions());
          rowCount++;
          APPLogger.LOG(
              LOGCAT.INFO,
              "ALG-EUC  detectedTime - " + detection.getDetectedTime() + " totalTrans - " + detection.getTotaltransaction()
                  + " succesTrans - " + detection.getSuccessTransactions() + " failedTrans - "
                  + detection.getFailedTransactions());
        }
        FileOutputStream outStream;
        try {
          String fileName="F:";//System.getProperty("user.home");
          fileName=fileName.concat("NSTSysbilDetectionResult.xls");
          outStream = new FileOutputStream(new File(fileName));
          hssfWorkbook.write(outStream);
          outStream.close();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    };
  }

  /*
   * @Deprecated private void jaccardAlgorithm() { ChartWindow chartWindow = new ChartWindow();
   * chartWindow.pack(); RefineryUtilities.centerFrameOnScreen(chartWindow);
   * chartWindow.setVisible(true); }
   */

  public Component getComponent() {
    return component;
  }

  public void setComponent(Component component) {
    this.component = component;
  }

  public void updatedTotalPeerCount(DefaultTableModel serverTableModel, int serverPositionCol, String peerPosition) {
    boolean isNewPeerNode = false;
    if (serverTableModel != null) {
      for (int i = 0; i < serverTableModel.getRowCount(); i++) {
        isNewPeerNode = serverTableModel.getValueAt(i, serverPositionCol).toString().equalsIgnoreCase(peerPosition);
      }
    }
    if (!isNewPeerNode) {
      Integer totalClients = Integer.valueOf(totalClientInfoLBL.getText());
      totalClients = totalClients + 1;
      totalClientInfoLBL.setText(totalClients.toString());
    }

  }

  public void insertP2pTransaction(List<NSTPeerClient> p2pTransDetails) {
    boolean isEntryExists = false;
    synchronized (this.serverTableModel) {
      for (NSTPeerClient p2pDetail : p2pTransDetails) {
        isEntryExists = false;
        if (this.serverTableModel.getRowCount() == 0) {
          Object[] orders =
              APPConstans.getOrderToArray(p2pDetail, p2pDetail.getOrderID(), this.serverTableModel, p2pDetail.getOrderStatus());
          this.insertRow(orders);
          if (this.isSelectedJACCARD) {
            this.addOrderEntryInJaccardList(orders[0]);
          }
        }
        for (int i = 0; i < serverTableModel.getRowCount(); i++) {
          if (serverTableModel.getValueAt(i, APPConstans.SERVER_ORDER_COL).toString().equalsIgnoreCase(p2pDetail.getOrderID())) {
            isEntryExists = true;
            if (serverTableModel.getValueAt(i, APPConstans.SERVER_STATUS_COL).toString()
                .equalsIgnoreCase(p2pDetail.getOrderStatus())) {
              break;
            } else {
              serverTableModel.setValueAt(p2pDetail.getOrderStatus(), i, APPConstans.SERVER_STATUS_COL);
            }
          }
        }
        if (!isEntryExists) {
          Object[] orders =
              APPConstans.getOrderToArray(p2pDetail, p2pDetail.getOrderID(), this.serverTableModel, p2pDetail.getOrderStatus());
          this.insertRow(orders);
          if (this.isSelectedJACCARD) {
            this.addOrderEntryInJaccardList(orders[0]);
          }
        }
      }
    }
  }

  public boolean isServerTransationTBLUpdated() {
    return serverTransationTBLUpdated;
  }

  public void setServerTransationTBLUpdated(boolean serverTransationTBLUpdated) {
    this.serverTransationTBLUpdated = serverTransationTBLUpdated;
  }

  public List<PeerDetails> getPeerDetailList() {
    if (this.peerDetailList == null) {
      this.peerDetailList = new ArrayList<PeerDetails>();
    }
    return peerDetailList;
  }

  public void setPeerDetailList(List<PeerDetails> peerDetailList) {
    this.peerDetailList = peerDetailList;
  }

}
