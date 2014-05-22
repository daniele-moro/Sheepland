package it.polimi.iodice_moro.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestStatoPartita {
	final static String PROVA_XML = new String("prova.xml");
	StatoPartita statoPartitaT;
	
	@Before
	public void setUp() throws Exception {
		statoPartitaT= new StatoPartita(PROVA_XML);
	}

	@Test
	public void testCostruttore() {
		
		statoPartitaT= new StatoPartita(PROVA_XML);
		//Controlliamo che il costruttore abbia inizializzato ai valori di default gli attributi della classe
		assertEquals(statoPartitaT.getNumRecinti(),StatoPartita.NUM_RECINTI_MAX);
		assertEquals(statoPartitaT.getPosPecoraNera(),null);
		assertEquals(statoPartitaT.getGiocatoreCorrente(),null);
		assertEquals(statoPartitaT.isTurnoFinale(),false);
		
		//Controlliamo che initTessere funzioni a dovere
		for(TipoTerreno t:TipoTerreno.values()){
			if(!t.toString().equals("sheepsburg")){
					assertEquals(statoPartitaT.getCostoTessera(t),0);
			}
		}
		
		//Controlliamo che abbia caricato tutte le regioni (posso controllare solo che il tipo di regione sia corretto)
		List<Regione> regioni=statoPartitaT.getRegioni();
		assertEquals(regioni.get(0).getTipo().toString(),"pianura");
		assertEquals(regioni.get(1).getTipo().toString(),"sabbia");
		assertEquals(regioni.get(2).getTipo().toString(),"paludi");
		assertEquals(regioni.get(3).getTipo().toString(),"bosco");
		assertEquals(regioni.get(4).getTipo().toString(),"sheepsburg");
		
		//Controlliamo che abbia caricato le strade in modo corretto (posso controllare che il numero di casella sia corretto)
		List<Strada> strade=statoPartitaT.getStrade();
		assertEquals(strade.get(0).getnCasella(),1);
		assertEquals(strade.get(1).getnCasella(),3);
		assertEquals(strade.get(2).getnCasella(),2);
		assertEquals(strade.get(3).getnCasella(),6);
		assertEquals(strade.get(4).getnCasella(),3);
		assertEquals(strade.get(5).getnCasella(),1);
		assertEquals(strade.get(6).getnCasella(),2);
		assertEquals(strade.get(7).getnCasella(),1);
		
		//Rimangono da testare i link
		//Controlliamo link tra regione e strada, controllando che funzioni il metodo getStradeConfini
		List<Strada> strConfini;
		
		//REGIONE id=1
		strConfini=statoPartitaT.getStradeConfini(regioni.get(0));
		assertEquals(strConfini.size(),3);
		assertTrue(strConfini.contains(strade.get(0)));
		assertTrue(strConfini.contains(strade.get(1)));
		assertTrue(strConfini.contains(strade.get(2)));
		
		//REGIONE id=2
		strConfini=statoPartitaT.getStradeConfini(regioni.get(1));
		assertEquals(strConfini.size(),4);
		assertTrue(strConfini.contains(strade.get(0)));
		assertTrue(strConfini.contains(strade.get(3)));
		assertTrue(strConfini.contains(strade.get(4)));
		assertTrue(strConfini.contains(strade.get(5)));
		
		//REGIONE id=3
		strConfini=statoPartitaT.getStradeConfini(regioni.get(2));
		assertEquals(strConfini.size(),2);
		assertTrue(strConfini.contains(strade.get(4)));
		assertTrue(strConfini.contains(strade.get(6)));
		
		//REGIONE id=4
		strConfini=statoPartitaT.getStradeConfini(regioni.get(3));
		assertEquals(strConfini.size(),3);
		assertTrue(strConfini.contains(strade.get(2)));
		assertTrue(strConfini.contains(strade.get(5)));
		assertTrue(strConfini.contains(strade.get(7)));
		
		//REGIONE id=5
		strConfini=statoPartitaT.getStradeConfini(regioni.get(4));
		assertEquals(strConfini.size(),4);
		assertTrue(strConfini.contains(strade.get(1)));
		assertTrue(strConfini.contains(strade.get(3)));
		assertTrue(strConfini.contains(strade.get(6)));
		assertTrue(strConfini.contains(strade.get(7)));
		
		//Controlliamo i link tra strade e strade, controllando che funzioni il metodo getStradeAdiacenti
		List<Strada> strVicine;
		//STRADA id=101
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(0));
		assertEquals(strVicine.size(),2);
		assertTrue(strVicine.contains(strade.get(1)));
		assertTrue(strVicine.contains(strade.get(3)));
		
		//STRADA id=102
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(1));
		assertEquals(strVicine.size(),4);
		assertTrue(strVicine.contains(strade.get(0)));
		assertTrue(strVicine.contains(strade.get(3)));
		assertTrue(strVicine.contains(strade.get(2)));
		assertTrue(strVicine.contains(strade.get(6)));
		
		//STRADA id=103
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(2));
		assertEquals(strVicine.size(),2);
		assertTrue(strVicine.contains(strade.get(1)));	
		assertTrue(strVicine.contains(strade.get(4)));

		//STRADA id=104
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(3));
		assertEquals(strVicine.size(),4);
		assertTrue(strVicine.contains(strade.get(0)));
		assertTrue(strVicine.contains(strade.get(1)));
		assertTrue(strVicine.contains(strade.get(4)));
		assertTrue(strVicine.contains(strade.get(6)));
		
		//STRADA id=105
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(4));
		assertEquals(strVicine.size(),4);
		assertTrue(strVicine.contains(strade.get(2)));
		assertTrue(strVicine.contains(strade.get(3)));
		assertTrue(strVicine.contains(strade.get(5)));
		assertTrue(strVicine.contains(strade.get(6)));
		
		//STRADA id=106
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(5));
		assertEquals(strVicine.size(),2);
		assertTrue(strVicine.contains(strade.get(4)));
		assertTrue(strVicine.contains(strade.get(6)));
		
		//STRADA id=107
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(6));
		assertEquals(strVicine.size(),5);
		assertTrue(strVicine.contains(strade.get(3)));
		assertTrue(strVicine.contains(strade.get(4)));
		assertTrue(strVicine.contains(strade.get(5)));
		assertTrue(strVicine.contains(strade.get(7)));
		assertTrue(strVicine.contains(strade.get(1)));
		
		//STRADA id=108
		strVicine = statoPartitaT.getStradeAdiacenti(strade.get(7));
		assertEquals(strVicine.size(),1);
		assertTrue(strVicine.contains(strade.get(6)));
		
		//FINE TEST COSTRUTTORE
	}
	
	@Test
	public void testGetAltraRegione(){
		//Controlliamo che il metodo getAltraRegione funzioni correttamente
		Strada str=statoPartitaT.getStrade().get(0);
		Regione regSorg=statoPartitaT.getRegioni().get(0);
		Regione regDest=statoPartitaT.getRegioni().get(1);
		
		//Controlliamo che l'altra regione che stiamo cercando sia quella giusta
		assertEquals(statoPartitaT.getAltraRegione(regSorg, str),regDest);
		
		//Controlliamo che l'altra regione che stiamo cercando non sia quella che usiamo come parametro
		assertFalse(statoPartitaT.getAltraRegione(regSorg, str).equals(regSorg));	
	}
	
	@Test
	public void testGetRegioniAdiacenti(){
		//Controlliamo che il metodo getRegioniAdiacenti funzioni correttamente
		List<Regione> regioniAdiacenti= new ArrayList<Regione>();
		List<Regione> regioni=statoPartitaT.getRegioni();
		//Ci costruiamo l'array con le presunte regioni adiacenti alla regione id=1
		regioniAdiacenti.add(regioni.get(1));
		regioniAdiacenti.add(regioni.get(4));
		regioniAdiacenti.add(regioni.get(3));
		
		assertEquals(statoPartitaT.getRegioniAdiacenti(regioni.get(0)).size(),regioniAdiacenti.size());
		List<Regione> regad= statoPartitaT.getRegioniAdiacenti(regioni.get(0));
		assertTrue(regad.containsAll(regioniAdiacenti));
	}
	
	@Test
	public void testDecNumRecinti(){
		//Controlliamo che decNumRecinti funzioni correttamente
		statoPartitaT.decNumRecinti();
		assertEquals(statoPartitaT.getNumRecinti(),StatoPartita.NUM_RECINTI_MAX-1);
	}
	
	@Test
	public void testSetTurnoFinale(){
		//Controlliamo che setTurnoFinale funzioni correttamente
		statoPartitaT.setTurnoFinale();
		assertEquals(statoPartitaT.isTurnoFinale(),true);
	}
	
	@Test
	public void testSetPosPecoraNera(){
		//Controlliamo che setPosPecoraNera funzioni correttamente
		statoPartitaT.setPosPecoraNera(statoPartitaT.getRegioni().get(0));
		assertEquals(statoPartitaT.getPosPecoraNera(),statoPartitaT.getRegioni().get(0));
	}
	
	@Test
	public void testSetGiocatoreCorrente(){
		//Controlliamo che setGiocatoreCorrente funzioni correttamente
		Giocatore gamer= new Giocatore("Prova");
		statoPartitaT.setGiocatoreCorrente(gamer);
		assertEquals(statoPartitaT.getGiocatoreCorrente(),gamer);
	}
	
	@Test
	public void testAddGiocatore(){
		//Controlliamo che addGiocatore funzioni correttamente
		Giocatore gamer1 = new Giocatore("Prova1");
		Giocatore gamer2 = new Giocatore("Prova2");
		List<Giocatore> listaGamer=new ArrayList<Giocatore>();
		listaGamer.add(gamer1);
		listaGamer.add(gamer2);
		statoPartitaT.addGiocatore(gamer1);
		statoPartitaT.addGiocatore(gamer2);
		assertEquals(statoPartitaT.numGiocatori(),listaGamer.size());
		assertTrue(statoPartitaT.getGiocatori().containsAll(listaGamer));
	}
	
	@Test
	public void testIncCostoTessera(){
		//Controlliamo che incCostoTessera funzioni correttamente
		statoPartitaT.incCostoTessera(TipoTerreno.BOSCO);
		assertEquals(statoPartitaT.getCostoTessera(TipoTerreno.BOSCO),1);
		
		statoPartitaT.incCostoTessera(TipoTerreno.COLTIVAZIONI);
		assertEquals(statoPartitaT.getCostoTessera(TipoTerreno.COLTIVAZIONI),1);

	}
	
	
	
	
}
