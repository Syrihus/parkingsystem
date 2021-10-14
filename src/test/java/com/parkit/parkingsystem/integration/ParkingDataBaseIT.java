package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static Ticket ticket; 

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
	}

	
	@Test
	public void testParkingACar() {
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		//WHEN
		parkingService.processIncomingVehicle();
		//THEN
		Assertions.assertEquals(1, ticketDAO.getTicket("ABCDEF").getId());
		Assertions.assertEquals(false, ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable());
	}

    @Test
    public void testParkingLotExit(){
       
    	//WHEN
    	testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        //TODO: check that the fare generated and out time are populated correctly in the database
        Assertions.assertEquals(0, ticketDAO.getTicket("ABCDEF").getPrice());
        Assertions.assertNotEquals(null, ticketDAO.getTicket("ABCDEF").getOutTime());
    }
//	@Test
//	public void testParkingLotExit() {
//		testParkingACar();
//		// GIVEN
//		
//		Date exitDate = new Date(); 
//		exitDate.setTime(exitDate.getTime() + (120*60*1000));
//		System.out.println("trace 1 " + exitDate.getTime());
//		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//		//WHEN
//		parkingService.processExitingVehicle(exitDate);
//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
////		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
////        String strDate = dateFormat.format(exitDate);  
////        String strDate2 = dateFormat.format(ticketDAO.getTicket("ABCDEF").getOutTime());
//        //THEN
//		Timestamp ts = new Timestamp(exitDate.getTime()); 
//        Assertions.assertEquals(ts. getTime(),ticketDAO.getTicket("ABCDEF").getOutTime().getTime()); 
//		Assertions.assertEquals(3d, ticketDAO.getTicket("ABCDEF").getPrice());
//	}
	
	@Test 
	public void testSaveTicket() {
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		//WHEN
		parkingService.processIncomingVehicle();
		//THEN
		Assertions.assertEquals(false, ticketDAO.saveTicket(new Ticket()));
	}


	
	@Test
	public void testCloseConnection() throws ClassNotFoundException, SQLException {
		//GIVEN
		DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();  
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO); 
		//WHEN
		parkingService.processIncomingVehicle();
		//THEN
		Assertions.assertNotNull(dataBaseTestConfig.getConnection().isClosed());
	}
	
}
