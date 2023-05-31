package com.csumb.cst363;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDate;

@Controller
public class ControllerDoctor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /*
     * Request for new doctor registration form.
     */
    @GetMapping("/doctor/register")
    public String newDoctor(Model model) {
        // return blank form for new doctor registration
        model.addAttribute("doctor", new Doctor());
        return "doctor_register";
    }

    /*
     * Process doctor registration.
     */
    @PostMapping("/doctor/register")
    public String createDoctor(@ModelAttribute("doctor") Doctor doctor, Model model) {

        try (Connection con = getConnection();) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO mydb.doctor (doctorssn, doctorname, startdate, specialty) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, doctor.getDoctorssn());
            ps.setString(2, doctor.getDoctorname());
            ps.setDate(3, new java.sql.Date(doctor.getStartdate().getTime()));
            ps.setString(4, doctor.getSpecialty());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) doctor.setDoctorssn(rs.getInt(1));

            // display message and doctor information
            model.addAttribute("message", "Registration successful.");
            model.addAttribute("doctor", doctor);
            return "doctor_show";

        } catch (SQLException e) {
            model.addAttribute("message", "SQL Error: " + e.getMessage());
            model.addAttribute("doctor", doctor);
            return "doctor_register";
        }
    }


    /*
     * Request form for doctor search.
     */
    @GetMapping("/doctor/get")
    public String getDoctor(Model model) {
        // return form to enter doctor id and name
        model.addAttribute("doctor", new Doctor());
        return "doctor_get";
    }

    /*
     * Search for doctor by id and name.
     */
    @PostMapping("/doctor/get")
    public String getDoctor(@ModelAttribute("doctor") Doctor doctor, Model model) {

        try (Connection con = getConnection();) {
            PreparedStatement ps = con.prepareStatement("SELECT doctorname, startdate, specialty FROM mydb.doctor WHERE doctorssn = ? AND doctorname = ?");
            ps.setInt(1, doctor.getDoctorssn());
            ps.setString(2, doctor.getDoctorname());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                doctor.setDoctorname(rs.getString(1));
                doctor.setStartdate(new java.sql.Date(rs.getDate(2).getTime()));
                doctor.setSpecialty(rs.getString(3));
                model.addAttribute("doctor", doctor);
                return "doctor_show";

            } else {
                model.addAttribute("message", "Doctor not found.");
                return "doctor_get";
            }

        } catch (SQLException e) {
            model.addAttribute("message", "SQL Error: " + e.getMessage());
            model.addAttribute("doctor", doctor);
            return "doctor_get";
        }
    }


    /*
     * search for doctor by id.
     */
    @GetMapping("/doctor/edit/{id}")
    public String getDoctor(@PathVariable int id, Model model) {
        Doctor doctor = new Doctor();
        doctor.setDoctorssn(id);
        try (Connection con = getConnection();) {

            PreparedStatement ps = con.prepareStatement("SELECT doctorname, startdate, specialty FROM mydb.doctor WHERE doctorssn = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                doctor.setDoctorname(rs.getString(1));
                doctor.setStartdate(new java.sql.Date(rs.getDate(2).getTime()));
                doctor.setSpecialty(rs.getString(3));
                model.addAttribute("doctor", doctor);
                return "doctor_edit";
            } else {
                model.addAttribute("message", "Doctor not found.");
                return "doctor_get";
            }

        } catch (SQLException e) {
            model.addAttribute("message", "SQL Error: " + e.getMessage());
            model.addAttribute("doctor", doctor);
            return "doctor_get";
        }
    }


    /*
     * Update doctor profile.
     */
    @PostMapping("/doctor/edit")
    public String updateDoctor(@ModelAttribute("doctor") Doctor doctor, Model model) {

        try (Connection con = getConnection();) {
            PreparedStatement ps = con.prepareStatement("UPDATE mydb.doctor SET doctorname = ?, startdate = ?, specialty = ? WHERE doctorssn = ?");
            ps.setString(1, doctor.getDoctorname());
            ps.setDate(2, new java.sql.Date(doctor.getStartdate().getTime()));
            ps.setString(3, doctor.getSpecialty());
            ps.setInt(4, doctor.getDoctorssn());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                model.addAttribute("message", "Profile updated successfully.");
                model.addAttribute("doctor", doctor);
                return "doctor_show";
            } else {
                model.addAttribute("message", "Doctor not found.");
                model.addAttribute("doctor", doctor);
                return "doctor_edit";
            }

        } catch (SQLException e) {
            model.addAttribute("message", "SQL Error: " + e.getMessage());
            model.addAttribute("doctor", doctor);
            return "doctor_edit";
        }
    }


    /*
     * Utility method to get a database connection.
     * Replace with your own method to establish a database connection.
     */
    private Connection getConnection() throws SQLException {
        // Replace with your own code to establish a database connection
        return jdbcTemplate.getDataSource().getConnection();
    }
}
