package com.csumb.cst363;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller    
public class ControllerPrescriptionFill {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Sanitizer Sanitizer;

	/*
	 * Patient requests form to search for prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getFillForm(Model model) {
		model.addAttribute("prescription", new Prescription());
		return "prescription_fill";
	}

	/*
	 * Process the prescription fill request from a patient.
	 * 1. Validate that Prescription p contains rxid, pharmacy name, and pharmacy address
	 *    and uniquely identify a prescription and a pharmacy.
	 * 2. Update prescription with pharmacyid, name, and address.
	 * 3. Update prescription with today's date.
	 * 4. Display updated prescription
	 * 5. If there is an error, show the form with an error message.
	 */
	@PostMapping("/prescription/fill")
	public String processFillForm(Prescription p, Model model) {
		// Sanitize input data
		String pharmacyName = Sanitizer.isString(p.getPharmacyName()) ? p.getPharmacyName() : "";
		String pharmacyAddress = Sanitizer.isStreet(p.getPharmacyAddress()) ? p.getPharmacyAddress() : "";
		
		// Check if sanitized data matches original data
		if (!pharmacyName.equals(p.getPharmacyName())
				|| !pharmacyAddress.equals(p.getPharmacyAddress())) {
			model.addAttribute("message", "Invalid input data.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		
		// Validate that Prescription p contains rxid, pharmacy name, and pharmacy address
		if (!validatePrescription(p)) {
			model.addAttribute("message", "Invalid prescription data.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		
		// Update prescription with pharmacyid, name, and address
		updatePrescription(p);
		
		// Update prescription with today's date
		p.setDateFilled(Date.valueOf(LocalDate.now()).toString());
		
		model.addAttribute("message", "Prescription has been filled.");
		model.addAttribute("prescription", p);
		return "prescription_show";
	}

	/*
	 * Return JDBC Connection using jdbcTemplate in Spring Server
	 */
	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

	// Method to validate prescription
	private boolean validatePrescription(Prescription p) {
		// Perform validation logic
		// Return true if valid, false otherwise
		return Sanitizer.isSSN(Integer.parseInt(p.getRxid())) && Sanitizer.isString(p.getPharmacyName())
				&& Sanitizer.isStreet(p.getPharmacyAddress());
	}


	// Method to update prescription with pharmacy information
	private void updatePrescription(Prescription p) {
		// Perform update logic
		// Update prescription with pharmacyid, name, and address
		p.setPharmacyID("70012345");
		p.setPharmacyAddress("123 Main St");
		p.setPharmacyName("Example Pharmacy");
	}

}
