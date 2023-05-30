package com.csumb.cst363;
import java.sql.Date;
/*
 * This class is used to transfer data to/from patient templates.
 */
public class Patient {
	
	private int patientId;
	private String last_name;
	private String first_name;
	private Date birthdate;  // yyyy-mm-dd
	private int ssn;
	private String street;
	private String city;
	private String state;
	private int zipcode;
	// following fields are data about primary doctor
	private int primaryID;       
	private String primaryName;  
	private String specialty;    
	private String years;       

	
//	public String getPatientId() {
//		return patientId;
//	}
//	public void setPatientId(String patientId) {
//		this.patientId = patientId;
//	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
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
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public int getSsn() {
		return ssn;
	}
	public void setSsn(int ssn) {
		this.ssn = ssn;
	}
	public int getPrimaryID() {
		return primaryID;
	}
	public void setPrimaryID(int primaryID) {
		this.primaryID = primaryID;
	}
	public String getPrimaryName() {
		return primaryName;
	}
	public void setPrimaryName(String primaryName) {
		this.primaryName = primaryName;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getZipcode() {
		return zipcode;
	}
	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}
	@Override
	public String toString() {
		return "Patient [patientId=" + patientId + ", last_name=" + last_name + ", first_name=" + first_name
				+ ", birthdate=" + birthdate + ", ssn=" + ssn + ", street=" + street + ", city=" + city + ", state="
				+ state + ", zipcode=" + zipcode + ", primaryID=" + primaryID + ", primaryName=" + primaryName
				+ ", specialty=" + specialty + ", years=" + years + "]";
	}
}
