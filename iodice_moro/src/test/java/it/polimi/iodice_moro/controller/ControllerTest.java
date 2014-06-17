package it.polimi.iodice_moro.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.network.ViewRMI;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest{

	StatoPartita statoPartitaT;
	Giocatore giocatoreTest;
	Controller controllerTest;
	
	IFView fakeView;
	
	Regione regione0, regione1, regione2, regione3, regione4;
	Strada strada0, strada1, strada2, strada3, strada4;
	
	@Before
	public void setUp() throws Exception {
		statoPartitaT= new StatoPartita();
		controllerTest = new Controller(statoPartitaT);
		fakeView = new FakeView();
		controllerTest.setView(fakeView);
		
		
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
	public void testCreaGiocatoreWithColors() throws RemoteException, PartitaIniziataException {		
		Color colore = controllerTest.creaGiocatore("Prova_x");
		//Controllo che il secondo giocatore della partita
		assertEquals("Prova_x", statoPartitaT.getGiocatori().get(1).getNome());
		assertFalse(statoPartitaT.getGiocatori().get(1).getColore().equals(null));
		assertEquals(colore, statoPartitaT.getGiocatori().get(1).getColore());
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
	
	@Test
	public void testPagaSpostamento() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class myTarget = Controller.class;
		Class params[] = new Class[2];
		params[0] = Strada.class;
		params[1] = Giocatore.class;
		Method pagaSpostamento = myTarget.getDeclaredMethod("pagaSpostamento", params);
		pagaSpostamento.setAccessible(true);
		
		List<Strada> stradaAdiacenti = statoPartitaT.getStradeAdiacenti(giocatoreTest.getPosition());
		
		//Aggiungo il lupo alla regione.
		regione0.addLupo();
		
		Boolean result = (Boolean)pagaSpostamento.invoke(controllerTest, stradaAdiacenti.get(0), giocatoreTest);
		Boolean result2= (Boolean)pagaSpostamento.invoke(controllerTest, strada4, giocatoreTest);
		
		assertFalse(result);
		assertTrue(result2);
	}
	
	@Test
	public void testLanciaDado()  throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class myTarget = Controller.class;
		Method lanciaDado = myTarget.getDeclaredMethod("lanciaDado");
		lanciaDado.setAccessible(true);
		int result;
		for(int i=0; i<20; i++) {
			result=(int)lanciaDado.invoke(controllerTest);
			assertTrue(result >0 && result <7);
		}
	}
	
	@Test
	@SuppressWarnings("deprecation")
	public void testGetPosRegioni() {
		Map<String, Point> posRegioni = controllerTest.getPosRegioni();
		double x,y;
		//Verifico per 4 regioni
		//Regione ID ffd20000
		x=posRegioni.get("ffd20000").getX();
		y=posRegioni.get("ffd20000").getY();
		assertEquals(52, x,0);
		assertEquals(170,y,0);
		//Regione ID ff005473
		x=posRegioni.get("ff005473").getX();
		y=posRegioni.get("ff005473").getY();
		assertEquals(173, x, 0);
		assertEquals(383,y, 0);
		//Regione ID ffff2323
		x=posRegioni.get("ffff2323").getX();
		y=posRegioni.get("ffff2323").getY();
		assertEquals(174, x, 0);
		assertEquals(144,y, 0);
		//Regione ID ffff23aa
		x=posRegioni.get("ffff23aa").getX();
		y=posRegioni.get("ffff23aa").getY();
		assertEquals(370, x, 0);
		assertEquals(120,y, 0);	
	}
	
	@Test
	@SuppressWarnings("deprecation")
	public void testGetPosStrade() {
		Map<String, Point> posStrade = controllerTest.getPosStrade();
		double x,y;
		//Verifico per 4 strade
		//Strada ID ff3c00ff
		x=posStrade.get("ff3c00ff").getX();
		y=posStrade.get("ff3c00ff").getY();
		assertEquals(71, x, 0);
		assertEquals(246,y, 0);
		//Strada ID fffb8f9b
		x=posStrade.get("fffb8f9b").getX();
		y=posStrade.get("fffb8f9b").getY();
		assertEquals(142, x, 0);
		assertEquals(215,y,0);
		//Strada ID ffff2323
		x=posStrade.get("ffb4808e").getX();
		y=posStrade.get("ffb4808e").getY();
		assertEquals(113, x, 0);
		assertEquals(167,y, 0);
		//Strada ID ffd27f52
		x=posStrade.get("ffd27f52").getX();
		y=posStrade.get("ffd27f52").getY();
		assertEquals(254, x, 0);
		assertEquals(503,y, 0);
	}
	
	@Test
	public void testGetIDRegioniByAd() {
		List<String> regioniAdiacenti=controllerTest.getIDRegioniAd();
		List<Regione> regioniAdiacentiReali=statoPartitaT.getRegioniADStrada(statoPartitaT.getGiocatoreCorrente().getPosition());
		assertTrue(regioniAdiacenti.contains(regioniAdiacentiReali.get(0).getColore()));
		assertTrue(regioniAdiacenti.contains(regioniAdiacentiReali.get(1).getColore()));
		
	}
	
	@Test 
	public void testAddView() throws RemoteException, PartitaIniziataException {
		//Creo ViewRMI e gli aggiungo una view.
		IFView viewrmi = new ViewRMI(controllerTest);;
		Color colore = controllerTest.creaGiocatore("X");
		controllerTest.setView(viewrmi);
		controllerTest.addView(fakeView, colore);
		//Controllo che la view restituita in base al colore sia quella giusta.
		assertEquals(fakeView, ((ViewRMI)viewrmi).getViews().get(colore));
	}
	
	@Test
	public void testGetGiocatori() {
		Map<Color, String> mappaGiocatori = controllerTest.getGiocatori();
		List<Giocatore> listaGiocatori = statoPartitaT.getGiocatori();
		for(Giocatore g : listaGiocatori) {
			assertTrue(mappaGiocatori.keySet().contains(g.getColore()));
		}
	}
	
	@Test
	public void testIniziaPartita() throws RemoteException {
		
		controllerTest.iniziaPartita();
		
		//Controllo che vengano inizializzate nel modo giusto le strade e le regioni.
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		Map<String, Point> posStrade = new HashMap<String,Point>();
		
		for(Regione r: statoPartitaT.getRegioni()){
			posRegioni.put(r.getColore(),r.getPosizione());
		}
		
		for(Strada s: statoPartitaT.getStrade()){
			posStrade.put(s.getColore(),s.getPosizione());
		}
		
		assertEquals(posRegioni, ((FakeView)fakeView).getPosRegioni());
		assertEquals(posStrade, ((FakeView)fakeView).getPosStrade());
		
		//Controllo che il numero delle pecore sia inizializzato nel modo giusto.
		for(Regione regione : statoPartitaT.getRegioni()) {
			if(!(regione.getTipo().equals(TipoTerreno.SHEEPSBURG))) {
				assertEquals(1, regione.getNumPecore());
			}
			else {
				//Controllo che la posizione del lupo e della pecora sia in sheepland.
				assertTrue(regione.isLupo());
				assertTrue(regione.isPecoraNera());
				assertEquals(statoPartitaT.getPosLupo(), regione);
				assertEquals(statoPartitaT.getPosPecoraNera(),  regione);
			}
			//Controllo che il numero delle tessere di ogni giocatore sia maggiore di zero
			//cioè gli sia stata assegnata la tessera iniziale.
			Boolean result = true;
			for(Giocatore giocatore : statoPartitaT.getGiocatori()) {
				for(Entry<String, Integer> entry : giocatore.getTesserePossedute().entrySet()) {
					int value = entry.getValue();
					if(value<0 || value>1) {
						result = false;
					}
				}
			assertTrue(result);
				
			}
			
			assertEquals(statoPartitaT.getGiocatoreCorrente(), statoPartitaT.getGiocatori().get(0));
		}
	}
	
	@Test
	public void testIniziaPartitaWithTwoPlayers() throws RemoteException, PartitaIniziataException {
		controllerTest.creaGiocatore("x");
		assertEquals(2, statoPartitaT.getGiocatori().size());
		controllerTest.iniziaPartita();
		assertEquals(30, statoPartitaT.getGiocatori().get(0).getSoldi());
		assertEquals(30, statoPartitaT.getGiocatori().get(1).getSoldi());
	}
	
	@Test
	public void testAccoppiamento1() throws RemoteException, NotAllowedMoveException, IllegalClickException {
		int lancio;
		Regione regionex = statoPartitaT.getRegioniADStrada(statoPartitaT.getGiocatoreCorrente().getPosition()).get(0);
		int numPecore = 2;
		regionex.setNumPecore(numPecore);
		
		
		//Testo per tre volte il giusto funzionamento di accoppiamento1().
		controllerTest.accoppiamento1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore++;
			assertEquals(numPecore, regionex.getNumPecore());
			
		}
		
		giocatoreTest.azzeraTurno();
		controllerTest.accoppiamento1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore++;
			assertEquals(numPecore, regionex.getNumPecore());
			
		}
		
		giocatoreTest.azzeraTurno();
		controllerTest.accoppiamento1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore++;
			assertEquals(numPecore, regionex.getNumPecore());
		}
	}
	
	@Test
	public void testAccoppiamento1WithNotAllowedMoveExc() {
		Regione regionex = statoPartitaT.getRegioniADStrada(statoPartitaT.getGiocatoreCorrente().getPosition()).get(0);
		int numPecore = 1;
		regionex.setNumPecore(numPecore);
		try {
			controllerTest.accoppiamento1(regionex.getColore());
			fail("Should have thrown exception");
		} catch (RemoteException e) {
			fail();
		} catch (NotAllowedMoveException e) {
			
		} catch (IllegalClickException e) {
			fail();
		}
		
	}
	
	@Test
	public void testAccoppiamento1WithIllegalClickExc () {
		try {
			controllerTest.accoppiamento1("fafian");
			fail("Should have thrown exception");
		} catch (RemoteException e) {
			fail();
		} catch (NotAllowedMoveException e) {
			fail();
		} catch (IllegalClickException e) {
			
		}
	}
	
	@Test
	public void testSparatoria1() throws RemoteException, NotAllowedMoveException, IllegalClickException {
		int lancio;
		Regione regionex = statoPartitaT.getRegioniADStrada(statoPartitaT.getGiocatoreCorrente().getPosition()).get(0);
		int numPecore = 2;
		regionex.setNumPecore(numPecore);
		
		//Testo per tre volte il giusto funzionamento di accoppiamento1().
		controllerTest.sparatoria1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		System.out.println(lancio);
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore--;
			assertEquals(numPecore, regionex.getNumPecore());
		}
		
		giocatoreTest.azzeraTurno();
		controllerTest.sparatoria1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		System.out.println(lancio);
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore--;
			assertEquals(numPecore, regionex.getNumPecore());	
			
		}
		
		giocatoreTest.azzeraTurno();
		controllerTest.sparatoria1(regionex.getColore());
		lancio = ((FakeView)fakeView).getLancioDado();
		System.out.println(lancio);
		if(lancio == statoPartitaT.getGiocatoreCorrente().getPosition().getnCasella()) {
			numPecore--;
			assertEquals(numPecore, regionex.getNumPecore());
		}
	}
	
	@Test
	public void testSparatoria1WithNotAllowedMoveExc() {
		Regione regionex = statoPartitaT.getRegioniADStrada(statoPartitaT.getGiocatoreCorrente().getPosition()).get(0);
		int numPecore = 0;
		regionex.setNumPecore(numPecore);
		try {
			controllerTest.sparatoria1(regionex.getColore());
			fail("Should have thrown exception");
		} catch (RemoteException e) {
			fail();
		} catch (NotAllowedMoveException e) {
			
		} catch (IllegalClickException e) {
			fail();
		}
		
	}
	
	@Test
	public void testSparatoria1WithIllegalClickExc () {
		try {
			controllerTest.accoppiamento1("facian");
			fail("Should have thrown exception");
		} catch (RemoteException e) {
			fail();
		} catch (NotAllowedMoveException e) {
			fail();
		} catch (IllegalClickException e) {
			
		}
	}
	
	@Test
	public void testSortByValue() {
		//Creo una lista, la ordino usando l'algoritmo e controllo che l'ordine sia giusto.
		Map<Giocatore, Integer> punteggi = new HashMap<Giocatore, Integer>();
		punteggi.put(new Giocatore("Third"), 10);
		punteggi.put(new Giocatore("First"), 50);
		punteggi.put(new Giocatore("Second"), 30);
		punteggi.put(new Giocatore("Fourth"), 1);
		punteggi=Controller.sortByValue(punteggi);
		Iterator<Entry<Giocatore, Integer>> iterator = punteggi.entrySet().iterator();
		assertEquals((Integer)50, iterator.next().getValue());	
		assertEquals((Integer)30, iterator.next().getValue());
		assertEquals((Integer)10, iterator.next().getValue());
		assertEquals((Integer)1, iterator.next().getValue());
		
	}
}