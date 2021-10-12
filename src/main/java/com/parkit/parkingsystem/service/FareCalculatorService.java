package com.parkit.parkingsystem.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	/**
	 * Calculate fare in minutes. If the user leave the parking before 30 minutes,
	 * the parking is free. After 30 minutes the user pay normal fare with the first
	 * hour.
	 * 
	 * @author Arnaud
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		// TODO: Some tests are failing here. Need to check if this logic is correct
		long durationTime = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
		double numberOfMinutes = (durationTime / 60) / 1000;

		if (numberOfMinutes > 30) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(numberOfMinutes * Fare.CAR_RATE_PER_MINUTE);
				break;
			}
			case BIKE: {
				ticket.setPrice(numberOfMinutes * Fare.BIKE_RATE_PER_MINUTE);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}
	}
    
	/**
	 * Calculate fare, processing of price to around at 2 decimals
	 * 
	 * @author Arnaud
	 * @param ticket
	 */
	public double priceFormatter(Ticket ticket) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		String priceFormat = df.format(ticket.getPrice());
		return Double.parseDouble(priceFormat);
	}
}