package com.project.nst.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.project.nst.Model.PeerDetails;
import com.project.nst.socket.NSTPeerClient;
import com.project.nst.utils.APPConstans;

public class PeerRegistrationConsole extends GUICommonWindows {
  private Component component;
  private String peerName;
  private String peerPosition;
  private String peerID;

  public PeerRegistrationConsole(int width, int height, String name, Color color, int xPosition, int yPosition) {
    super(width, height, name, color, xPosition, yPosition);
  }

  public void showWindow() {
    JPanel jpanel = getConsole();
    component = jpanel;
    Dimension componentPanelDimension = new Dimension(this.width, this.heigth);
    JPanel componentPanel = new JPanel();
    componentPanel.setBorder(BorderFactory.createTitledBorder("Registration"));
    componentPanel.setPreferredSize(componentPanelDimension);
    componentPanel.setMinimumSize(componentPanelDimension);
    componentPanel.setSize(componentPanelDimension);
    componentPanel.setBackground(Color.white);
    JLabel label = new JLabel(APPConstans.REGISTRATION_WINDOW_TXT);
    JLabel label1 = new JLabel("                                                     ");
    componentPanel.add(label);
    componentPanel.add(label1);
    JButton registerBtn = new JButton(APPConstans.REGISTRATION_BTN_TXT);
    registerBtn.setAlignmentX(SwingConstants.CENTER);
    registerBtn.addActionListener(new Registration(this));
    componentPanel.add(registerBtn);
    jpanel.add(componentPanel);
  }

  @SuppressWarnings("unchecked")
  public void closeWindow() throws UnknownHostException, IOException, ClassNotFoundException {
    Point pts = this.component.getLocationOnScreen();
    ClientConsole clientConsole =
        new ClientConsole(APPConstans.CLIENT_CONSOLE_WIDTH, APPConstans.CLIENT_CONSOLE_HEIGHT, APPConstans.ClientName,
            APPConstans.SKU_BLUE, (pts.x), (pts.y));
    clientConsole.setPeerName(this.peerName);
    clientConsole.setPeerPosition(this.peerPosition);
    clientConsole.setPeerID(peerID);
    NSTPeerClient nstClient = clientConsole.nstClient;
    nstClient.setPeerName(this.peerName);
    nstClient.setPeerPosition(this.peerPosition);
    Object[] response = clientConsole.nstClient.peerRegisterAndDeRegister(APPConstans.PEER_REGISTRATION);
    NSTPeerClient nstClientResp = (NSTPeerClient) response[1];
    List<PeerDetails> peerDetailsList = (List<PeerDetails>) response[2];
    updatedConnectedPeerTables(nstClientResp, peerDetailsList, clientConsole);
    String peerID = nstClientResp.getPeerID();
    clientConsole.setPeerID(peerID);
    clientConsole.nstClient.setDetectionAlgorithm(nstClientResp.getDetectionAlgorithm());
    this.frame.dispose();
    clientConsole.showWindow();
    int port = APPConstans.getPortByPosition(this.peerPosition);
    clientConsole.startPeerServer(port);
    updatedNSTServerContactList(nstClientResp, clientConsole, peerDetailsList);
    clientConsole.startSynchronizerThread();
    clientConsole.startBatchRunner();
  }

  private void updatedNSTServerContactList(NSTPeerClient nstClientResp, ClientConsole clientConsole,
      List<PeerDetails> peerDetailsList) {

    if (clientConsole.nstPeerServer.peerDetailList == null) {
      clientConsole.nstPeerServer.peerDetailList = new ArrayList<PeerDetails>();
    }
    for (PeerDetails peerDetail : peerDetailsList) {
      if (!nstClientResp.getPeerPosition().equalsIgnoreCase(peerDetail.getPeerPosition())) {
        peerDetail.setPeerDistance((APPConstans.getPortByPosition(nstClientResp.getPeerPosition()))
            - APPConstans.getPortByPosition(peerDetail.getPeerPosition()));
        clientConsole.nstPeerServer.peerDetailList.add(peerDetail);
      }
    }
  }

  private void updatedConnectedPeerTables(NSTPeerClient nstClientResp, List<PeerDetails> peerDetailsList,
      ClientConsole clientConsole) {
    addGroupLeaderDetails(clientConsole);
    for (PeerDetails peerDetail : peerDetailsList) {
      if (!nstClientResp.getPeerPosition().equalsIgnoreCase(peerDetail.getPeerPosition())) {
        updatedconnectedPeersDetailsTBL(peerDetail, clientConsole);
      }
    }

  }

  private void addGroupLeaderDetails(ClientConsole clientConsole) {
    Object[] obj = {APPConstans.CONNECT_GROUP_LEADER_TXT, APPConstans.SERVER_DEFAULT_PORT};
    clientConsole.connectedPeersDetails.addRow(obj);
    clientConsole.connectedPeersDetails.fireTableRowsInserted(clientConsole.connectedPeersDetails.getRowCount(),
        clientConsole.connectedPeersDetails.getRowCount());
  }

  private void updatedconnectedPeersDetailsTBL(PeerDetails peerDetail, ClientConsole clientConsole) {
    int port = APPConstans.getPortByPosition(peerDetail.getPeerPosition());
    Object[] obj = {peerDetail.getPeerName(), port};
    clientConsole.connectedPeersDetails.addRow(obj);
    clientConsole.connectedPeersDetails.fireTableRowsInserted(clientConsole.connectedPeersDetails.getRowCount(),
        clientConsole.connectedPeersDetails.getRowCount());
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

  public String getPeerID() {
    return peerID;
  }

  public void setPeerID(String peerID) {
    this.peerID = peerID;
  }

  private class Registration implements ActionListener {

    private PeerRegistrationConsole peerRegistrationConsole;

    public Registration(PeerRegistrationConsole peerRegistrationConsole) {
      this.peerRegistrationConsole = peerRegistrationConsole;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        this.peerRegistrationConsole.closeWindow();
      } catch (UnknownHostException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } catch (ClassNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }

  }
}
