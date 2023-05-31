package com.csumb.cst363;

import java.sql.Connection;
import java.util.Scanner; 
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PharmacyReport {

	public static void main(String[] args) {
		int exitProgram = 0;   //Checks if the program should terminate.
		// TODO Auto-generated method stub
//		try {
//			while (exitProgram != 999) {
		/*
		 * The try-catch block reads user input, checking for a valid
		 * pharmacy name and prescription date range. 
		 * If an invalid pharmacy name or date is inputed, the program
		 * throws an exception stating the error.
		 */
		try (Scanner sc = new Scanner(System.in)) {
			//Program reads user input and saves it.
			System.out.println("Enter pharmacy name: ");
			String pharmacyName = sc.nextLine(); 
			System.out.println("Enter prescription's start date (YYYY-MM-DD): ");
			String startDate = sc.nextLine(); 
			System.out.println();
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project1", "root",
					"$hazia@#Mehreen30739");
			/*
			 * After the database connection is established, the following 
			 * query grabs each of the pharmacy's filled prescriptions, 
			 * displaying the drug names and their total quantity.  
			 */
			PreparedStatement ps = conn.prepareStatement(
					"select pr.formula, sum(pr.quantity) from filled_prescription fpr, prescription pr "
							+ "where fpr.prescriptionid = pr.prescriptionid && pr.pharmacyName = ? && pr.date >= ? group by pr.formula");
			if (!pharmacyName.isEmpty()) {
				ps.setString(1, pharmacyName);
				ps.setString(2, startDate);
				ResultSet rs = ps.executeQuery();
				String border = "-------------------------";
				if(rs.next()) {
					System.out.println();
					System.out.println(pharmacyName + " Report");
					for (int i = 0; i < pharmacyName.length(); i++) {
						System.out.print("=");
					} //for
					System.out.println("=======");
					System.out.println();
					System.out.println("DRUG NAME        QUANTITY");
					System.out.println(border);
				} //if
				else {
					System.out.println("Error: The pharmacy '" + pharmacyName + "' does not exist, please try again.");
				} //else
				while (rs.next()) {
					String formula = rs.getString(1);
					int totalDrugQty = rs.getInt(2);
					String drugQty = Integer.toString(totalDrugQty);
//				System.out.print(formula + "          " + totalDrugQty);
					System.out.print(formula);
					for (int i = border.length() - 4; i > (formula.length() - drugQty.length()); i--) {
						System.out.print(" ");
					}
					System.out.println(totalDrugQty);
					// for DEBUG
				} // if
				sc.close();
			} // if
			else {
				System.out.println("Error: Pharmacy name cannot be empty");
			}
		} catch (SQLException se) {
			System.out.println("Error: SQL Exception " + se.getMessage());
		} //try-catch
//			} // while
//		} catch (NoSuchElementException e) {
//			System.out.println("Error: " + e.getMessage());
//		} // try-catch
	} //main()

}