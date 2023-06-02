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
public class ControllerPrescriptionCreate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /*
     * Doctor requests blank form for a new prescription.
     */
    @GetMapping("/prescription/new")
    public String newPrescription(Model model) {
        model.addAttribute("prescription", new Prescription());
        return "prescription_create";
    }

    /*
     * Process the new prescription form. 
     * 1. Validate that Doctor SSN exists and matches Doctor Name. 
     * 2. Validate that Patient SSN exists and matches Patient Name. 
     * 3. Validate that Drug name exists. 
     * 4. Insert new prescription. 
     * 5. If there's an error, return an error message and the prescription form. 
     * 6. Otherwise, return the prescription with the generated rxid number.
     */

    @PostMapping("/prescription")
    public String createPrescription(Prescription prescription, Model model) {
        // 1. Validate that Doctor SSN exists and matches Doctor Name
    	String doctorSSN = prescription.getDoctor_ssn();
        String doctorName = prescription.getDoctorFirstName();
        boolean isDoctorValid = Sanitizer.isSSN(Integer.parseInt(doctorSSN)) && Sanitizer.isString(doctorName);

        // 2. Validate that Patient SSN exists and matches Patient Name
        String patientSSN = prescription.getPatient_ssn();
        String patientName = prescription.getPatientFirstName();
        boolean isPatientValid = Sanitizer.isSSN(Integer.parseInt(patientSSN)) && Sanitizer.isString(patientName);

        // 3. Validate that Drug name exists
        String drugName = prescription.getDrugName();
        boolean isDrugValid = Sanitizer.isString(drugName);

        // 4. Insert new prescription
        if (isDoctorValid && isPatientValid && isDrugValid) {
            String rxid = insertPrescription(prescription);
            if (rxid != null) {
                prescription.setRxid(rxid);
                model.addAttribute("message", "Prescription created.");
                model.addAttribute("prescription", prescription);
                return "prescription_show";
            } else {
                model.addAttribute("message", "Failed to create a prescription.");
            }
        } else {
            model.addAttribute("message", "Invalid doctor, patient, or drug information.");
        }

        return "prescription_create";
    }

    /*
     * Return JDBC Connection using jdbcTemplate in Spring Server
     */
    private Connection getConnection() throws SQLException {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        return conn;
    }

    // Method to insert prescription and return generated rxid
    private String insertPrescription(Prescription prescription) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO prescriptions (doctorssn, patientssn, formula) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, prescription.getDoctor_ssn());
            statement.setString(2, prescription.getPatient_ssn());
            statement.setString(3, prescription.getDrugName());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating prescription failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                } else {
                    throw new SQLException("Creating prescription failed, no rxid obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
