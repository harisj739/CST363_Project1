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
        String dateStr = "";
        int age = 0;
        String primaryName = "";
		// TODO
		try (Connection con = getConnection();) {
			PreparedStatement ps = con.prepareStatement("insert into patient (lastname, firstname, birthday, patientssn, primarydoctor, street, city, state, zip)  values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			if (!p.getFirst_name().isEmpty() && !p.getLast_name().isEmpty() && !p.getPrimaryName().isEmpty()
					&& Sanitizer.isString(p.getFirst_name()) && Sanitizer.isString(p.getLast_name()) && Sanitizer.isString(p.getPrimaryName())) {
				ps.setString(1, p.getLast_name());
				ps.setString(2, p.getFirst_name());
				primaryName = p.getPrimaryName();
				ps.setString(5, p.getPrimaryName());
			} else {
				model.addAttribute("message", "ERROR: Names cannot be empty and must only contain letters A-Z. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_register";	
			}
			
			
			if(Sanitizer.isSSN(p.getSsn())) {
				ps.setInt(4, p.getSsn());
			} else {
				model.addAttribute("message", "ERROR: The SSN must be 9 digits, cannot start with a 0 or a 9, the middle digits cannot be 00, and the last 4 digits cannot be 0000.");
				model.addAttribute("Patient", p);
				return "patient_register";	
			}
			
			if(Sanitizer.isDOB(p.getBirthdate())) {
				ps.setDate(3, p.getBirthdate());
			} else {
				model.addAttribute("message", "ERROR: Invalid date: year must be in between 1900 to 2022 (inclusive), months 1 to 12 (inclusive), and days 1 to 31 (inclusive): " + dateStr);
				model.addAttribute("Patient", p);
				return "patient_register";
			}
			
			if(!p.getStreet().isEmpty() && Sanitizer.isStreet(p.getStreet())) {
				ps.setString(6, p.getStreet());
			} else {
				model.addAttribute("message", "ERROR: The street must only contain letters and numerical digits.");
				model.addAttribute("Patient", p);
				return "patient_register";
			}
			
			if (!p.getCity().isEmpty() && !p.getState().isEmpty()
					&& Sanitizer.isString(p.getCity())
					&& Sanitizer.isString(p.getState())) {
				ps.setString(7, p.getCity());
				ps.setString(8, p.getState());
			} else {
				model.addAttribute("message", "ERROR: The city and state cannot be empty and must only contain letters A-Z. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_register";	
			}
			
			if(Sanitizer.isZip(p.getZipcode())) {
				ps.setInt(9, p.getZipcode());
			} else {
				model.addAttribute("message", "ERROR: The zipcode must be either exactly 5 or 9 digits long. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_register";
			}
			
			
			PreparedStatement pst = con.prepareStatement("select specialty from doctor where doctorname = ?");
			pst.setString(1, p.getPrimaryName());
			ResultSet resultset = pst.executeQuery();
			if (resultset.next()) {
				String specialty = resultset.getString(1);
				if (specialty.equals("Family Medicine") || specialty.equals("Internal Medicine") || specialty.equals("Pediatrics")) {
					ps.setString(5, primaryName);
				}
				else {
					model.addAttribute("message", "ERROR: Invalid primary doctor - Choose a primary doctor that specializes in Family Medicine, Internal Medicine, and Pediatrics.");
					model.addAttribute("Patient", p);
					return "patient_register";
				}
			}
			
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) p.setPatientId((int)rs.getLong(1));
			
			ps = con.prepareStatement("select doctorssn from doctor where doctorname = ?");
			ps.setString(1, primaryName);
			rs = ps.executeQuery();
			if (rs.next()) {
				p.setPrimaryID(rs.getInt(1));
			} 
			
			ps = con.prepareStatement("update patient set doctorssn = ? where primarydoctor = ?");
			ps.setInt(1, p.getPrimaryID());
			ps.setString(2, p.getPrimaryName());
			ps.executeUpdate();
			
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("Patient", p);
			return "patient_show";
			
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
	//@PathVariable int patientId
	public String updatePatient(@PathVariable int patientId, Model model) {

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
//		try (Connection con = getConnection();) {
//
//			PreparedStatement ps = con.prepareStatement("update patient set street = ?, city = ?, state = ?, zip = ?, primarydoctor = ? where patientid = ?");
//			if(!p.getStreet().isEmpty() && Sanitizer.isStreet(p.getStreet())) {
//				ps.setString(1,  p.getStreet());
//			} else {
//				model.addAttribute("message", "ERROR: The street must only contain letters and numerical digits.");
//				model.addAttribute("Patient", p);
//				return "patient_edit";
//			}
//			if (!p.getCity().isEmpty() && !p.getState().isEmpty()
//					&& Sanitizer.isString(p.getCity())
//					&& Sanitizer.isString(p.getState())) {
//				ps.setString(2, p.getCity());
//				ps.setString(3, p.getState());
//			} else {
//				model.addAttribute("message", "ERROR: The city and state cannot be empty and must only contain letters A-Z. Please try again.");
//				model.addAttribute("Patient", p);
//				return "patient_edit";
//			}
//			if(Sanitizer.isZip(p.getZipcode())) {
//				ps.setInt(4, p.getZipcode());
//			} else {
//				model.addAttribute("message", "ERROR: The zipcode must be either exactly 5 or 9 digits long. Please try again.");
//				model.addAttribute("Patient", p);
//				return "patient_edit";
//			}
//			if (!p.getPrimaryName().isEmpty() && Sanitizer.isString(p.getPrimaryName())) {
//				ps.setString(5, p.getPrimaryName());
//			} else {
//				model.addAttribute("message", "ERROR: Names cannot be empty and must only contain letters A-Z. Please try again.");
//				model.addAttribute("Patient", p);
//				return "patient_edit";	
//			}
//			
//			ps.setInt(6,  patientId);
//			int rc = ps.executeUpdate();
//			
//			ps = con.prepareStatement("select doctorssn from doctor where doctorname = ?");
//			ps.setString(1, p.getPrimaryName());
//			ResultSet rs = ps.executeQuery();
//			if (rs.next()) {
//				p.setPrimaryID(rs.getInt(1));
//			} 
//			
//			ps = con.prepareStatement("update patient set doctorssn = ? where patientid = ?");
//			ps.setInt(1, p.getPrimaryID());
//			ps.setInt(2, patientId);
//			ps.executeUpdate();
//			
//			if (rc==1) {
//				model.addAttribute("message", "Update successful");
//				model.addAttribute("Patient", p);
//				
//				return "patient_show";
//				
//			}else {
//				model.addAttribute("message", "Error. Update was not successful");
//				model.addAttribute("Patient", p);
//				return "patient_edit";
//			}
//				
//		} catch (SQLException e) {
//			model.addAttribute("message", "SQL Error."+e.getMessage());
//			model.addAttribute("Patient", p);
//			return "patient_edit";
//		}
	}
	
	
	/*
	 * Process changes to patient profile.  
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient p, Model model) {

		// TODO
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("update patient set street = ?, city = ?, state = ?, zip = ?, primarydoctor = ? where patientid = ?");
			if(!p.getStreet().isEmpty() && Sanitizer.isStreet(p.getStreet())) {
				ps.setString(1,  p.getStreet());
			} else {
				model.addAttribute("message", "ERROR: The street must only contain letters and numerical digits.");
				model.addAttribute("Patient", p);
				return "patient_edit";
			}
			if (!p.getCity().isEmpty() && !p.getState().isEmpty()
					&& Sanitizer.isString(p.getCity())
					&& Sanitizer.isString(p.getState())) {
				ps.setString(2, p.getCity());
				ps.setString(3, p.getState());
			} else {
				model.addAttribute("message", "ERROR: The city and state cannot be empty and must only contain letters A-Z. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_edit";
			}
			if(Sanitizer.isZip(p.getZipcode())) {
				ps.setInt(4, p.getZipcode());
			} else {
				model.addAttribute("message", "ERROR: The zipcode must be either exactly 5 or 9 digits long. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_edit";
			}
			if (!p.getPrimaryName().isEmpty() && Sanitizer.isString(p.getPrimaryName())) {
				ps.setString(5, p.getPrimaryName());
			} else {
				model.addAttribute("message", "ERROR: Names cannot be empty and must only contain letters A-Z. Please try again.");
				model.addAttribute("Patient", p);
				return "patient_edit";	
			}
			
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
