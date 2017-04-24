package com.nationstar.spring.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.nationstar.spring.dao.StudentDAO;
import com.nationstar.spring.model.Student;

public class SchoolController {

	private StudentDAO studentDAO;
	private Student student;
	private String username;
	private String password;
	private String greet="Welcome User..!";
	public SchoolController(){
		System.out.println("School Controller Created...");
	}

	// @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	// public Student getStudent(@PathVariable int id) {
	public Student getStudent() {
		return studentDAO.getStudent(student.getId());
	}

	// @RequestMapping(value = "/add", method = RequestMethod.POST)
	// public int addStudent(@RequestAttribute("student") Student student) {
	public int addStudent() {
		return this.studentDAO.addStudent(student);// @RequestBody Student
													// std,@RequestAttribute
													// Student
													// std1
	}

	// @RequestMapping(value = "/checkStd", method = RequestMethod.POST)
	// public boolean isExists(@ModelAttribute Student student) {
	public boolean isExists() {
		return this.studentDAO.isExists(student);
	}

	// @RequestMapping(value = "/checkStd/{student}", method =
	// RequestMethod.POST)
	// public boolean isExists(@PathVariable int student) {
	public boolean isExistsByID() {
		return this.studentDAO.isExists(student);
	}

	public StudentDAO getStudentDAO() {
		return studentDAO;
	}

	public void setStudentDAO(StudentDAO studentDAO) {
		this.studentDAO = studentDAO;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public String getAuthorize() {
		System.out.println("Login...");
		this.studentDAO.getStudent(1);
		return "home";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGreet() {
	     /* FacesContext.getCurrentInstance().addMessage(null, 
	              new FacesMessage(FacesMessage.SEVERITY_INFO,"Successfully Updated", 
	  "Updated value to " + greet));*/
		return greet;
	}

	public void setGreet(String greet) {
		this.greet = greet;
	}
}
