package it.polimi.iodice_moro.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest {

	StatoPartita statoPartitaT;
	Giocatore giocatoreTest;
	Controller controllerTest;
	
	Regione regione0, regione1, regione2, regione3, regione4;
	Strada strada0, strada1, strada2, strada3, strada4;
	
	@Before
	public void setUp() throws Exception {
		statoPartitaT= new StatoPartita();
		controllerTest = new Controller(statoPartitaT);
		
		List<Regione> listaRegioni = statoPartitaT.getRegioni();
		List<Strada> listaStrade = statoPartitaT.getStrade();
		
		regione0=listaRegioni.get(0);
		regione1=listaRegioni.get(1);
		regione2=listaRegioni.get(2);
		regione3=listaRegioni.get(3);
		regione4=listaRegioni.get(15);
		
		strada0=listaStrade.get(0);
		strada1=listaStrade.get(1);
		strada2=listaStrade.get(2);
		strada3=listaStrade.get(3);
		strada4=listaStrade.get(4);
		
		controllerTest.creaGiocatore("Prova", statoPartitaT.getStradeConfini(regione1).get(0));
		statoPartitaT.setGiocatoreCorrente(statoPartitaT.getGiocatori().get(0));
		giocatoreTest=statoPartitaT.getGiocatoreCorrente();
		regione1.setNumPecore(2);
	}

	@Test
	public void testCostruttore() {
		assertSame(statoPartitaT, controllerTest.getStatoPartita());
	}
	
	
	
	@Test
	public void testSpostaPecora() throws Exception {
		
		int numOfPecoreBefore=regione1.getNumPecore();
		controllerTest.spostaPecora(regione1);
		assertEquals(numOfPecoreBefore-1, regione1.getNumPecore());
		assertEquals(1, statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()).getNumPecore());

		controllerTest.spostaPecora(statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()));
		assertEquals(numOfPecoreBefore, regione1.getNumPecore());
		assertEquals(0, statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()).getNumPecore());
	}
	
	@Test
	public void testSpostaPecoraWithException() {
		giocatoreTest.setPosition(statoPartitaT.getStradeConfini(regione4).get(0));
		try {
			controllerTest.spostaPecora(regione1);
			fail("Should have thrown exception");
		}
		
		catch (Exception e) {
			
		}
	}

	@Test
	public void testSpostaPecoraNera() throws Exception {
		statoPartitaT.setPosPecoraNera(regione1);
		regione1.addPecoraNera();
		assertTrue(regione1.isPecoraNera());
		assertEquals(regione1, statoPartitaT.getPosPecoraNera());
		
		controllerTest.spostaPecoraNera(regione1, statoPartitaT.getRegioniAdiacenti(regione1).get(0));
		assertFalse(regione1.isPecoraNera());
		assertTrue(statoPartitaT.getRegioniAdiacenti(regione1).get(0).isPecoraNera());
		
		controllerTest.spostaPecoraNera(statoPartitaT.getRegioniAdiacenti(regione1).get(0), regione1);
		assertFalse(statoPartitaT.getRegioniAdiacenti(regione1).get(0).isPecoraNera());
		assertTrue(regione1.isPecoraNera());
		
	}
	
	@Test
	
	public void testSpostaPecoraNeraWithException() {
		try {
			controllerTest.spostaPecoraNera(regione1, regione1);
			fail("Should have thrown an exception");
		}
		
		catch (Exception e) {
			
		}
	}
	

	@Test
	public void testAcquistaTessera() throws Exception {
		TipoTerreno tipo1=regione1.getTipo();
		int soldiIniziali=giocatoreTest.getSoldi();
		
		assertEquals(0, statoPartitaT.getCostoTessera(tipo1));
		controllerTest.acquistaTessera(tipo1);
		assertEquals(soldiIniziali, giocatoreTest.getSoldi());
		
		controllerTest.acquistaTessera(tipo1);
		controllerTest.acquistaTessera(tipo1);
		assertEquals(soldiIniziali-1-2,giocatoreTest.getSoldi());
	}
	
	@Test
	public void testAcquistaTesseraWithNotEnoughMoneyException() {
		TipoTerreno tipo1=regione1.getTipo();
		giocatoreTest.decrSoldi(giocatoreTest.getSoldi());
		try {
			controllerTest.acquistaTessera(tipo1);
			fail("Should have thrown exception");
		}
		catch (Exception e) {
			
		}
	}
	
	@Test
	public void testAcquistaTesseraWhichCantBeBought() {
		TipoTerreno tipo1=regione1.getTipo();
		for(int i=0; i<5; i++) {
			statoPartitaT.incCostoTessera(tipo1);
		}
		try {
			controllerTest.acquistaTessera(tipo1);
			fail("Should have thrown exception");
		}
		catch (Exception e) {
			
		}
		
	}
	

	@Test
	public void testSpostaPedina() throws Exception {
		Strada giocatorePositionBefore = giocatoreTest.getPosition();
		Strada giocatorePositionAfterFirst = statoPartitaT.getStradeAdiacenti(giocatorePositionBefore).get(0);
		int soldiBefore = giocatoreTest.getSoldi();
		controllerTest.spostaPedina(giocatorePositionAfterFirst);
		//Controllo che recinto sia statto messo, posizione del giocatore sia quella giusta
		//e che soldi non siano diminuiti.
		assertTrue(giocatorePositionBefore.isRecinto());
		assertEquals(giocatorePositionAfterFirst, giocatoreTest.getPosition());
		assertEquals(soldiBefore, giocatoreTest.getSoldi());
		
		
		Strada giocatorePositionAfterSecond = statoPartitaT.getStradeConfini(regione4).get(0);
		controllerTest.spostaPedina(giocatorePositionAfterSecond);
		//Controllo che recinto sia statto messo, posizione del giocatore sia quella giusta
		//e che soldi siano diminuiti.
		assertTrue(giocatorePositionAfterFirst.isRecinto());
		assertEquals(giocatorePositionAfterSecond, giocatoreTest.getPosition());
		assertEquals(soldiBefore-1, giocatoreTest.getSoldi());
		
	}

	@Test
	public void TestaSpostaPedinaWithException() {
		Strada giocatorePositionBefore = giocatoreTest.getPosition();
		Strada giocatorePositionAfterFirst = statoPartitaT.getStradeAdiacenti(giocatorePositionBefore).get(0);
		giocatorePositionAfterFirst.setRecinto(true);
		try {
			controllerTest.spostaPedina(giocatorePositionAfterFirst);
			fail("Should have caught exception");
		}
		catch (Exception e) {
			
		}
		
		giocatoreTest.decrSoldi((giocatoreTest.getSoldi()));
		Strada giocatorePositionAfterSecond = statoPartitaT.getStradeConfini(regione4).get(0);
		try {
			controllerTest.spostaPedina(giocatorePositionAfterSecond);
			fail("Should have caught the second exception");
		}
		catch (Exception e){
			
		}
		
	}	

	@Test
	public void testCreaGiocatore() {
		controllerTest.creaGiocatore("Prova", strada0);
		assertEquals("Prova", statoPartitaT.getGiocatori().get(0).getNome());
		assertEquals(strada0, statoPartitaT.getGiocatori().get(0).getPosition());		
	}
	
	@Test
	public void testCheckSpostamentoNera() {
		
	}	
	
	
}