package com.nationstar.spring.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Student {

	public enum StudingYear {

		I, II, III, IV;
	}

	private int id;
	private String name;
	private long contact;
	private StudingYear studingYear;
	private Department department;
	private List<Address> address;

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

	public long getContact() {
		return contact;
	}

	public void setContact(long contact) {
		this.contact = contact;
	}

	public StudingYear getStudingYear() {
		return studingYear;
	}

	public void setStudingYear(StudingYear studingYear) {
		this.studingYear = studingYear;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Address> getAddress() {
		if (this.address == null) {
			this.address = new ArrayList<Address>();
		}
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

}
