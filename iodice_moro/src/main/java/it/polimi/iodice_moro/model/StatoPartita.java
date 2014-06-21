package it.polimi.iodice_moro.model;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Classe che gestisce lo stato corrente della partita.
 * @author Antonio Iodice, Daniele Moro
 *
 */
public class StatoPartita {
	
	private static final Logger logger_model =  Logger.getLogger("it.polimi.iodice_moro.model");
	
	//--------------------------ATTRIBUTI------------------------------------------
	/**
	 * Costante per il numero di recinti normali massimo
	 */
	public static final int NUM_RECINTI_MAX = 20;

	/**
	 * Costante per il file da cui caricare il grafo
	 */
	private static final String FILE_GRAFO = "grafo.xml";
	
	/**
	 * Numero di recinti ancora da posizionare
	 */
	private int numRecinti;
	
	/**
	 * Verifica se si è o meno nel turno finale
	 */
	private boolean turnoFinale;
	
	/**
	 * Tessere per la gestione del costo dei terreni
	 */
	private Map<String,Integer> tessere= new HashMap<String, Integer>();
	
	/**
	 * Grafo che rappresenta la mappa, i vertici del grafo sono sia strade che regioni,
	 * gli archi sono tra Regioni e Strade (regioni che hanno come confine la strada) e tra strade(tra strade adiacenti)
	 * Gli archi tra Regioni e Strade pesano di più (100 volte gli altri)per evitare 
	 * che nel calcolo del percorso minimo tra due strade vengano presi in considerazione anche questi archi.
	 */
	private WeightedGraph<VerticeGrafo, DefaultWeightedEdge> mappa;
	
	/**
	 * Lista delle strade della mappa
	 */
	private List<Strada> strade= new ArrayList<Strada>();
	
	/**
	 * Lista delle regioni della mappa
	 */
	private List<Regione> regioni= new ArrayList<Regione>();
	
	/**
	 * Posizione della pecora nera
	 */
	private Regione posPecoraNera;
	
	/**
	 * Posizione del lupo.
	 */
	private Regione posLupo;
	
	/**
	 * Giocatore corrente che sta giocando il turno attuale
	 */
	private Giocatore giocatoreCorrente;
	
	/**
	 * Elenco dei giocatori che partecipano alla partita
	 */
	private List<Giocatore> giocatori = new ArrayList<Giocatore>();
	
	
	//-----------------------------METODI-----------------------------------------
	
	/**
	 * Costruttore per l'inizializzazione della partita, con caricamento della mappa,
	 *  è privato perchè lo StatoPartita è un singleton, 
	 *  per inizializare la classe si chiama {@link StatoPartita#getInstance(String path)}
	 * @param file Indirizzo del file da cui caricare la mappa
	 */
	public StatoPartita(String file) {
		initTessere();
		posPecoraNera=null;
		posLupo=null;
		giocatoreCorrente=null;
		numRecinti=NUM_RECINTI_MAX;
		turnoFinale=false;
		mappa= new SimpleWeightedGraph<VerticeGrafo,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		caricaMappa(file);
	}
	
	/**
	 * Costruttore senza parametri, usa come file del grafo il file di default
	 */
	public StatoPartita(){
		this(FILE_GRAFO);
	}
	
	/**
	 * Metodo per inizializzare la lista di tessere con il loro costo,
	 * il costo è inializzato a 0
	 * utilizza {@link TipoTerreno#values()} per prelevare tutte le possibili istanze dell'enumeratore
	 */
	private void initTessere(){
		for(TipoTerreno t: TipoTerreno.values()){
			tessere.put(t.toString(), 0);
				}
	}

	/**
	 * Metodo per caricare la mappa
	 * Gestisce le eventuali eccezioni generate dal caricamento da file della mappa
	 * @param file Indirizzo del file da cui caricare la mappa
	 */
	private void caricaMappa(String file){
		try {
			parseMappaXML("/"+file);
		} catch (JDOMException e)  {
			/*
			 * L'eccezione viene loggata nel logger del model
			 */
			logger_model.log(Level.SEVERE, e.getMessage());
		} catch(IOException e){
			/*
			 * L'eccezione viene loggata nel logger del model
			 */
			logger_model.log(Level.SEVERE, e.getMessage());
			
		}
	}
	
	/**
	 * Parser del file XML contenente la mappa
	 * @param url URL del file da caricare
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private void parseMappaXML(String file) throws JDOMException, IOException{
		Map<Integer,VerticeGrafo> nodi = new HashMap<Integer, VerticeGrafo>();
		// SAXBuilder usato per creare oggetti JDOM2
		SAXBuilder jdomBuilder = new SAXBuilder();
		
		//jdomDocument e' l'oggetto JDOM2
		Document jdomDocument = (Document) jdomBuilder.build(this.getClass().getResourceAsStream(file));
		
		//Prelevo il nodo ROOT
		Element rootNode=jdomDocument.getRootElement();

		//Itero sulle regioni
		for (Element i : rootNode.getChildren("regione")){
			/*
			 * Prelevo i dati memorizzati nell'xml
			 */
			int id= Integer.parseInt(i.getAttributeValue("id"));
			String tipo = i.getAttributeValue("tipo");
			String colore = i.getAttributeValue("colore");
			int posx = Integer.parseInt(i.getAttributeValue("x"));
			int posy = Integer.parseInt(i.getAttributeValue("y"));
			Point posizione = new Point(posx,posy);
			
			//Istanzio la nuova classe Regione
			Regione nuovaReg= new Regione(tipo, colore, posizione);
			//Aggiugo l'istanza alla HashMap dei nodi e al vettore delle regioni
			nodi.put(id,nuovaReg);
			regioni.add(nuovaReg);
			
			//Aggingo l'istanza al grafo (come vettore
			mappa.addVertex(nuovaReg);
		}
		
		//Itero sulle starde e applico lo stesso ragionamento delle regioni
		for (Element i : rootNode.getChildren("strada")){
			//Prelevo i dati memorizzati nell'xml
			int id= Integer.parseInt(i.getAttributeValue("id"));
			int ncas = Integer.parseInt(i.getAttributeValue("ncasella"));
			String colore = i.getAttributeValue("colore");
			int posx = Integer.parseInt(i.getAttributeValue("x"));
			int posy = Integer.parseInt(i.getAttributeValue("y"));
			Point posizione = new Point(posx,posy);

			Strada nuovaStr= new Strada(ncas, colore, posizione);
			nodi.put(id,nuovaStr);
			strade.add(nuovaStr);
			mappa.addVertex(nuovaStr);
		}
		
		//itero sui link tra strade/strade e strade/regioni
		for (Element i : rootNode.getChildren("arco")){
			//Memorizzo id del source e della destinazione
			int idSource = Integer.parseInt(i.getAttributeValue("source"));
			int idDest = Integer.parseInt(i.getAttributeValue("dest"));

			//prelevo dalla hashmap gli oggetti correlati a quell'id
			VerticeGrafo nodoSource = nodi.get(idSource);
			VerticeGrafo nodoDest = nodi.get(idDest);
			
			//Alloco il nuovo arco pesato
			DefaultWeightedEdge edge = new DefaultWeightedEdge();
			//Aggiungo al grafo l'arco corrispondente
			mappa.addEdge((VerticeGrafo)nodoSource, (VerticeGrafo)nodoDest, edge);
			/*
			 * Controllo se l'arco è tra strade e regioni, in questo caso deve pesare 100 
			 * (per evitare che venga preso in considerazione nel calcolo del percorso minimo tra strade), 
			 *in caso contrario 1
			 */
			if(nodoDest.isRegione() || nodoSource.isRegione()){
				mappa.setEdgeWeight(edge, 100);
			} else{
				mappa.setEdgeWeight(edge, 1);
			}
		}
	}

	/**
	 * Il metodo ritorna le strade adiacenti (quindi subito collegate) ad una strada passata come parametro
	 * @param strada Strada di cui si vuole trovare le strade adiacenti
	 * @return Ritorna le Strade adiacenti
	 */
	public List<Strada> getStradeAdiacenti(Strada strada){
		//Prelevo tutti gli archi che partono dalla strada di cui devo trovare le adiacenti
		Set<DefaultWeightedEdge> archi =mappa.edgesOf(strada);
		List<Strada> stradeAdiacenti = new ArrayList<Strada>();
		
		//itero sugli archi ottenuti
		for(DefaultWeightedEdge arc : archi){
			/*
			 * Per ogni arco prelevo la sorgente dell'arco e 
			 * controllo che il vertice sia una strada e non sia la strada che ho come parametro,
			 * se è una strada la memorizzo nell'array delle starde adiacenti
			 */
			VerticeGrafo destArco = mappa.getEdgeSource(arc);
			if(!destArco.isRegione() && !destArco.equals(strada)){
				stradeAdiacenti.add((Strada) destArco);
			}
			
			//stesso ragionamento di prima solo per la destinazione dell'arco
			destArco = mappa.getEdgeTarget(arc);
			if(!destArco.isRegione() && !destArco.equals(strada)){
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
	public List<Strada> getStradeConfini(Regione regione){
		//Prelevo tutti gli archi che partono dalla regione di cui devo trovare le strade confinanti
		Set<DefaultWeightedEdge> archi = mappa.edgesOf(regione);
		List<Strada> stradeConfini = new ArrayList<Strada>();
		
		//itero sugli archi ottenuti
		for(DefaultWeightedEdge arc : archi){
			/*
			 *per ogni arco prelevo la sorgente dell'arco e 
			 * controllo che il vertice sia una strada,
			 * se è una strada la memorizzo nell'array delle starde adiacenti
			 */
			VerticeGrafo destArco = mappa.getEdgeSource(arc);
			if(!destArco.isRegione()){
				stradeConfini.add((Strada) destArco);
			}
			
			//stesso ragionamento di prima solo per la destinazione dell'arco
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
		Set<DefaultWeightedEdge> archi = mappa.edgesOf(strada);
		/*
		 * itero sugli archi che partono dalla strada passata come parametro
		 * per ogni arco dovrò vedere la destinazione e source e verificare se è una regione e se lo è,
		 * che sia diversa da quella passata come parametro
		 */
		for(DefaultWeightedEdge arc : archi){
			VerticeGrafo destArco=mappa.getEdgeSource(arc);
			if(destArco.isRegione() && !destArco.equals(regione)){
				return (Regione) destArco;
			}
			destArco = mappa.getEdgeTarget(arc);
			if(destArco.isRegione() && !destArco.equals(regione)){
				return (Regione) destArco;
			}
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
	public List<Regione> getRegioniAdiacenti(Regione regione){
		//Prelevo tutti gli archi che partono dalla regione di cui devo trovare le regioni adiacenti
		List<Strada> stradeConfini = getStradeConfini(regione);
		List<Regione> regioniConfini = new ArrayList<Regione>();
		
		for(Strada str : stradeConfini){
			regioniConfini.add(getAltraRegione(regione, str));
		}
		return regioniConfini;
		
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
	 * @return Ritorna la regione dove è posizionato il lupo.
	 */
	public Regione getPosLupo() {
		return posLupo;
	}

	/**
	 * @param posPecoraNera Posizione della pecora nera
	 */
	public void setPosPecoraNera(Regione posPecoraNera) {
		this.posPecoraNera = posPecoraNera;
	}
	
	/**
	 * @param posLupo Posizione del lupo.
	 */
	public void setPosLupo(Regione posLupo) {
		this.posLupo = posLupo;
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
	public List<Giocatore> getGiocatori() {
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
	
	/**
	 * @return Elenco delle strade
	 */
	public List<Strada> getStrade(){
		return strade;
	}
	
	/**
	 * @return Elenco delle regioni
	 */
	public List<Regione> getRegioni(){
		return regioni;
	}
	
	/**
	 * Converte da una stringa alla regione corrispondente a quella stringa.
	 * @param nomeregione Nome della regione di cui si vuole avere il riferimento.
	 * @return Riferimento regione il cui tipo corrisponde alla stringa in ingresso.
	 */
	public List<Regione> getRegioniByString(TipoTerreno terrenoRegione) {
		List<Regione> listaRegioni = new ArrayList<Regione>();
		for(Regione regione : getRegioni()) {
			if(regione.getTipo().equals(terrenoRegione)) {
				listaRegioni.add(regione);
			}
		}
		return listaRegioni;
	}
	
	/** Metodo per trovare l'indice del giocatore nella lista dei giocatori,
	 * usato per semplificare il codice nel controller
	 * @param giocatore Giocatore di cui trovare l'indice nella lista dei giocatori
	 * @return Indice del giocatore passato per parametro
	 */
	public int getIndex(Giocatore giocatore) {
		/*
		 * trovo l'indice del giocatore corrente all'interno della lista dei giocatori
		 */
		return giocatori.indexOf(giocatore);
	}
	
	/**
	 * Calcola chi è il giocatore del prossimo turno, prelevando il giocatore nell'array circolare dei giocatori
	 * @return Prossimo giocatore
	 */
	public Giocatore getNextGamer() {
		int indice=getIndex(giocatoreCorrente);
		/*
		 * Incremento l'indice a cui si trova il giocatore facendone il modulo rispetto alla lunghezza della
		 * lista di giocatori
		 */
		indice=(indice+1)%giocatori.size();
		return giocatori.get(indice);
	}
	
	/**
	 * Ritorna le regioni adiacenti ad una strada data
	 * @param strada Strada di cui cercare le regioni adiacenti
	 * @return Lista di regioni che sono adiacenti alla strada passata come parametro
	 */
	public List<Regione> getRegioniADStrada(Strada strada){
		Set<DefaultWeightedEdge> archi = mappa.edgesOf(strada);
		/*
		 * itero sugli archi che partono dalla strada passata come parametro
		 * per ogni arco dovrò vedere la destinazione e source e verificare se è una regione e se lo è,
		 * che sia diversa da quella passata come parametro
		 */
		List<Regione> reg = new ArrayList<Regione>();
		for(DefaultWeightedEdge arc : archi){
			VerticeGrafo destArco=mappa.getEdgeSource(arc);
			if(destArco.isRegione()){
				reg.add((Regione) destArco);
			}
			destArco = mappa.getEdgeTarget(arc);
			if(destArco.isRegione()){
				reg.add((Regione) destArco);
			}
		}
		return reg;
	}
	
	/**
	 * Ritorna la regione corrispondente all'ID passato come parametro
	 * @param id ID della Regione che si sta cercando
	 * @return Istanza della regione che si sta cercando
	 */
	public Regione getRegioneByID(String id){
		for(Regione r:regioni){
			if(r.getColore().equals(id)){
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Ritorna la strada corrispondente all'ID passato come parametro
	 * @param id ID della strada che si sta cercando
	 * @return Istanza della strada che si sta cercando
	 */
	public Strada getStradaByID(String id){
		for(Strada s: strade){
			if(s.getColore().equals(id)){
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Trova il percorso minimo tra due strade passando solo su strade
	 * @param start Strada di Partenza
	 * @param end Strada di arrivo
	 * @return Lista delle strade da attraversare
	 */
	public List<Strada> dijkstraTraStrade(Strada start, Strada end){
		//Prelevo il percorso più corto tra due strade, usando Dijkstra fornito dalla libreria JGraphT
		List<DefaultWeightedEdge> archi = DijkstraShortestPath.findPathBetween(mappa, start, end);
		//Ora dobbiamo costruirci la lista dei vertici (strade) bisogna attraversare
		//Ci costruiamo la lista di vertici che bisogna attraversare
		List<Strada> vertici = new ArrayList<Strada>();
		VerticeGrafo src = start;
		//inizio aggiungendo alla lista il vertice di inizio
		vertici.add(start);
		for (DefaultWeightedEdge e : archi) {
			/*
			 * Per ogni arco del percorso, prelevo il vertice opposto al quello della precedente iterazione,
			 * se questo vertice è una strada, lo aggiungo alla lista dei vertici
			 */
			//------------DEBUG------------
			System.out.println(src.toString()); 
			System.out.println(e.toString());
			//-----------------------------
			src = Graphs.getOppositeVertex(mappa, e, src) ;
			if(!src.isRegione()){
				vertici.add((Strada) src);
			}
		}
		//------------DEBUG----------
		System.out.println("\n");
		for(Strada s : vertici){
			System.out.println(s.toString());
		}
		//-----------------------------
		//Ritorno la lista dei vertici che bisogna attraversare, compreso inizio e fine del percorso
		return vertici;
		
	}
}