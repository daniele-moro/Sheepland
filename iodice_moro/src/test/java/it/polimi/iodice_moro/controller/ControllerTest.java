package it.polimi.iodice_moro.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest{

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
	public void testCostruttore() throws RemoteException {
			assertSame(statoPartitaT, controllerTest.getStatoPartita());
	}
	
	@Test
	public void testCostruttoreDue() throws RemoteException {
		IFView viewProva = new View(controllerTest);
		Controller controllerTestDue = new Controller(viewProva);
	}
	
	@Test
	public void testAggiungiRecinto() throws RemoteException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class myTarget = Controller.class;
		Class params[] = new Class[1];
		params[0] = Strada.class;
		Method aggiungiRecinto = myTarget.getDeclaredMethod("aggiungiRecinto", params[0]);
		aggiungiRecinto.setAccessible(true);
		
		aggiungiRecinto.invoke(controllerTest, strada0);
		assertTrue(strada0.isRecinto());
		assertFalse(strada1.isRecinto());
	}
	
	
	@Test
	public void testSpostaPecora() throws RemoteException, NotAllowedMoveException {
		
		int numOfPecoreBefore=regione1.getNumPecore();
		controllerTest.spostaPecora(regione1);
		//Controllo che numero di pecore sia diminuito nella regione d'origine.
		assertEquals(numOfPecoreBefore-1, regione1.getNumPecore());
		//Controllo che numero di pecore sia aumentato nella regione d'arrivo.
		assertEquals(1, statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()).getNumPecore());

		//Faccio l'operazione contraria e controllo se numero di pecore è quello d'origine.
		controllerTest.spostaPecora(statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()));
		assertEquals(numOfPecoreBefore, regione1.getNumPecore());
		assertEquals(0, statoPartitaT.getAltraRegione(regione1, giocatoreTest.getPosition()).getNumPecore());
	}
	
	@Test
	public void testSpostaPecoraWithException() {
		giocatoreTest.setPosition(statoPartitaT.getStradeConfini(regione4).get(0));
		//Giocatore è in posizione dove non può spostare pecora (non adiacente alla regione)
		//Deve essere lanciata eccezione.
		try {
			controllerTest.spostaPecora(regione1);
			fail("Should have thrown exception");
		} catch (NotAllowedMoveException e) {
			
		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
	}

	@Test
	public void testSpostaPecoraNera() throws Exception {
		statoPartitaT.setPosPecoraNera(regione1);
		regione1.addPecoraNera();
		assertTrue(regione1.isPecoraNera());
		assertEquals(regione1, statoPartitaT.getPosPecoraNera());
		
		//Controllo che nella regione d'origine non c'è pecora nera e controllo
		//che nella regione d'arrivo non c'è.
		controllerTest.spostaPecoraNera(regione1, statoPartitaT.getRegioniAdiacenti(regione1).get(0));
		assertFalse(regione1.isPecoraNera());
		assertTrue(statoPartitaT.getRegioniAdiacenti(regione1).get(0).isPecoraNera());
		
		//Faccio il contrario.
		controllerTest.spostaPecoraNera(statoPartitaT.getRegioniAdiacenti(regione1).get(0), regione1);
		assertFalse(statoPartitaT.getRegioniAdiacenti(regione1).get(0).isPecoraNera());
		assertTrue(regione1.isPecoraNera());
		
	}
	
	@Test
	
	public void testSpostaPecoraNeraWithException() {
		try {
			//Provo a spostare una pecora nera in una regione che non c'è.
			//Dovrebbe lanciare eccezione.
			regione1.setPecoraNera(false);
			controllerTest.spostaPecoraNera(regione1, regione4);
			fail("Should have thrown an exception");
		} catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
	}
	
	public void testSpostaLupo() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class myTarget = Controller.class;
		Class params[] = new Class[2];
		params[0] = Regione.class;
		params[1] = Regione.class;
		Method spostaLupo = myTarget.getDeclaredMethod("spostaLupo", params);
		spostaLupo.setAccessible(true);
		
		//Aggiungo il lupo alla regione.
		regione0.addLupo();
		
		spostaLupo.invoke(controllerTest, regione0);
		//Controllo che il lupo sia stato spostato in una delle regioni adiacenti e che
		//ce ne sia soltano uno.
		Boolean lupoPresente = false;
		int numeroLupo = 0;
		for(Regione regione : statoPartitaT.getRegioniAdiacenti(regione0)) {
			if(regione.isLupo()) {
				lupoPresente = true;
				numeroLupo++;
			}
		}
		assertTrue(lupoPresente);
		assertEquals(1, numeroLupo);
		assertFalse(regione0.isLupo());
	}
	

	@Test
	public void testAcquistaTessera() throws Exception {
		TipoTerreno tipo1=regione1.getTipo();
		int soldiIniziali=giocatoreTest.getSoldi();
		
		//Controllo che se nessuna tessera è stata acquistata il costo è zero.
		assertEquals(0, statoPartitaT.getCostoTessera(tipo1));
		controllerTest.acquistaTessera(tipo1);
		//Controllo che comprando una tessera il cui costo è zero i soldi del 
		//giocatore non diminuiscano.
		assertEquals(soldiIniziali, giocatoreTest.getSoldi());
		
		controllerTest.acquistaTessera(tipo1);
		controllerTest.acquistaTessera(tipo1);
		//Controllo che i soldi del giocatore siano diminuiti di un valore pari al
		//costo delle tessere acquistate.
		assertEquals(soldiIniziali-1-2,giocatoreTest.getSoldi());
	}
	
	@Test
	public void testAcquistaTesseraWithNotEnoughMoneyException() throws RemoteException{
		TipoTerreno tipo1=regione1.getTipo();
		//Forzo l'incremento del costo della tessera
		controllerTest.getStatoPartita().incCostoTessera(tipo1);
		controllerTest.getStatoPartita().incCostoTessera(tipo1);


		giocatoreTest.decrSoldi(giocatoreTest.getSoldi()-1);
		//Provo ad acquistare tessere quando il giocatore non ha abbastanza soldi per comprare la tessera
		try {
			controllerTest.acquistaTessera(tipo1);
			fail("Should have thrown exception");
		}
		catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
		
		
		giocatoreTest.decrSoldi(1);
		//Provo ad acquistare tessere quando il giocatore ha 0 soldi
		try {
			controllerTest.acquistaTessera(tipo1);
			fail("Should have thrown exception");
		} catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
		

	}
	
	@Test
	public void testAcquistaTesseraWhichCantBeBought() throws RemoteException {
		TipoTerreno tipo1=regione1.getTipo();
		
		//Provo ad acquistare tessera di sheepsburg.
		try {
			controllerTest.acquistaTessera(TipoTerreno.SHEEPSBURG);
			fail("Should have thrown exception");
		} catch (NotAllowedMoveException e) {
			
		}
		
		for(int i=0; i<5; i++) {
			statoPartitaT.incCostoTessera(tipo1);
		}
		//Provo ad acquistare un tipo di tessera che è già esaurito.
		try {
			controllerTest.acquistaTessera(tipo1);
			fail("Should have thrown exception");
		}
		catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
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
		
		//Test per verificare che quando finiscono i recinti, viene settato il turnoFinale
		int numRec=statoPartitaT.getNumRecinti();
		for(int i=1; i<numRec; i++){
			statoPartitaT.decNumRecinti();
		}
		
		Strada giocatorePositionAfterSecond = statoPartitaT.getStradeConfini(regione4).get(0);
		controllerTest.spostaPedina(giocatorePositionAfterSecond);
		//Controllo che recinto sia statto messo, posizione del giocatore sia quella giusta
		//e che soldi siano diminuiti.
		assertTrue(giocatorePositionAfterFirst.isRecinto());
		assertEquals(giocatorePositionAfterSecond, giocatoreTest.getPosition());
		assertEquals(soldiBefore-1, giocatoreTest.getSoldi());
		assertTrue(statoPartitaT.isTurnoFinale());
	}

	@Test
	public void TestaSpostaPedinaWithException() {
		Strada giocatorePositionBefore = giocatoreTest.getPosition();
		Strada giocatorePositionAfterFirst = statoPartitaT.getStradeAdiacenti(giocatorePositionBefore).get(0);
		giocatorePositionAfterFirst.setRecinto(true);
		//Provo a spostare pastore in una strada occupata da recinto. 
		try {
			controllerTest.spostaPedina(giocatorePositionAfterFirst);
			fail("Should have caught exception");
		}
		catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
		
		giocatoreTest.decrSoldi((giocatoreTest.getSoldi()));
		Strada giocatorePositionAfterSecond = statoPartitaT.getStradeConfini(regione4).get(0);
		//Provo a spostare pastore in una strada non adiacente alla posizione del
		//giocatore, quando il giocatore non ha soldi.
		try {
			controllerTest.spostaPedina(giocatorePositionAfterSecond);
			fail("Should have caught the second exception");
		}
		catch (NotAllowedMoveException e) {

		} catch (RemoteException e) {
			fail("Problemi di rete, "+"Messaggio: "+e.getMessage());
		}
		
	}	

	@Test
	public void testCreaGiocatore() throws RemoteException {
		controllerTest.creaGiocatore("Prova", strada0);

		assertEquals("Prova", statoPartitaT.getGiocatori().get(0).getNome());
		assertEquals(strada0, statoPartitaT.getGiocatori().get(0).getPosition());
	}
	
	@Test
	public void testAggiornaTurno() throws RemoteException {
		int numMossePrima = giocatoreTest.getNumMosse();
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);

		//Controllo che dopo aver comprato la tessera si sia aggiornato l'attributo 
		//ultima mossa, sia aumentato il numero delle mosse fatte di uno e che
		//si sia aggiornato l'attributo del pastore spostare durante il turno.
		assertEquals(TipoMossa.COMPRA_TESSERA, giocatoreTest.getUltimaMossa());
		assertEquals(numMossePrima+1, giocatoreTest.getNumMosse());
		assertFalse(giocatoreTest.isPastoreSpostato());
		
		giocatoreTest.azzeraTurno();
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		//Stesse prova di prima, stavolta con la mossa "Sposta Pastore".
		assertEquals(TipoMossa.SPOSTA_PASTORE, giocatoreTest.getUltimaMossa());
		assertEquals(numMossePrima+1, giocatoreTest.getNumMosse());
		assertTrue(giocatoreTest.isPastoreSpostato());	
		
	}
	
	@Test
	public void testCheckTurnoGiocatore() throws RemoteException{
		controllerTest.creaGiocatore("Giocatore2", strada2);
		statoPartitaT.setPosPecoraNera(regione1);
		statoPartitaT.setPosLupo(regione1);
		regione1.addPecoraNera();
		regione1.addLupo();
		Giocatore secondoGiocatore = statoPartitaT.getGiocatori().get(1);
		Giocatore provaGiocatore = controllerTest.checkTurnoGiocatore(TipoMossa.COMPRA_TESSERA);
		//Controllo che se il giocatore può fare altre mosse venga ritornato
		//ancora il giocatore corrente.
		assertEquals(giocatoreTest, provaGiocatore);
		
		//Controllo che ritorni il prossimo giocatore.
		giocatoreTest.incNumMosse();
		giocatoreTest.incNumMosse();
		giocatoreTest.incNumMosse();
		provaGiocatore = controllerTest.checkTurnoGiocatore(TipoMossa.COMPRA_TESSERA);
		assertEquals(provaGiocatore, statoPartitaT.getGiocatoreCorrente());
		assertEquals(provaGiocatore, secondoGiocatore);
		
		//Controllo che se è l'ultimo giocatore, ritorna il primo.
		provaGiocatore.incNumMosse();
		provaGiocatore.incNumMosse();
		provaGiocatore.incNumMosse();
		provaGiocatore = controllerTest.checkTurnoGiocatore(TipoMossa.COMPRA_TESSERA);
		assertEquals(provaGiocatore, statoPartitaT.getGiocatoreCorrente());
		assertEquals(provaGiocatore, statoPartitaT.getGiocatori().get(0));
		
		secondoGiocatore.incNumMosse();
		secondoGiocatore.incNumMosse();
		secondoGiocatore.incNumMosse();
		statoPartitaT.setGiocatoreCorrente(secondoGiocatore);
		statoPartitaT.setTurnoFinale();
		provaGiocatore = controllerTest.checkTurnoGiocatore(TipoMossa.SPOSTA_PASTORE);
		assertEquals(null, provaGiocatore);
	}
	

	
	@Test
	public void testCheckSpostaPecoraNera() throws RemoteException {
		regione1.setPecoraNera(true);
		statoPartitaT.setPosPecoraNera(regione1);
		controllerTest.checkSpostaPecoraNera();
		boolean pecoraInRegione1;
		boolean pecoraInRegioniAdiacenti=false;
		//Controllo se la pecora è ancora nella regione d'origine o meno.
		if(regione1.isPecoraNera()) {
			pecoraInRegione1 = true;
		} else {
			pecoraInRegione1 = false;
		}
		
		//Controllo se la pecora nera è in una delle regioni adiacenti a quella
		//della pecora nera.
		List <Regione> regioniAdiacenti = statoPartitaT.getRegioniAdiacenti(regione1);
		for(Regione regione: regioniAdiacenti) {
			if (regione.isPecoraNera()) {
				pecoraInRegioniAdiacenti = true;
			}
		}
		//Se la pecora è sia nella regione d'origine che nelle regioni adiacenti
		//test fallisce
		if(pecoraInRegione1 && pecoraInRegioniAdiacenti) {
			fail();		
		}
		//Se la pecora nera non è né nella regione d'origine né in quelle adiacenti,
		//il test fallisce.
		if(!pecoraInRegione1 && !pecoraInRegioniAdiacenti) {
			fail();
		}
		
	}
	
	public void testMossaPossibile() throws RemoteException {
		//Testo alcune delle mosse limite.
		//Muovere il pastore 3 volte
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		assertTrue(controllerTest.mossaPossibile(TipoMossa.SPOSTA_PASTORE));
		giocatoreTest.azzeraTurno();
		
		//Muovere 1 pecora, comprare 1 tessera, quindi muovere il pastore.
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		assertTrue(controllerTest.mossaPossibile(TipoMossa.SPOSTA_PASTORE));
		giocatoreTest.azzeraTurno();
		
		//Comprare 1 tessera Terreno, Muovere il Pastore, comprare tessera.
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		assertTrue(controllerTest.mossaPossibile(TipoMossa.COMPRA_TESSERA));
		giocatoreTest.azzeraTurno();
		
		//Muovere il pastore 2 volte, quindi comprare una tessera.
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		assertTrue(controllerTest.mossaPossibile(TipoMossa.COMPRA_TESSERA));
		giocatoreTest.azzeraTurno();
		
		//Ora testo condizioni che devono essere false.
		//Compra 2 tessere terreno e poi muovere il pastore.
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		assertFalse(controllerTest.mossaPossibile(TipoMossa.SPOSTA_PASTORE));
		giocatoreTest.azzeraTurno();

		//Muovere il Pastore, quindi Muovere una Pecora per due volte.
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		assertFalse(controllerTest.mossaPossibile(TipoMossa.SPOSTA_PECORA));
		giocatoreTest.azzeraTurno();
		
		//Muovere 1 Pecora, Comprare 1 tessera Terreno, quindi di nuovo Muovere 1 Pecora.
		controllerTest.aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		controllerTest.aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		assertFalse(controllerTest.mossaPossibile(TipoMossa.SPOSTA_PECORA));
		giocatoreTest.azzeraTurno();
	}
	
	
}