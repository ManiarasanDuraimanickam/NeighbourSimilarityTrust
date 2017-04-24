package com.nationstar.spring.controller;


public class EmployerController {

	// @RequestMapping(value = "/studentname", method = RequestMethod.GET)
	public String getStudentName() {

		System.out.println("Student...");
		return "test";
	}
}
