package com.project.nst.Model;

import java.io.Serializable;

public class PeerDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5807443648910474748L;
	private String PeerName;
	private String PeerID;
	private String peerPosition;
	private int peerDistance;

	public String getPeerName() {
		return PeerName;
	}

	public String getPeerID() {
		return PeerID;
	}

	public String getPeerPosition() {
		return peerPosition;
	}

	public void setPeerName(String peerName) {
		PeerName = peerName;
	}

	public void setPeerID(String peerID) {
		PeerID = peerID;
	}

	public void setPeerPosition(String peerPosition) {
		this.peerPosition = peerPosition;
	}

  public int getPeerDistance() {
    return peerDistance;
  }

  public void setPeerDistance(int peerDistance) {
    this.peerDistance = peerDistance;
  }

}
