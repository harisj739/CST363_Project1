package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    /* 
     * Patient requests form to search for prescription.
     */
    @GetMapping("/prescription/fill")
    public String getfillForm(Model model) {
        model.addAttribute("prescription", new Prescription());
        return "prescription_fill";
    }

    /*
     * Process the prescription fill request from a patient.
     * 1. Validate that Prescription p contains rxid, pharmacy name, and pharmacy address
     *    and uniquely identify a prescription and a pharmacy.
     * 2. Update prescription with pharmacy id, name, and address.
     * 3. Update prescription with today's date.
     * 4. Display the updated prescription.
     * 5. If there is an error, show the form with an error message.
     */
    @PostMapping("/prescription/fill")
    public String processFillForm(Prescription p, Model model) {
        try {
            // 1. Validate that Prescription p contains rxid, pharmacy name, and pharmacy address.
            if (!validatePrescription(p)) {
                model.addAttribute("message", "Invalid Prescription information.");
                model.addAttribute("prescription", p);
                return "prescription_fill";
            }

            // 2. Update prescription with pharmacy id, name, and address.
            updatePrescription(p);

            // 3. Update prescription with today's date.
            p.setDateFilled(new java.util.Date().toString());

            // 4. Display the updated prescription.
            model.addAttribute("message", "Prescription has been filled.");
            model.addAttribute("prescription", p);
            return "prescription_show";
        } catch (SQLException e) {
            // Handle any database errors
            model.addAttribute("message", "Error processing the prescription.");
            model.addAttribute("prescription", p);
            return "prescription_fill";
        }
    }

    /*
     * Validate that Prescription p contains rxid, pharmacy name, and pharmacy address.
     */
    private boolean validatePrescription(Prescription p) {
        return p.getRxid() != null && !p.getRxid().isEmpty() &&
               p.getPharmacyName() != null && !p.getPharmacyName().isEmpty() &&
               p.getPharmacyAddress() != null && !p.getPharmacyAddress().isEmpty();
    }

    /*
     * Update prescription with pharmacy id, name, and address.
     */
    private void updatePrescription(Prescription p) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE prescription SET pharmacyID = ?, pharmacyName = ?, pharmacyAddress = ? WHERE rxid = ?");
        stmt.setString(1, p.getPharmacyID());
        stmt.setString(2, p.getPharmacyName());
        stmt.setString(3, p.getPharmacyAddress());
        stmt.setString(4, p.getRxid());
        stmt.executeUpdate();
    }

    /*
     * Return JDBC Connection using jdbcTemplate in Spring Server.
     */
    private Connection getConnection() throws SQLException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        return conn;
    }
}

