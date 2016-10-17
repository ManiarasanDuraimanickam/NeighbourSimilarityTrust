package com.project.nst.Model;

public class SybilDetection {

  private String detectedTime;
  private int totaltransaction;
  private int successTransactions;
  private int failedTransactions;
  
  public String getDetectedTime() {
    return detectedTime;
  }
  public void setDetectedTime(String detectedTime) {
    this.detectedTime = detectedTime;
  }
  public int getTotaltransaction() {
    return totaltransaction;
  }
  public void setTotaltransaction(int totaltransaction) {
    this.totaltransaction = totaltransaction;
  }
  public int getSuccessTransactions() {
    return successTransactions;
  }
  public void setSuccessTransactions(int successTransactions) {
    this.successTransactions = successTransactions;
  }
  public int getFailedTransactions() {
    return failedTransactions;
  }
  public void setFailedTransactions(int failedTransactions) {
    this.failedTransactions = failedTransactions;
  }
}
