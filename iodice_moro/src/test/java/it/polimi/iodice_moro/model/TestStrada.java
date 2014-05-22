package it.polimi.iodice_moro.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestStrada {
	Strada stradaTest= new Strada(0);

	@Before
	public void setUp() throws Exception {
		stradaTest.setRecinto(false);
	}

	@Test
	public void testCostruttoreStrada() {
		Strada stradaTestInterno = new Strada(2);
		
		//Controlliamo che il valore del numero di casella sia due come indicato nel costruttore
		assertEquals(stradaTestInterno.getnCasella(),2);
		
		//Controlliamo che il valore di recinto sia false come dovrebbe venire inizializzato dal costruttore
		assertFalse(stradaTestInterno.isRecinto());
		
		//Controlliamo che l'istanza creata sia veramente una strada
		assertFalse(stradaTestInterno.isRegione());
	}
	
	@Test
	public void testSetRecinto(){
		
		//Controlliamo che di default la strada non sia recintata
		assertEquals(stradaTest.isRecinto(),false);
		//Settiamo il recinto a true
		stradaTest.setRecinto(true);
		//Controlliamo che ora sia presente il recinto
		assertEquals(stradaTest.isRecinto(),true);
	}
	
	@Test
	public void testGetnCasella(){
		//Controllo il getnCasella()
		assertEquals(stradaTest.getnCasella(),0);
		
	}

}
