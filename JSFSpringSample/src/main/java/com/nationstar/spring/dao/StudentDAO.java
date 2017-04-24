package com.nationstar.spring.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.nationstar.spring.model.Student;

@Repository
@Scope(value = "request")
public class StudentDAO {

	private static Map<Integer, Student> studentContainer;
	private static int totalRegStd = 0;

	@Autowired
	private Student std;
	@Autowired
	private ApplicationContext appContext;

	@PostConstruct
	public void init() {
		System.out.println("studentdoa init method.....");
		studentContainer = new HashMap<Integer, Student>();
	}

	public static Map<Integer, Student> getStudentContainer() {
		return studentContainer;
	}

	public static void setStudentContainer(
			Map<Integer, Student> studentContainer) {
		StudentDAO.studentContainer = studentContainer;
	}

	public Student getStudent(int id) {
		return getStudentContainer().get(id);
	}

	public int addStudent(Student student) {
		totalRegStd++;
		getStudentContainer().put(totalRegStd, student);
		return totalRegStd;
	}

	public boolean isExists(Student student) {
		return getStudentContainer().containsValue(student);
	}

	public boolean isExists(int student) {
		return getStudentContainer().containsKey(student);
	}

}
