package it.polimi.iodice_moro.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestRegione {
	Regione regioneTest= new Regione("SABBIA");

	@Before
	public void setUp() throws Exception {
		regioneTest.setPecoraNera(false);
		regioneTest.setNumPecore(2);
		
	}

	@Test
	public void testCostrutore() {
		//Controlliamo che il costruttore inizializzi correttamente gli attributi della classe
		Regione regioneTestI= new Regione("SHEEPSBURG");
		assertEquals(regioneTestI.getNumPecore(),0);
		assertEquals(regioneTestI.getTipo(),TipoTerreno.SHEEPSBURG);
		assertEquals(regioneTestI.isPecoraNera(),false);
	}

	@Test
	public void testSetPecoraNera() {
		//Controlliamo il metodo setPecoraNera
		assertEquals(regioneTest.isPecoraNera(),false);
		regioneTest.setPecoraNera(false);
		assertEquals(regioneTest.isPecoraNera(),false);
		regioneTest.setPecoraNera(true);
		assertEquals(regioneTest.isPecoraNera(),true);
	}

	@Test
	public void testSetNumPecore() {
		//Controlliamo il metodo setNumPecore
		regioneTest.setNumPecore(3);
		assertEquals(regioneTest.getNumPecore(),3);
		regioneTest.setNumPecore(0);
		assertEquals(regioneTest.getNumPecore(),0);
	}

	@Test
	public void testAddPecora() {
		//Controlliamo il metodo addPecora
		regioneTest.addPecora();
		assertEquals(regioneTest.getNumPecore(),3);
		regioneTest.addPecora();
		regioneTest.addPecora();
		assertEquals(regioneTest.getNumPecore(),5);
	}

	@Test
	public void testRemovePecora() {
		//Controlliamo il metodo removePecora
		regioneTest.removePecora();
		assertEquals(regioneTest.getNumPecore(),1);
		regioneTest.removePecora();
		regioneTest.removePecora();
		assertEquals(regioneTest.getNumPecore(),-1);
		
	}

	@Test
	public void testRemovePecoraNera() {
		//Controlliamo il metodo removePecoraNera
		regioneTest.removePecoraNera();
		assertEquals(regioneTest.isPecoraNera(),false);
		//Controlliamo che non venga negato il valore presente nell'attributo interno alla classe
		regioneTest.addPecoraNera();
		regioneTest.removePecoraNera();
		regioneTest.removePecoraNera();
		assertEquals(regioneTest.isPecoraNera(),false);
	}

	@Test
	public void testAddPecoraNera() {
		regioneTest.addPecoraNera();
		assertEquals(regioneTest.isPecoraNera(),true);
		//Controlliamo che non venga negato il valore presente nell'attributo della classe
		regioneTest.addPecoraNera();
		regioneTest.addPecoraNera();
		assertEquals(regioneTest.isPecoraNera(),true);
	}
	
	@Test
	public void testIsRegione() {
		assertEquals(regioneTest.isRegione(),true);
	}

}
