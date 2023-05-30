package com.csumb.cst363;

import java.sql.Connection;
import java.util.Random;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatient {
	
	static final String[] specialties = {"Internal Medicine", "Family Medicine", "Pediatrics", "Orthpedics", "Dermatology", 
			"Cardiology", "Gynecology", "Gastroenterology", "Psychiatry", "Oncology"};
	static final String[] alphabet = {"a", "A"};
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String newPatient(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new Patient());
		return "patient_register";
	}
	
	/*
	 * Process new patient registration	 */
	@PostMapping("/patient/new")
	public String newPatient(Patient p, Model model) { 
        
        long millis=System.currentTimeMillis();  
        Date date= new Date(millis);  
        int age;
		// TODO
		try (Connection con = getConnection();) {
			PreparedStatement ps = con.prepareStatement("insert into patient (lastname, firstname, birthday, patientssn, primarydoctor, street, city, state, zip)  values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			if (!p.getFirst_name().isEmpty() && !p.getLast_name().isEmpty()) {
				ps.setString(1, p.getLast_name());
				ps.setString(2, p.getFirst_name());
			}
			else {
				model.addAttribute("message", "ERROR: Names cannot be empty, please try again.");
				model.addAttribute("Patient", p);
				return "patient_register";	
			}
			
			if ((p.getBirthdate().getMonth() <= date.getMonth()) && (p.getBirthdate().getDay() <= date.getDay() - 1)) {
				age = p.getBirthdate().getYear() - date.getYear();
			}
			else {
				age = p.getBirthdate().getYear() - date.getYear() - 1;
			}
			
			if (age >= 18) {
				ps.setDate(3, p.getBirthdate());
			}
			else {
				model.addAttribute("message", "ERROR: The patient must be 18 years or older.");
				model.addAttribute("Patient", p);
				return "patient_register";	
			}
			ps.setInt(4, p.getSsn());
			ps.setString(5, p.getPrimaryName());
			ps.setString(6, p.getStreet());
			ps.setString(7, p.getCity());
			ps.setString(8, p.getState());
			ps.setInt(9, p.getZipcode());
			
			String primaryName = p.getPrimaryName();
			
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) p.setPatientId((int)rs.getLong(1));
			
			
			ps = con.prepareStatement("select doctorssn from doctor where doctorname = ?");
			ps.setString(1, primaryName);
			rs = ps.executeQuery();
			if (rs.next()) {
				p.setPrimaryID(rs.getInt(1));
			} 
			
			ps = con.prepareStatement("update patient set doctorssn = ? where patientid = ?");
			ps.setInt(1, p.getPrimaryID());
			ps.setInt(2, p.getPatientId());
			int rowCount = ps.executeUpdate();
			
			if(rowCount == 1) {
				ps = con.prepareStatement("alter table patient add foreign key (doctorssn) references doctor (doctorssn)");
				ps.executeUpdate();
			}
			
			// display message and patient information
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("Patient", p);
			return "patient_get";
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("Patient", p);
			return "patient_register";	
		}

		/*
		 * Complete database logic to verify and process new patient
		 */

	}
	
	/*
	 * Request blank form to search for patient by and and id
	 */
	@GetMapping("/patient/edit")
	public String getPatientForm(Model model) {
		return "patient_get";
	}
	
	/*
	 * Perform search for patient by patient id and name.
	 */
	
	// Patient p,
	@PostMapping("/patient/show")
	public String getPatientForm(Patient p, Model model) {

		// TODO
		// for DEBUG 
		try (Connection con = getConnection();) {
			
			PreparedStatement ps = con.prepareStatement("select patientssn, firstname, lastname, birthday, street, city, state, zip, primarydoctor from patient where patientid = ? && lastname = ?");
			ps.setInt(1,p.getPatientId());
			ps.setString(2, p.getLast_name());
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setSsn(rs.getInt(1));
				p.setFirst_name(rs.getString(2));
				p.setLast_name(rs.getString(3));
				p.setBirthdate(rs.getDate(4));
				p.setStreet(rs.getString(5));
				p.setCity(rs.getString(6));
				p.setState(rs.getString(7));
				p.setZipcode(rs.getInt(8));
				p.setPrimaryName(rs.getString(9));
				
				model.addAttribute("Patient", p);
				return "patient_edit";
			} else {
				model.addAttribute("message", "Patient not found.");
				model.addAttribute("Patient", p);
				return "patient_get";
			}
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("Patient", p);
			return "patient_get";
			
		}
	}

	/*
	 *  Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{patientId}")
	public String updatePatient(@PathVariable int patientId, Model model) {

		// TODO Complete database logic search for patient by id.
		Patient p = new Patient();
		p.setPatientId(patientId);
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("select patientssn, firstname, lastname, birthday, street, city, state, zip, primarydoctor from patient where patientid = ?");
			ps.setInt(1, patientId);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setSsn(rs.getInt(1));
				p.setFirst_name(rs.getString(2));
				p.setLast_name(rs.getString(3));
				p.setBirthdate(rs.getDate(4));
				p.setStreet(rs.getString(5));
				p.setCity(rs.getString(6));
				p.setState(rs.getString(7));
				p.setZipcode(rs.getInt(8));
				p.setPrimaryName(rs.getString(9));
				model.addAttribute("Patient", p);
				return "patient_show";
			} else {
				model.addAttribute("message", "Doctor not found.");
				model.addAttribute("Patient", p);
				return "patient_get";
			}
			
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("Patient", p);
			return "patient_get";
			
		}
	}
	
	
	/*
	 * Process changes to patient profile.  
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient p, Model model) {

		// TODO
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("update patient set street = ?, city = ?, state = ?, zip = ?, primarydoctor = ? where patientid = ?");
			ps.setString(1,  p.getStreet());
			ps.setString(2, p.getCity());
			ps.setString(3, p.getState());
			ps.setInt(4,  p.getZipcode());
			ps.setString(5, p.getPrimaryName());
			ps.setInt(6,  p.getPatientId());
			int rc = ps.executeUpdate();
			
			ps = con.prepareStatement("select doctorssn from doctor where doctorname = ?");
			ps.setString(1, p.getPrimaryName());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p.setPrimaryID(rs.getInt(1));
			} 
			
			ps = con.prepareStatement("update patient set doctorssn = ? where patientid = ?");
			ps.setInt(1, p.getPrimaryID());
			ps.setInt(2, p.getPatientId());
			ps.executeUpdate();
			
			if (rc==1) {
				model.addAttribute("message", "Update successful");
				model.addAttribute("Patient", p);
				
				return "patient_show";
				
			}else {
				model.addAttribute("message", "Error. Update was not successful");
				model.addAttribute("Patient", p);
				return "patient_edit";
			}
				
		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("Patient", p);
			return "patient_edit";
		}
	}

	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}
