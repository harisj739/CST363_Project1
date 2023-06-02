package com.csumb.cst363;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DataGenerate {

    // USE THESE VARS TO CONNECT TO SERVER
    public static final String user = "root";
    public static final String pw = "Ballislife45!";
    public static final String server = "jdbc:mysql://localhost:3306/project1";

    // Constants for number of patients, doctors and scripts
    public static final int doctorCount = 10;
    public static final int patientCount = 100;
    public static final int scriptCount = 100;

    // Constants for random text files
    public static final String fNameFile = "fnames.txt";
    public static final int fNameLines = 4940;
    public static final String lastNameFile = "lnames.txt";
    public static final int lnameLines = 21985;
    public static final String stateFile = "states.txt";
    public static final int stateLines = 51;
    public static final String streetFile = "streets.txt";
    public static final int streetLines = 500;
    public static final String citiesFile = "cities.txt";
    public static final int cityLines = 385;

    // Main driver
    public static void main(String[] args) {
        List<Patient> patients = new ArrayList<>();
        List<Doctor> doctors = new ArrayList<>();
        List<Prescription> prescriptions = new ArrayList<>();
        
        // create 10 random doctors in doctors list
        for (int i = 0; i < doctorCount; i++) {
            doctors.add(getDoctor(i + 1));
        }

        // create 500 random patients, use doctors list to populate primary id and primary name
        for (int i = 0; i < patientCount; i++) {
        	
//        	patients.add(getPatient(i + 1, doctors.get(new Random().nextInt(doctors.size()))));
        	patients.add(getPatient(doctors.get(new Random().nextInt(doctors.size()))));
        	
        }

        // make connection
        try (Connection con = DriverManager.getConnection(server, user, pw)) {
            PreparedStatement ps = null;

            // insert doctors
            for (Doctor d : doctors) {
                ps = con.prepareStatement(insertDoctorStatement(d));
                ps.setInt(1, d.getId());
                ps.setString(2, d.getLast_name());
                ps.setString(3, d.getSpecialty());
                ps.setDate(4, Date.valueOf(d.getPractice_since_year()));
                ps.executeUpdate();
            }

            // insert patients
            for (Patient p : patients) {
                ps = con.prepareStatement(insertPatientStatement(p), Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, p.getSsn());
                ps.setString(2, p.getFirst_name());
                ps.setString(3, p.getLast_name());
                ps.setDate(4, p.getBirthdate());
                ps.setString(5, p.getStreet());
                ps.setString(6, p.getCity());
                ps.setString(7, p.getState());
                ps.setInt(8, p.getZipcode());
                ps.setString(9, p.getPrimaryName());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                	p.setPatientId((int)rs.getLong(1));
                }
                
            }

            // create random prescriptions and insert them by selecting random doctors and patients from lists
            for (int i = 0; i < scriptCount; i++) {
                int p = new Random().nextInt(patientCount);
                int d = new Random().nextInt(doctorCount);
                int quantity = new Random().nextInt(10) + 1;
                Date startDate = randomDOB();
                Date endDate = randomDOB();
                while (endDate.compareTo(startDate) <= 0) {
                	endDate = randomDOB();
                }
                int prescription_id = i + 1;
                String pharmacyName = randomLastName() + " Pharmacy";
                String formula = "Drug " + prescription_id;
                String tradeName = "Tradename " + prescription_id;
                
                ps = con.prepareStatement("insert into drug (formula, tradename) values (?, ?)");
                ps.setString(1, formula);
                ps.setString(2, tradeName);
                ps.executeUpdate();
                
                ps = con.prepareStatement(insertPrescription());
                ps.setInt(1, prescription_id);
                ps.setInt(2, doctors.get(d).getId());
                ps.setString(3, doctors.get(d).getLast_name());
                ps.setString(4, pharmacyName);
                ps.setInt(5, patients.get(p).getPatientId());
                ps.setDate(6, startDate);
                ps.setDate(7, endDate);
                ps.setString(8, formula);
                ps.setInt(9, quantity);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // generate a doctor
    public static Doctor getDoctor(int id) {
        Doctor doc = new Doctor();
        doc.setId(id);
        String name;
        int SSN;
        do {
            name = randomFullName();
            SSN = randomSSN();
            doc.setLast_name(name);
            doc.setId(SSN);
        } while (!Sanitizer.isString(name) || !Sanitizer.isSSN(SSN));

        doc.setSpecialty(randomSpecialty());
        doc.setPractice_since_year(Date.valueOf((new Random().nextInt(60) + 1960) + "-01-01").toString());
        return doc;
    }

    // generate a patient
    public static Patient getPatient(Doctor primaryID) {
        Patient p = new Patient();
//        p.setPatientId(id);
        p.setSsn(randomSSN());
        p.setFirst_name(randomFirstName());
        p.setLast_name(randomLastName());
        p.setBirthdate(randomDOB());
        p.setStreet(randomStreet());
        p.setCity(randomCity());
        p.setState(randomState());
        p.setZipcode(randomZip());
//        p.setPrimaryID(primaryID.getDoctorssn());
        p.setPrimaryName(randomFullName());
        return p;
    }

    // construct insert statement for doctors
    public static String insertDoctorStatement(Doctor d) {
        return "INSERT INTO doctor (doctorssn, doctorname, specialty, startdate) " +
                "VALUES (?, ?, ?, ?)";
    }

 // construct insert statement for patients
    public static String insertPatientStatement(Patient p) {
        return "INSERT INTO patient (patientssn, firstname, lastname, birthday, street, city, state, zip, primarydoctor) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }


    // construct insert statement for prescriptions
    public static String insertPrescription() {
        return "INSERT INTO prescription (prescriptionid, doctorssn, doctorname, pharmacyName, patientid, startDate, endDate, formula, quantity) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    // helper methods for generating random data
    public static String randomFullName() {
        return randomFirstName() + " " + randomLastName();
    }

    public static String randomFirstName() {
        return randomLineFromFile(fNameFile, fNameLines);
    }

    public static String randomLastName() {
        return randomLineFromFile(lastNameFile, lnameLines);
    }

    public static String randomSpecialty() {
        String[] specialties = {"Cardiology", "Dermatology", "Endocrinology", "Gastroenterology", "Neurology"};
        return specialties[new Random().nextInt(specialties.length)];
    }


    public static Date randomDOB() {
        int year = new Random().nextInt(60) + 1940;
        int month = new Random().nextInt(12) + 1;
        int day = new Random().nextInt(28) + 1;
        LocalDate date = LocalDate.of(year, month, day);
        return Date.valueOf(date);
    }

    public static String randomStreet() {
        return randomLineFromFile(streetFile, streetLines) + " St";
    }

    public static String randomCity() {
        return randomLineFromFile(citiesFile, cityLines);
    }

    public static String randomState() {
        return randomLineFromFile(stateFile, stateLines);
    }

    public static int randomZip() {
        return new Random().nextInt(90000) + 10000;
    }

    public static int randomSSN() {
        return new Random().nextInt(900000000) + 100000000;
    }

    public static String randomLineFromFile(String fileName, int numLines) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int randomLine = new Random().nextInt(numLines);
            for (int i = 0; i < randomLine; i++) {
                scanner.nextLine();
            }
            String line = scanner.nextLine();
            scanner.close();
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}