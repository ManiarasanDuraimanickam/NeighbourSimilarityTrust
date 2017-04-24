package com.nationstar.spring.model;

import org.springframework.stereotype.Component;

@Component
public class Department {

	private int id;

	private String name;

	private String HOD;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHOD() {
		return HOD;
	}

	public void setHOD(String hOD) {
		HOD = hOD;
	}

}
