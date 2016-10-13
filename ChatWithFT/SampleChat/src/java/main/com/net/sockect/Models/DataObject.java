package com.net.sockect.Models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Serializable;

public class DataObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String textMgs = null;

	private String ServerID = null;

	private String myID = null;

	private boolean requestServerID = false;

	private boolean fileTransfer = false;

	private String fileName = null;

	private transient BufferedReader bufReaderFileDatas = null;

	private transient InputStream inStreamFileDatas = null;
	
	private transient Object objFiledatas=null;


	public DataObject() {}

	public DataObject(String mgs) {
		this.textMgs = mgs;
	}


	public DataObject(String mgs, boolean requestId) {
		this.textMgs = mgs;
		this.requestServerID = requestId;
	}

	public DataObject(String mgs, boolean requestId, String myID) {
		this.textMgs = mgs;
		this.requestServerID = requestId;
		this.myID = myID;
	}

	/**
	 * send Buffered Reader data.
	 * @param mgs
	 * @param requestId
	 * @param myID
	 * @param fileTransfer
	 * @param fileName
	 * @param fileDatas
	 */
	public DataObject(String mgs, boolean requestId, String myID, boolean fileTransfer, String fileName, BufferedReader fileDatas) {
		this.textMgs = mgs;
		this.requestServerID = requestId;
		this.myID = myID;
		this.fileTransfer = fileTransfer;
		this.fileName = fileName;
		this.bufReaderFileDatas = fileDatas;
	}

	/**
	 * send inputstream data.
	 * 
	 * @param mgs
	 * @param requestId
	 * @param myID
	 * @param fileTransfer
	 * @param fileName
	 * @param fileDatas
	 */
	public DataObject(String mgs, boolean requestId, String myID, boolean fileTransfer, String fileName, InputStream fileDatas) {
		this.textMgs = mgs;
		this.requestServerID = requestId;
		this.myID = myID;
		this.fileTransfer = fileTransfer;
		this.fileName = fileName;
		this.inStreamFileDatas = fileDatas;
	}

	/**
	 * send Object data.
	 * 
	 * @param mgs
	 * @param requestId
	 * @param myID
	 * @param fileTransfer
	 * @param fileName
	 * @param fileDatas
	 */
	public DataObject(String mgs, boolean requestId, String myID, boolean fileTransfer, String fileName, Object fileDatas) {
		this.textMgs = mgs;
		this.requestServerID = requestId;
		this.myID = myID;
		this.fileTransfer = fileTransfer;
		this.fileName = fileName;
		this.objFiledatas = fileDatas;
	}

	public String getServerID() {
		return ServerID;
	}

	public void setServerID(String serverID) {
		ServerID = serverID;
	}


	public String getTextMgs() {
		return textMgs;
	}

	public void setTextMgs(String textMgs) {
		this.textMgs = textMgs;
	}

	public boolean isRequestServerID() {
		return requestServerID;
	}

	public void setRequestServerID(boolean requestServerID) {
		this.requestServerID = requestServerID;
	}

	public String getMyID() {
		return myID;
	}

	public void setMyID(String myID) {
		this.myID = myID;
	}

	public boolean isFileTransfer() {
		return fileTransfer;
	}

	public void setFileTransfer(boolean fileTransfer) {
		this.fileTransfer = fileTransfer;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BufferedReader getBufReaderFileDatas() {
		return bufReaderFileDatas;
	}

	public void setBufReaderFileDatas(BufferedReader bufReaderFileDatas) {
		this.bufReaderFileDatas = bufReaderFileDatas;
	}

	public InputStream getInStreamFileDatas() {
		return inStreamFileDatas;
	}

	public void setInStreamFileDatas(InputStream inStreamFileDatas) {
		this.inStreamFileDatas = inStreamFileDatas;
	}

	public Object getObjFiledatas() {
		return objFiledatas;
	}

	public void setObjFiledatas(Object objFiledatas) {
		this.objFiledatas = objFiledatas;
	}

}
