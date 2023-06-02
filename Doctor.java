package com.csumb.cst363;

/*
 * This class is used to transfer data to/from doctor templates
 *  for registering new doctor and updating doctor profile.
 */
public class Doctor {
	
	private int id;   // unique id generated by database.
	private String last_name;
	private String first_name;
	private String specialty;
	private String practice_since_year;  // yyyy  The year doctor started in practice.
	private String ssn;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getPractice_since_year() {
		return practice_since_year;
	}
	public void setPractice_since_year(String practice_since_year) {
		this.practice_since_year = practice_since_year;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	@Override
	public String toString() {
		return "Doctor [id=" + id + ", last_name=" + last_name + ", first_name=" + first_name + ", specialty="
				+ specialty + ", practice_since_year=" + practice_since_year + ", ssn=" + ssn + "]";
	}
}


