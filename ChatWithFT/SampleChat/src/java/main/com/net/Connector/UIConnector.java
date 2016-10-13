package com.net.Connector;

public interface UIConnector {

	public void updateNeighbourList();

	public void UpdateMessages(String Msg, String sender);

	public void emptyMgsORServerNotSelectedError();

	public void showTrayMsg(String Msg, String sender);
	
	public void showSendingMsg(String Msg,String sender);
	
	public void hideIpColumn();
}
