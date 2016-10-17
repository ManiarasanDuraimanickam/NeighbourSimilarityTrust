package com.project.nst.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.project.nst.socket.NSTPeerClient;
import com.project.nst.utils.APPConstans;

public class GUICommonWindows {

  protected JFrame frame = new JFrame();
  protected final int width;
  protected final int heigth;
  protected final String name;
  protected final Color color;
  protected final Dimension dimension;
  protected final Label label;
  protected final int xPosition, yPosition;
  protected final String[] serverCols = {"S.no", "Position", "Node Name", "Order ID", "Item", "Status"};
  private String[] peerCols = {"S.no", "Node Name", "Order ID", "Item", "Status"};
  public DefaultTableModel serverTableModel;
  private JTable serverTable;
  protected List<Integer> P2pTableNativeColorsList=new ArrayList<Integer>();

  protected GUICommonWindows(int width, int height, String name, Color color, int xPosition, int yPosition) {
    this.width = width;
    this.heigth = height;
    this.name = name;
    this.color = color;
    this.dimension = new Dimension(this.width, this.heigth);
    this.label = new Label(name);
    this.xPosition = xPosition;
    this.yPosition = yPosition;
  }

  protected JPanel getConsole() {
    GUICommonWindows displayFrame = this;
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setResizable(false);
    JPanel windowPanel = new JPanel();
    displayFrame.addDisplayComp(frame, windowPanel);
    return windowPanel;
  }

  private void addDisplayComp(Frame frame, JPanel windowPanel) {
    windowPanel.setPreferredSize(this.dimension);
    windowPanel.setBackground(this.color);
    windowPanel.add(this.label, -1);
    frame.add(windowPanel);
    frame.setAlwaysOnTop(false);
    frame.setSize(this.width, this.heigth);

    frame.setFocusable(true);
    frame.setLocationRelativeTo(null);
    frame.setTitle(this.name);
    frame.pack();
    frame.setVisible(true);
    frame.setSize(this.width, this.heigth);
    frame.setPreferredSize(this.dimension);
    frame.setMaximumSize(this.dimension);
    if (this.xPosition > 0 && this.yPosition > 0) {
      frame.setLocation(this.xPosition, this.yPosition);
    }
  }

  protected void showMessageBox(Component component, String msg) {
    JOptionPane.showMessageDialog(component, msg);
  }

  protected int showMessageBoxWithConfirm(Component component, String msg, String title, int options) {
    int selected = JOptionPane.showConfirmDialog(component, msg, title, options);
    return selected;
  }

  protected JPanel getInnerPanelWithTitle(JPanel innerPanel, Color color, String title, int width, int height) {
    innerPanel.setBackground(color);
    innerPanel.setBorder(BorderFactory.createTitledBorder(title));
    innerPanel.setPreferredSize(new Dimension(width, height));
    return innerPanel;
  }

  protected void setRowColor(final Color cyan, JTable p2pTable, final int existsRowCoun) {
    p2pTable.setDefaultRenderer(Object.class, new  DefaultTableCellRenderer(){
    private static final long serialVersionUID = 3447324242127243415L;
    private boolean doColor;
    public Component getTableCellRendererComponent(JTable jtable, Object val, boolean isSelected, boolean hasFocus, int row,
        int col) {
      super.getTableCellRendererComponent(jtable, val, isSelected, hasFocus, row, col);
      if (col==0 && P2pTableNativeColorsList.contains((Integer)val)){
        doColor=true;
      }
      else if(col==0 && !P2pTableNativeColorsList.contains((Integer)val)){
        doColor=false;
      }
      if(doColor){
        setBackground(Color.cyan);
      }
      else {
        setBackground(jtable.getBackground());
      }
      return this;

    }
   });
  }

  protected JScrollPane getDefaultTransactionTable(int width, int height, boolean isServer) {
    if (isServer) {
      this.serverTableModel = new DefaultTableModel(this.serverCols, 0);
    } else {
      this.serverTableModel = new DefaultTableModel(this.peerCols, 0);
    }
    this.serverTable = new JTable(this.serverTableModel);
    JScrollPane jScrollPane = new JScrollPane(serverTable);
    jScrollPane.setAutoscrolls(true);
    jScrollPane.setPreferredSize(new Dimension(width, height));
    return jScrollPane;
  }

  public void insertRow(Object[] val2) {
    this.serverTableModel.addRow(val2);
    this.serverTableModel.fireTableRowsInserted(this.serverTableModel.getRowCount(), this.serverTableModel.getRowCount());
  }

  public void paintServerTable() {
    this.serverTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      private boolean doColor=false;
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable jtable, Object val, boolean isSelected, boolean hasFocus, int row,
          int col){
        super.getTableCellRendererComponent(jtable, val, isSelected, hasFocus, row, col);
        if (col==0 && P2pTableNativeColorsList.contains((Integer)val)){
          doColor=true;
        }
        else if(col==0 && !P2pTableNativeColorsList.contains((Integer)val)){
          doColor=false;
        }
        if(doColor){
          setBackground(Color.magenta);
        }else {
          setBackground(Color.orange);
        }
        return this;
      }
    });
  }

  public void updateRecords(DefaultTableModel serverTableModel, NSTPeerClient client, int orderCol, int statusCol, String status) {
    for (int i = 0; i < serverTableModel.getRowCount(); i++) {
      if (serverTableModel.getValueAt(i, orderCol).toString().equalsIgnoreCase(client.getOrderID())) {
        serverTableModel.setValueAt(status, i, statusCol);
      }
    }
  }

  protected Component getPeerTransactionsPanel(String Title, boolean isServer, int tableWidth, int tableHeigth) {
    JPanel transactionDetailsPNL = new JPanel();
    transactionDetailsPNL = getInnerPanelWithTitle(transactionDetailsPNL, Color.white, Title, 100, 200);
    transactionDetailsPNL.add(getDefaultTransactionTable(tableWidth, tableHeigth, isServer));
    return transactionDetailsPNL;
  }

  protected ActionListener getLogoutAction(final Component component, final boolean isServer, final Object currentWindow) {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int selected = showMessageBoxWithConfirm(component, "Are you Sure to Exit", "Logout Confirm", 2);
        if (selected != 0) {
          return;
        }
        if (isServer) {
          System.exit(0);
        } else {
          ClientConsole clientConsole = (ClientConsole) currentWindow;
          try {
            clientConsole.nstClient.peerRegisterAndDeRegister(APPConstans.PEER_DE_REGISTRATION);
            clientConsole.nstPeerServer.setRunNSTPeerServer(false);
            clientConsole.peerSynchronizer.stopProcess();
            clientConsole.peerSynchronizer.updateP2pTransToGL();
            clientConsole.batchRunner.stopThread();
          } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
          }
          clientConsole.closewindow();

        }
      }
    };
  }
  public void addOrderEntryInJaccardList(Object object) {
    this.P2pTableNativeColorsList.add((Integer)object);
  }
  public boolean isEntryExistsInJaccardList(Object val){
    return this.P2pTableNativeColorsList.contains(val);
  }
}
