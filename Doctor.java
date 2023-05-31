package com.csumb.cst363;

import java.sql.Date;

public class Doctor {

    private int doctorssn; // maps to `doctorssn` column in the `doctor` table
    private String doctorname; // maps to `doctorname` column in the `doctor` table
    private Date startdate; // maps to `startdate` column in the `doctor` table
    private String specialty; // maps to `specialty` column in the `doctor` table

    public int getDoctorssn() {
        return doctorssn;
    }

    public void setDoctorssn(int doctorssn) {
        this.doctorssn = doctorssn;
    }

    public String getDoctorname() {
        return doctorname;
    }

    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date date) {
        this.startdate = date;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return "Doctor [doctorssn=" + doctorssn + ", doctorname=" + doctorname + ", startdate=" + startdate
                + ", specialty=" + specialty + "]";
    }

}

