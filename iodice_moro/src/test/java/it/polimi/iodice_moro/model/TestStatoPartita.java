package it.polimi.iodice_moro.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TestStatoPartita {
	final static String PROVA_XML = new String("prova.xml");
	StatoPartita statoPartitaT = StatoPartita.getInstance(PROVA_XML);
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCostruttore() {
		
		//Controlliamo che abbia caricato tutte le regioni (posso controllare solo che il tipo di regione sia corretto)
		ArrayList<Regione> regioni=statoPartitaT.getRegioni();
		assertEquals(regioni.get(0).getTipo().toString(),"pianura");
		assertEquals(regioni.get(1).getTipo().toString(),"sabbia");
		assertEquals(regioni.get(2).getTipo().toString(),"paludi");
		assertEquals(regioni.get(3).getTipo().toString(),"bosco");
		assertEquals(regioni.get(4).getTipo().toString(),"sheepsburg");
		
		//Controlliamo che abbia caricato le strade in modo corretto (posso controllare che il numero di casella sia corretto)
		ArrayList<Strada> strade=statoPartitaT.getStrade();
		assertEquals(strade.get(0).getnCasella(),1);
		assertEquals(strade.get(1).getnCasella(),3);
		assertEquals(strade.get(2).getnCasella(),2);
		assertEquals(strade.get(3).getnCasella(),6);
		assertEquals(strade.get(4).getnCasella(),3);
		assertEquals(strade.get(5).getnCasella(),1);
		assertEquals(strade.get(6).getnCasella(),2);
		assertEquals(strade.get(7).getnCasella(),1);
		
		//Rimangono da testare i link
	}
	
	@Test
	public void testGetAltraRegione(){
		
	}
	
}
