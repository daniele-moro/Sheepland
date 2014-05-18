package it.polimi.iodice_moro.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


//PER ORA implementa solo il caricamento della mappa da file xml (file che si trova nella stessa cartella della classe
/**
 * Classe che gestisce lo stato corrente della partita.
 * SINGLETON
 * @author Antonio Iodice, Daniele Moro
 *
 */
public class StatoPartita {
	
	//--------------------------ATTRIBUTI------------------------------------------
	/**
	 * Costante per il numero di recinti normali massimo
	 */
	private static final int NUM_RECINTI_MAX = 20;

	/**
	 * Costante per il file da cui caricare il grafo
	 */
	private static final String FILE_GRAFO = "grafo.xml";
	
	/**
	 * Numero di recinti ancora da posizionare
	 */
	public int numRecinti;
	
	/**
	 * Verifica se si è o meno nel turno finale
	 */
	public boolean turnoFinale;
	
	/**
	 * Tessere per la gestione del costo dei terreni
	 */
	private Map<String,Integer> tessere= new HashMap<String, Integer>();
	
	/**
	 * Grafo che rappresenta la mappa, i vertici del grafo sono sia strade che regioni,
	 * gli archi sono tra Regioni e Strade (regioni che hanno come confine la strada) e tra strade(tra strade adiacenti)
	 */
	private UndirectedGraph<VerticeGrafo,DefaultEdge> mappa;
	
	/**
	 * Lista delle strade della mappa
	 */
	private ArrayList<Strada> strade= new ArrayList<Strada>();
	
	/**
	 * Lista delle regioni della mappa
	 */
	private ArrayList<Regione> regioni= new ArrayList<Regione>();
	
	/**
	 * Attributo per gestire l'istanza singleton.
	 */
	private static StatoPartita instance = null;
	
	/**
	 * Posizione della pecora nera
	 */
	private Regione posPecoraNera;
	
	/**
	 * Giocatore corrente che sta giocando il turno attuale
	 */
	private Giocatore giocatoreCorrente;
	
	/**
	 * Elenco dei giocatori che partecipano alla partita
	 */
	private ArrayList<Giocatore> giocatori = new ArrayList<Giocatore>();
	
	
	//-----------------------------METODI-----------------------------------------
	/**
	 * Metodo per instanziare l'unico oggetto che può essere presente.
	 * Prima di chiamare il costruttore controlla se esiste già una istanza di StatoPartita
	 * @return Ritorna l'unica istanza della classe StatoPartita
	 */
	public static StatoPartita getInstance() {
		if (instance == null)
			instance = new StatoPartita();
		return instance;
	}
	
	/**
	 * Costruttore per l'inizializzazione della partita, con caricamento della mappa,
	 *  è privato perchè lo stato partita è un sigleton, per inizializare la classe si chiama instance()
	 */
	private StatoPartita() {
		initTessere();
		posPecoraNera=null;
		giocatoreCorrente=null;
		numRecinti=NUM_RECINTI_MAX;
		turnoFinale=false;
		mappa= new SimpleGraph<VerticeGrafo,DefaultEdge>(DefaultEdge.class);
		caricaGrafo();
	}
	
	/**
	 * Metodo per inizializzare la lista di tessere con il loro costo,
	 * il costo è inializzato a 0
	 */
	private void initTessere(){
		TipoTerreno[] tipi= TipoTerreno.getTipi();
		for(TipoTerreno t : tipi){
			if(t.toString()!="sheepsburg"){
				tessere.put(t.toString(), 0);
			}
		}
	}

	private void caricaGrafo(){
		parseMappaXML(FILE_GRAFO);
	}
	
	/**
	 * parser del file XML contenente la mappa
	 * @param pathFile Path del file da caricare
	 */
	private void parseMappaXML(String pathFile){
		Map<Integer,VerticeGrafo> nodi = new HashMap<Integer, VerticeGrafo>();
		try{
			/*
			 * SAXBuilder usato per creare oggetti JDOM2
			 */
			SAXBuilder jdomBuilder = new SAXBuilder();
			/*
			 * Apertura del file xml in cui e' contenuta la mappa
			 */
			File xmlFile = new File(pathFile);
			/*
			 * jdomDocument e' l'oggetto JDOM2
			 */
			Document jdomDocument = (Document) jdomBuilder.build(xmlFile);
			/*
			 * Prelevo il nodo ROOT
			 */
			Element rootNode=jdomDocument.getRootElement();
			
			/*
			 * Itero sulle regioni
			 */
			for (Element i : rootNode.getChildren("regione")){
				/*
				 * Prelevo i dati memorizzati nell'xml
				 */
				int id= Integer.parseInt(i.getAttributeValue("id"));
				String tipo = i.getAttributeValue("tipo");
				/*
				 * Istanzio la nuova classe Regione
				 */
				Regione nuovaReg= new Regione(tipo);
				/*
				 * Aggiugo l'istanza alla HashMap dei nodi e al vettore delle regioni
				 */
				nodi.put(id,nuovaReg);
				regioni.add(nuovaReg);
				/*
				 * Aggingo l'istanza al grafo (come vettore
				 */
				mappa.addVertex(nuovaReg);
			}
			/*
			 * Itero sulle starde e applico lo stesso ragionamento delle regioni
			 */
			for (Element i : rootNode.getChildren("strada")){
				/*
				 * Prelevo i dati memorizzati nell'xml
				 */
				int id= Integer.parseInt(i.getAttributeValue("id"));
				int ncas = Integer.parseInt(i.getAttributeValue("ncasella"));
				
				Strada nuovaStr= new Strada(ncas);
				nodi.put(id,nuovaStr);
				strade.add(nuovaStr);
				mappa.addVertex(nuovaStr);
			}
			/*
			 * itero sui link tra strade
			 */
			for (Element i : rootNode.getChildren("arco")){
		//		int id= Integer.parseInt(i.getAttributeValue("id")); //ID dell'arco non usato
				/*
				 * Memorizzo id del source e della destinazione
				 */
				int idSource = Integer.parseInt(i.getAttributeValue("source"));
				int idDest = Integer.parseInt(i.getAttributeValue("dest"));
				
				/*
				 * prelevo dalla hashmap gli oggetti correlati a quell'id
				 */
				VerticeGrafo nodoSource = nodi.get(idSource);
				VerticeGrafo nodoDest = nodi.get(idDest);
				
				/*
				 * Aggiungo al grafo l'arco corrispondente
				 */
				mappa.addEdge((VerticeGrafo)nodoSource, (VerticeGrafo)nodoDest);
			}

			//DEBUG DEL GRAFO
			System.out.println(mappa.toString());
			System.out.println(mappa.edgesOf(nodi.get(1)).toString());

		} catch (IOException io) {
			//Eccezione dovuta al file
			System.out.println(io.getMessage());
		}
		catch (JDOMException jdomex) {
			//Eccezione dovuto a JDOM2
			System.out.println(jdomex.getMessage());
		}
		
	}
	

	/**
	 * Metodo che ritorna le strade adiacenti ad una starda data
	 * @param strada Strada di cui si vuole trovare le strade adiacenti
	 * @return Ritorna le Strade adiacenti
	 */
	private ArrayList<Strada> getStradeAdiacenti(Strada strada){
		/*
		 * Prelevo tutti gli archi che partono dalla strada di cui devo trovare le adiacenti
		 */
		Set<DefaultEdge> archi =mappa.edgesOf(strada);
		ArrayList<Strada> stradeAdiacenti = new ArrayList<Strada>();
		
		/*
		 * itero sugli archi ottenuti, per ogni arco prelevo la sorgente dell'arco e 
		 * controllo che il vertice sia una regione e non sia la strada che ho come parametro; 
		 * se è una regione la memorizzo nell'arrai delle starde adiacenti
		 */
		for(DefaultEdge arc : archi){
			VerticeGrafo destArco = mappa.getEdgeSource(arc);
			if(!destArco.isRegione() && destArco!=strada){
				stradeAdiacenti.add((Strada) destArco);
			}
			/*
			 * stesso ragionamento di prima solo per la destinazione dell'arco
			 */
			destArco = mappa.getEdgeTarget(arc);
			if(!destArco.isRegione() && destArco!=strada){
				stradeAdiacenti.add((Strada)destArco);
			}	
		}
		return stradeAdiacenti;
	}
	
	public static void main(String[] args){
		//Test per verificare il caricamento della mappa da XML e il prelievo delle strade adiacenti
		StatoPartita stato = getInstance();
		System.out.println("Adiacenze"+stato.getStradeAdiacenti(stato.strade.get(0)).toString());
	}
	
	//------------------------GETTERS & SETTERS----------------------------------
	
	/**
	 * @return Numero di recinti ancora disponibili
	 */
	public int getNumRecinti() {
		return numRecinti;
	}
	
	/**
	 * Metodo che decrementa il numero di recinti, quando uno di questi viene utilizzato
	 */
	public void decrementaNumRecinti() {
		this.numRecinti--;
	}

	/**
	 * @return Ritorna lo stato del turno finale
	 */
	public boolean isTurnoFinale() {
		return turnoFinale;
	}
	
	/**
	 * @param turnoFinale Per settare quando è il turno finale
	 */
	public void setTurnoFinale(boolean turnoFinale) {
		this.turnoFinale = turnoFinale;
	}

	/**
	 * @return Ritorna la Regione dove è posizionata la pecora nera
	 */
	public Regione getPosPecoraNera() {
		return posPecoraNera;
	}

	/**
	 * @param posPecoraNera Posizione della pecora nera
	 */
	public void setPosPecoraNera(Regione posPecoraNera) {
		this.posPecoraNera = posPecoraNera;
	}

	/**
	 * @return Ritorna il giocatore che sta giocando nel turno corrente
	 */
	public Giocatore getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	public void setGiocatoreCorrente(Giocatore giocatoreCorrente) {
		this.giocatoreCorrente = giocatoreCorrente;
	}

	public ArrayList<Giocatore> getGiocatori() {
		return giocatori;
	}

	public void addGiocatore(Giocatore giocatore) {
		giocatori.add(giocatore);
	}
	
	public int numGiocatori(){
		return giocatori.size();
	}
	
	public int costoTessera(TipoTerreno tessera){
		return tessere.get(tessera.toString());
	}
	
	public void incCostoTessera(TipoTerreno tessera){
		int costo=tessere.get(tessera.toString());
		costo++;
		tessere.put(tessera.toString(), costo);
	}
	
	
	

}
