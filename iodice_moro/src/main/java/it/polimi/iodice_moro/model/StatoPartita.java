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
	 * Metodo per instanziare l'unica istanza che può essere presente nel programma.
	 * Prima di chimare il costruttore controlla se esiste già una istanza di StatoPartita
	 * @param path Path dove si trova il file da cui caricare la mappa
	 * @return Ritorna l'unica istanza della classe StatoPartita
	 */
	public static StatoPartita getInstance(String path) {
		if (instance == null)
			instance = new StatoPartita(path);
		return instance;
	}
	
	/**
	 * Overload del metodo {@link StatoParita#getInstance(String path)} 
	 * il path per questo metodo è quello di default
	 * @return
	 */
	public static StatoPartita getInstance(){
		return getInstance(FILE_GRAFO);
	}
	
	/**
	 * Costruttore per l'inizializzazione della partita, con caricamento della mappa,
	 *  è privato perchè lo StatoPartita è un singleton, 
	 *  per inizializare la classe si chiama {@link StatoPartita#getInstance(String path)}
	 * @param path Indirizzo del file da cui caricare la mappa
	 */
	private StatoPartita(String path) {
		initTessere();
		posPecoraNera=null;
		giocatoreCorrente=null;
		numRecinti=NUM_RECINTI_MAX;
		turnoFinale=false;
		mappa= new SimpleGraph<VerticeGrafo,DefaultEdge>(DefaultEdge.class);
		caricaMappa(path);
	}
	
	/**
	 * Metodo per inizializzare la lista di tessere con il loro costo,
	 * il costo è inializzato a 0
	 * utilizza {@link TipoTerreno#values()} per prelevare tutte le possibili istanze dell'enumeratore
	 */
	private void initTessere(){
		for(TipoTerreno t: TipoTerreno.values()){
			if(!t.toString().equals("sheepsburg")){
				tessere.put(t.toString(), 0);
			}
		}
	}

	/**
	 * Metodo per caricare la mappa
	 * @param path Indirizzo del file da cui caricare la mappa
	 */
	private void caricaMappa(String path){
		try {
			parseMappaXML(path);
		} catch (JDOMException | IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Parser del file XML contenente la mappa
	 * @param pathFile Path del file da caricare
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private void parseMappaXML(String pathFile) throws JDOMException, IOException{
		Map<Integer,VerticeGrafo> nodi = new HashMap<Integer, VerticeGrafo>();
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

	}

	/**
	 * Il metodo ritorna le strade adiacenti (quindi subito collegate) ad una strada passata come parametro
	 * @param strada Strada di cui si vuole trovare le strade adiacenti
	 * @return Ritorna le Strade adiacenti
	 */
	public ArrayList<Strada> getStradeAdiacenti(Strada strada){
		/*
		 * Prelevo tutti gli archi che partono dalla strada di cui devo trovare le adiacenti
		 */
		Set<DefaultEdge> archi =mappa.edgesOf(strada);
		ArrayList<Strada> stradeAdiacenti = new ArrayList<Strada>();
		
		/*
		 * itero sugli archi ottenuti
		 */
		for(DefaultEdge arc : archi){
			/*
			 * Per ogni arco prelevo la sorgente dell'arco e 
			 * controllo che il vertice sia una strada e non sia la strada che ho come parametro; 
			 * se è una strada la memorizzo nell'array delle starde adiacenti
			 */
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
	
	/**
	 * Il metodo preleva le strade che confinano con la regione passata come parametro
	 * @param regione Regione di cui trovare le strade confinanti
	 * @return Lista di strade che confinano con la regione passata
	 */
	public ArrayList<Strada> getStradeConfini(Regione regione){
		/*
		 * Prelevo tutti gli archi che partono dalla regione di cui devo trovare le strade confinanti
		 */
		Set<DefaultEdge> archi = mappa.edgesOf(regione);
		ArrayList<Strada> stradeConfini = new ArrayList<Strada>();
		/*
		 * itero sugli archi ottenuti
		 */
		for(DefaultEdge arc : archi){
			/*
			 *per ogni arco prelevo la sorgente dell'arco e 
			 * controllo che il vertice sia una strada; 
			 * se è una strada la memorizzo nell'array delle starde adiacenti
			 */
			VerticeGrafo destArco = mappa.getEdgeSource(arc);
			if(!destArco.isRegione()){
				stradeConfini.add((Strada) destArco);
			}
			/*
			 * stesso ragionamento di prima solo per la destinazione dell'arco
			 */
			destArco = mappa.getEdgeTarget(arc);
			if(!destArco.isRegione()){
				stradeConfini.add((Strada)destArco);
			}
		}
		return stradeConfini;
		
	}
	
	/**
	 * Metodo che ritorna l'altra regione rispetto a quella passata come parametro, questa regione
	 * confina inoltre anche con la regione passata come parametro
	 * @param regione Regione che confina con la strada
	 * @param strada Strada che confina con la regione e che confinerà con la regione che stiamo cercando
	 * @return Regione che confina con la strada del parametro
	 */
	public Regione getAltraRegione(Regione regione, Strada strada){
		/*
		 * prelevo tutti gli archi che partono dalla strada, tra tutti gli archi ce ne saranno solo 2 che sono regioni,
		 * una di queste è quella che stiamo cercando
		 */
		Set<DefaultEdge> archi = mappa.edgesOf(strada);
		/*
		 * itero sugli archi che partono dalla strada passata come parametro
		 * per ogni arco dovrò vedere la destinazione e source e verificare se è una regione e se lo è,
		 * che sia diversa da quella passata come parametro
		 */
		for(DefaultEdge arc : archi){
			VerticeGrafo destArco=mappa.getEdgeSource(arc);
			if(destArco.isRegione() && destArco!=regione)
				return (Regione) destArco;
			destArco = mappa.getEdgeTarget(arc);
			if(destArco.isRegione() && destArco!=regione)
				return (Regione) destArco;
		}
		/*
		 * se non trovo nessuna regione, torno null per segnalare che non ci sono regioni 
		 * che sono adiacenti alla regione passata con in mezzo la strada passata come parametro
		 */
		return null;
	}
	
	/**
	 * Metodo che ritorna le regioni adiacenti ad una regione data,
	 *  usa {@link StatoPartita#getStradeConfini(Regione regione)} per trovare le strade che circondano la regione data;
	 *  usa {@link StatoPartita#getAltraRegione(Regione regione, Strada strada)}
	 * @param regione Regione di cui trovare le regioni che confinano con essa
	 * @return Elenco delle regioni che confinano con il parametro passato
	 */
	public ArrayList<Regione> getRegioniAdiacenti(Regione regione){
		/*
		 * Prelevo tutti gli archi che partono dalla regione di cui devo trovare le regioni adiacenti
		 */
		ArrayList<Strada> stradeConfini = getStradeConfini(regione);
		ArrayList<Regione> regioniConfini = new ArrayList<Regione>();
		
		for(Strada str : stradeConfini){
			regioniConfini.add(getAltraRegione(regione, str));
		}
		return regioniConfini;
		
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
	public void decNumRecinti() {
		this.numRecinti--;
	}

	/**
	 * @return Ritorna lo stato del turno finale
	 */
	public boolean isTurnoFinale() {
		return turnoFinale;
	}
	
	/**
	 * @param turnoFinale Per settare true turnoFinale quando è il turno finale
	 */
	public void setTurnoFinale() {
		this.turnoFinale = true;
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

	/**
	 * @param giocatoreCorrente Setta il giocatore che sta svolgendo il turno corrente
	 */
	public void setGiocatoreCorrente(Giocatore giocatoreCorrente) {
		this.giocatoreCorrente = giocatoreCorrente;
	}

	/** 
	 * @return Elenco dei giocatori che partecipano alla partita
	 */
	public ArrayList<Giocatore> getGiocatori() {
		return giocatori;
	}

	/**
	 * @param giocatore Giocatore da aggiungere alla partita
	 */
	public void addGiocatore(Giocatore giocatore) {
		giocatori.add(giocatore);
	}
	/**
	 * @return Ritorna il numero di giocatori che stanno giocando la partita
	 */
	public int numGiocatori(){
		return giocatori.size();
	}
	
	/**
	 * Metodo per conoscere il costo di un tipo di terreno
	 * @param tessera {@link TipoTerreno} del terreno di cui voglio conoscere il costo
	 * @return Ritorna il costo del terreno
	 */
	public int getCostoTessera(TipoTerreno tessera){
		return tessere.get(tessera.toString());
	}
	
	/**
	 * Metodo per incrementare il costo di un terreno
	 * @param tessera {@link TipoTerreno} del terreno di cui voglio incrementare il costo
	 */
	public void incCostoTessera(TipoTerreno tessera){
		int costo=tessere.get(tessera.toString());
		costo++;
		tessere.put(tessera.toString(), costo);
	}
}
