package com.project.nst.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.project.nst.Model.PeerDetails;
import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPLogger;
import com.project.nst.utils.APPLogger.LOGCAT;

public class NSTGroupLeaderClient extends Thread {

	public synchronized void updatedAllPeersContacts(int port, List<PeerDetails> peerDetailsList) {
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		Socket clSocket = null;
		try {
			clSocket = new Socket(APPConstans.LOCAL_IP, port);
			objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
			objectOutputStream.writeObject(APPConstans.SERVER_UPDATED_CONNECTED_PEER);
			objectOutputStream.writeObject(peerDetailsList);
			objectInputStream = new ObjectInputStream(clSocket.getInputStream());
			clSocket.close();
			objectOutputStream.flush();
			objectOutputStream.close();
			objectInputStream.close();
		} catch (Exception e) {
			APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
		}
		clSocket = null;
		objectOutputStream = null;
		objectInputStream = null;
	}

	public synchronized void updateFailedTransationToAll(int port, List<NSTPeerClient> failedTransactions) {
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		Socket clSocket = null;
		try {
			clSocket = new Socket(APPConstans.LOCAL_IP, port);
			objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
			objectOutputStream.writeObject(APPConstans.SERVER_UPDATE_FAILED_TRANS_REQ);
			objectOutputStream.writeObject(failedTransactions);
			objectInputStream = new ObjectInputStream(clSocket.getInputStream());
			clSocket.close();
			objectOutputStream.flush();
			objectOutputStream.close();
			objectInputStream.close();
		} catch (Exception e) {
			APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
		}
		clSocket = null;
		objectOutputStream = null;
		// objectInputStream = null;
	}
	public synchronized void broadcastAlgorithmChange(int port, String selectedAlorithm) {
      ObjectOutputStream objectOutputStream = null;
      ObjectInputStream objectInputStream = null;
      Socket clSocket = null;
      try {
          clSocket = new Socket(APPConstans.LOCAL_IP, port);
          objectOutputStream = new ObjectOutputStream(clSocket.getOutputStream());
          objectOutputStream.writeObject(APPConstans.SERVER_ALGORITHM_CHANGE_REQUEST);
          objectOutputStream.writeObject(selectedAlorithm);
          objectInputStream = new ObjectInputStream(clSocket.getInputStream());
          clSocket.close();
          objectOutputStream.flush();
          objectOutputStream.close();
          objectInputStream.close();
      } catch (Exception e) {
          APPLogger.LOG(LOGCAT.ERROR, e.getMessage());
      }
      clSocket = null;
      objectOutputStream = null;
      objectInputStream = null;
  }
}
