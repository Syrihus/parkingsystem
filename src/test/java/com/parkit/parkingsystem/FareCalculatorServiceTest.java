package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }


    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
 
    	//GIVEN
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
//        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR); 
        
    }

    @Test
    public void calculateFareBike(){
        
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
//        assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR); 
    }

    @Test
    public void calculateFareUnkownType(){
        
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //THEN
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        
    	//GIVEN 
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        //THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * 45 minutes parking time should give 3/4th parking fare
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
//        assertThat(ticket.getPrice()).isEqualTo((0.75 * Fare.BIKE_RATE_PER_HOUR)); 
    }

    /**
     * 45 minutes parking time should give 3/4th parking fare
     */
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        //GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
//        assertThat(ticket.getPrice()).isEqualTo((0.75 * Fare.CAR_RATE_PER_HOUR)); 
    }

    /**
     * 24 hours parking time should give 24 * parking fare per hour
     */
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (6 * 24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals( (6 * 24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
//        assertThat(ticket.getPrice()).isEqualTo((6 * 24 * Fare.CAR_RATE_PER_HOUR)); 
    }
    
    @Test
    public void calculateFareCarForThirtyMinutes() {
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (30 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals( (0 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
//        assertThat(ticket.getPrice()).isEqualTo((6 * 24 * Fare.CAR_RATE_PER_HOUR)); 
    }
    
    @Test
    public void calculateFareBikeForThirtyMinutes() {
    	//GIVEN
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (30 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        //THEN
        assertEquals( (0 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
//        assertThat(ticket.getPrice()).isEqualTo((6 * 24 * Fare.CAR_RATE_PER_HOUR)); 
    }
    
    @Test 
    public void verifyFormatter() {
    	//GIVEN
    	ticket.setPrice(1.317);
    	//WHEN
    	ticket.setPrice(fareCalculatorService.priceFormatter(ticket));
    	//THEN
    	assertEquals(1.32, ticket.getPrice()); 
//    	assertThat(ticket.getPrice()).isEqualTo(1.32); 
    }
}