package it.polimi.iodice_moro.model;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestGiocatore {
	Giocatore gamerTest;

	@Before
	public void setUp() throws Exception {
		//Inizializziamo l'ambiente per un nuovo test
		gamerTest = new Giocatore("test");
	}

	@Test
	public void testGiocatoreString() throws RemoteException {
		//Test cotruttore Giocatore(String nome)
		Giocatore gamerTestI= new Giocatore("prova");
		
		assertEquals(gamerTestI.getNome(),"prova");
		assertEquals(gamerTestI.isPastoreSpostato(),false);
		assertEquals(gamerTestI.getNumMosse(),0);
		assertEquals(gamerTestI.getUltimaMossa(),TipoMossa.NO_MOSSA);
		assertEquals(gamerTestI.getSoldi(),Giocatore.SOLDI_INIT);
		assertEquals(gamerTestI.getPosition(),null);
		
		//Controlliamo che le tessere siano state inizializzate correttamente
		Map<String,Integer> tesserePoss=gamerTestI.getTesserePossedute();
		for(TipoTerreno t:TipoTerreno.values()){
			if(!t.toString().equals("sheepsburg")){
					assertEquals(tesserePoss.get(t.toString()),Integer.valueOf(0));
					}
			}
	}

	@Test
	public void testGiocatoreStringStrada() throws RemoteException {
		//TestCostruttore Giocatore(String nome, Strada strada)
		Strada pos= new Strada(0);
		Giocatore gamerTestI = new Giocatore("prova2",pos);
		
		assertEquals(gamerTestI.getNome(),"prova2");
		assertEquals(gamerTestI.isPastoreSpostato(),false);
		assertEquals(gamerTestI.getNumMosse(),0);
		assertEquals(gamerTestI.getUltimaMossa(),TipoMossa.NO_MOSSA);
		assertEquals(gamerTestI.getSoldi(),Giocatore.SOLDI_INIT);
		assertEquals(gamerTestI.getPosition(),pos);
		
		//Controlliamo che le tessere siano state inizializzate correttamente
		Map<String,Integer> tesserePoss=gamerTestI.getTesserePossedute();
		for(TipoTerreno t:TipoTerreno.values()){
			if(!t.toString().equals("sheepsburg")){
					assertEquals(tesserePoss.get(t.toString()),Integer.valueOf(0));
					}
			}
		
	}

	@Test
	public void testDecrSoldi() {
		//Test decrSoldi()
		gamerTest.decrSoldi();
		assertEquals(gamerTest.getSoldi(),Giocatore.SOLDI_INIT-1);
		gamerTest.decrSoldi();
		gamerTest.decrSoldi();
		assertEquals(gamerTest.getSoldi(),Giocatore.SOLDI_INIT-3);
		
	}

	@Test
	public void testDecrSoldiInt() {
		//Test decrSoldi(int)
		gamerTest.decrSoldi(3);
		assertEquals(gamerTest.getSoldi(),Giocatore.SOLDI_INIT-3);
		gamerTest.decrSoldi(Giocatore.SOLDI_INIT);
		assertEquals(gamerTest.getSoldi(),-3);
	}

	@Test
	public void testSetUltimaMossa() {
		//Test setUltimaMossa(TipoMossa mossa)
		gamerTest.setUltimaMossa(TipoMossa.COMPRA_TESSERA);
		assertEquals(gamerTest.getUltimaMossa(), TipoMossa.COMPRA_TESSERA);
		
		gamerTest.setUltimaMossa(TipoMossa.NO_MOSSA);
		assertEquals(gamerTest.getUltimaMossa(),TipoMossa.NO_MOSSA);
		
	}

	@Test
	public void testIncNumMosse() {
		//Test incNumMosse()
		gamerTest.incNumMosse();
		assertEquals(gamerTest.getNumMosse(),1);
		gamerTest.incNumMosse();
		gamerTest.incNumMosse();
		gamerTest.incNumMosse();
		assertEquals(gamerTest.getNumMosse(),4);
	}

	@Test
	public void testSetPastoreSpostato() {
		//Test setSpastoreSpostato()
		gamerTest.setPastoreSpostato(true);
		assertEquals(gamerTest.isPastoreSpostato(),true);
		
		gamerTest.setPastoreSpostato(false);
		gamerTest.setPastoreSpostato(false);
		assertEquals(gamerTest.isPastoreSpostato(),false);
		
		gamerTest.setPastoreSpostato(true);
		gamerTest.setPastoreSpostato(true);
		assertEquals(gamerTest.isPastoreSpostato(),true);
		
		
	}

	@Test
	public void testSetPosition() {
		//Test setPosition(Strada str)
		//Variabili di test
		Strada pos1= new Strada(1);
		Strada pos2= new Strada(6);
		
		gamerTest.setPosition(pos1);
		assertEquals(gamerTest.getPosition(), pos1);
		
		gamerTest.setPosition(pos2);
		assertEquals(gamerTest.getPosition(),pos2);
	}

	@Test
	public void testAddTessera() {
		//Test addTessera()
		gamerTest.addTessera(TipoTerreno.COLTIVAZIONI);
		assertEquals(gamerTest.getTesserePossedute().get(TipoTerreno.COLTIVAZIONI.toString()),Integer.valueOf(1));
		
		gamerTest.addTessera(TipoTerreno.COLTIVAZIONI);
		assertEquals(gamerTest.getTesserePossedute().get(TipoTerreno.COLTIVAZIONI.toString()),Integer.valueOf(2));
		
		gamerTest.addTessera(TipoTerreno.SABBIA);
		assertEquals(gamerTest.getTesserePossedute().get(TipoTerreno.SABBIA.toString()),Integer.valueOf(1));
		
		gamerTest.addTessera(TipoTerreno.SHEEPSBURG);
		assertEquals(gamerTest.getTesserePossedute().get(TipoTerreno.SHEEPSBURG.toString()),Integer.valueOf(1));
		
	}
	
	@Test
	public void testAzzeraTurno(){
		//Test azzeraTurno()
		gamerTest.incNumMosse();
		gamerTest.setUltimaMossa(TipoMossa.COMPRA_TESSERA);
		gamerTest.setPastoreSpostato(true);
		gamerTest.azzeraTurno();
		assertEquals(gamerTest.getNumMosse(),0);
		assertEquals(gamerTest.getUltimaMossa(),TipoMossa.NO_MOSSA);
		assertFalse(gamerTest.isPastoreSpostato());
		
	}
	
	@Test
	public void testTreMosse(){
		//Controllo che all'inizio, quando non ha fatto mosse, sia il metod.o torni false
		assertFalse(gamerTest.treMosse());
		gamerTest.incNumMosse();
		//Controllo che dopo la prima mossa torni ancora false
		assertFalse(gamerTest.treMosse());
		
		gamerTest.incNumMosse();
		gamerTest.incNumMosse();
		//Dopo tre mosse il metod.o torna true
		assertTrue(gamerTest.treMosse());
	}

}
