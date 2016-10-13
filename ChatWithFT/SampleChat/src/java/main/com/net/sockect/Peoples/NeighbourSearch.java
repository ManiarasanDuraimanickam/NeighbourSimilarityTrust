package com.net.sockect.Peoples;

import java.io.Serializable;

public interface NeighbourSearch extends Runnable, Serializable {

	public void search();

	public void start();

	public void stop();
}
