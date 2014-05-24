package it.polimi.iodice_moro.controller;

import static org.junit.Assert.*;

import java.util.List;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest {

	final static String PROVA_XML = new String("prova.xml");
	StatoPartita statoPartitaT;
	Giocatore giocatoreTest;
	Controller controllerTest;
	
	Regione regione0, regione1, regione2, regione3, regione4;
	Strada strada0, strada1, strada2, strada3, strada4;
	
	@Before
	public void setUp() throws Exception {
		statoPartitaT= new StatoPartita(PROVA_XML);
		controllerTest = new Controller(statoPartitaT);
		giocatoreTest = new Giocatore("Test");
		
		List<Regione> listaRegioni = statoPartitaT.getRegioni();
		List<Strada> listaStrade = statoPartitaT.getStrade();
		
		regione0=listaRegioni.get(0);
		Regione regione1=listaRegioni.get(1);
		Regione regione2=listaRegioni.get(2);
		Regione regione3=listaRegioni.get(3);
		Regione regione4=listaRegioni.get(4);
		
		strada0=listaStrade.get(0);
		Strada strada1=listaStrade.get(1);
		Strada strada2=listaStrade.get(2);
		Strada strada3=listaStrade.get(3);
		Strada strada4=listaStrade.get(4);
		
		statoPartitaT.setGiocatoreCorrente(giocatoreTest);
	}

	@Test
	public void testCostruttore() {
		assertSame(statoPartitaT, controllerTest.getStatoPartita());
	}
	
	/*
	
	@Test
	public void testSpostaPecora() {
		giocatoreTest.setPosition(position);
		try {
			controllerTest.spostaPecora(regione1);
			assertEquals(2, regione1.getNumPecore());
			assertEquals(1, statoPartita.getAltraRegione(regione1, giocatore.getPosition()).getNumPecore());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	@Test
	public void testSpostaPecoraNera() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcquistaTessera() {
		fail("Not yet implemented");
	}

	@Test
	public void testSpostaPedina() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreaGiocatore() {
		controllerTest.creaGiocatore("Prova", strada0);
		assertEquals("Prova", statoPartitaT.getGiocatori().get(0).getNome());
		assertEquals(strada0, statoPartitaT.getGiocatori().get(0).getPosition());		
	}
	
	*/
	
}