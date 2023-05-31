package com.csumb.cst363;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DataGenerate {

    // USE THESE VARS TO CONNECT TO SERVER
    public static final String user = "root";
    public static final String pw = "Ballislife45!";
    public static final String server = "jdbc:mysql://localhost:3306/mydb";

    // Constants for number of patients, doctors and scripts
    public static final int doctorCount = 10;
    public static final int patientCount = 1000;
    public static final int scriptCount = 5000;

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
            patients.add(getPatient(i + 1, doctors.get(new Random().nextInt(doctors.size()))));
        }

        // make connection
        try (Connection con = DriverManager.getConnection(server, user, pw)) {
            PreparedStatement ps = null;

            // insert doctors
            for (Doctor d : doctors) {
                ps = con.prepareStatement(insertDoctorStatement(d));
                ps.setInt(1, d.getDoctorssn());
                ps.setString(2, d.getDoctorname());
                ps.setString(3, d.getSpecialty());
                ps.setDate(4, d.getStartdate());
                ps.executeUpdate();
            }

            // insert patients
            for (Patient p : patients) {
                ps = con.prepareStatement(insertPatientStatement(p));
                ps.setInt(1, p.getPatientId());
                ps.setInt(2, p.getSsn());
                ps.setString(3, p.getFirst_name());
                ps.setString(4, p.getLast_name());
                ps.setDate(5, p.getBirthdate());
                ps.setString(6, p.getStreet()());
                ps.setString(7, p.getCity());
                ps.setString(8, p.getgetState());
                ps.setInt(9, p.getZipcode());
                ps.setString(10, p.getPrimaryName());
                ps.executeUpdate();
            }

            // create random prescriptions and insert them by selecting random doctors and patients from lists
            for (int i = 0; i < scriptCount; i++) {
                int p = new Random().nextInt(patientCount);
                int d = new Random().nextInt(doctorCount);
                ps = con.prepareStatement(insertPrescription(doctors.get(d), patients.get(p)));
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // generate a doctor
    public static Doctor getDoctor(int id) {
        Doctor doc = new Doctor();
        doc.setDoctorssn(id);
        String name;
        int SSN;
        do {
            name = randomFullName();
            SSN = randomSSN();
            doc.setDoctorname(name);
            doc.setDoctorssn(SSN);
        } while (!Sanitizer.isName(name) || !Sanitizer.isSSN(SSN));

        doc.setSpecialty(randomSpecialty());
        doc.setStartdate(Date.valueOf((new Random().nextInt(60) + 1960) + "-01-01"));
        return doc;
    }

    // generate a patient
    public static Patient getPatient(int id, Doctor primaryID) {
        Patient p = new Patient();
        p.setPatientId(id);
        p.setSsn(randomSSN());
        p.setFirst_name(randomFirstName());
        p.setLast_name(randomLastName());
        p.setBirthdate(randomDOB());
        p.setStreet(randomStreet());
        p.setCity(randomCity());
        p.setState(randomState());
        p.setZipcode(randomZip());
        p.setPrimaryID(primaryID.getDoctorssn());
        return p;
    }

    // construct insert statement for doctors
    public static String insertDoctorStatement(Doctor d) {
        return "INSERT INTO doctor (doctorssn, doctorname, specialty, startdate) " +
                "VALUES (?, ?, ?, ?)";
    }

 // construct insert statement for patients
    public static String insertPatientStatement(Patient p) {
        return "INSERT INTO patient (patientid, patientssn, firstname, lastname, birthday, street, city, state, zip, primarydoctor) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }


    // construct insert statement for prescriptions
    public static String insertPrescription(Doctor d, Patient p) {
        int drug_id = new Random().nextInt(100) + 1;
        int quantity = new Random().nextInt(10) + 1;
        return "INSERT INTO prescription (doctorssn, patientId, drug_id, quantity) " +
                "VALUES (" + d.getDoctorssn() + ", " + p.getPatientId() + ", " + drug_id + ", " + quantity + ")";
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
