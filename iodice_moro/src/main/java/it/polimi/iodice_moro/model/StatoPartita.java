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
public class StatoPartita {
	
	private static final String FILE_GRAFO = "grafo.xml";
	
	//ATTRIBUTI
	public int numRecinti;
	public boolean turnoFinale;
	
	//Attributo per gestire l'istanza singleton.
	private static StatoPartita instance = null;
	
	//Costruttore fittizio. Prima di chiamare il costruttore controlla 
	//se esiste già istanza di StatoPartita.
	public static StatoPartita getInstance() {
		if (instance == null)
			instance = new StatoPartita();
		return instance;
			}
	
	
	
	//Grafo che rappresenta la mappa, i vertici del grafo sono sia strade che regioni
	//gli archi sono tra Regioni e Strade (regioni che hanno come confine la strada) e tra strade(tra strade adiacenti)
	private UndirectedGraph<VerticeGrafo,DefaultEdge> mappa;
	private ArrayList<Strada> strade= new ArrayList<Strada>();
	private ArrayList<Regione> regioni= new ArrayList<Regione>();
	
	//parser del file XML contenente la mappa
	private void parseMappaXML(String pathFile){
		Map<Integer,VerticeGrafo> nodi = new HashMap<Integer, VerticeGrafo>();
		try{
			//SAXBuilder usato per creare oggetti JDOM2
			SAXBuilder jdomBuilder = new SAXBuilder();
			
			//Apertura del file xml in cui e' contenuta la mappa
			File xmlFile = new File(pathFile);

			//jdomDocument e' l'oggetto JDOM2
			Document jdomDocument = (Document) jdomBuilder.build(xmlFile);
			//Prelevo il nodo ROOT
			Element rootNode=jdomDocument.getRootElement();

			//itero sulle regioni
			for (Element i : rootNode.getChildren("regione")){
				int id= Integer.parseInt(i.getAttributeValue("id"));
				String tipo = i.getAttributeValue("tipo");
				//Istanzio la nuova regione
				Regione nuovaReg= new Regione(tipo);
				//la aggiungo alla hashmap dei nodi
				nodi.put(id,nuovaReg);
				regioni.add(nuovaReg);
				//la aggiungo al grafo
				mappa.addVertex(nuovaReg);
			}

			//itero sulle strade
			for (Element i : rootNode.getChildren("strada")){
				int id= Integer.parseInt(i.getAttributeValue("id"));
				int ncas = Integer.parseInt(i.getAttributeValue("ncasella"));
				//Istanzio la nuova strada
				Strada nuovaStr= new Strada(ncas);
				//la aggiungo alla hashmap dei nodi
				nodi.put(id,nuovaStr);
				strade.add(nuovaStr);
				//la aggiungo al grafo
				mappa.addVertex(nuovaStr);
			}


			//itero sui link tra strade
			for (Element i : rootNode.getChildren("arco")){
		//		int id= Integer.parseInt(i.getAttributeValue("id")); //ID dell'arco non usato
				//Memorizzo id del source e della destinazione
				int idSource = Integer.parseInt(i.getAttributeValue("source"));
				int idDest = Integer.parseInt(i.getAttributeValue("dest"));
				
				//prelevo dalla hashmap gli oggetti correlati a quell'id
				VerticeGrafo nodoSource = nodi.get(idSource);
				VerticeGrafo nodoDest = nodi.get(idDest);
				
				//Aggiungo al grafo l'arco corrispondente
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

	private void caricaGrafo(){
		parseMappaXML(FILE_GRAFO);
	}
	
	
	public StatoPartita() {
		numRecinti=0;
		turnoFinale=false;
		mappa= new SimpleGraph<VerticeGrafo,DefaultEdge>(DefaultEdge.class);
		caricaGrafo();
	}
	
	//GETTERS & SETTERS	
	public int getNumRecinti() {
		return numRecinti;
	}

	public void setNumRecinti(int numRecinti) {
		this.numRecinti = numRecinti;
	}

	public boolean isTurnoFinale() {
		return turnoFinale;
	}

	public void setTurnoFinale(boolean turnoFinale) {
		this.turnoFinale = turnoFinale;
	}

	//Metodo che ritorna le strade adiacenti ad una strada data
	private ArrayList<Strada> getStradeAdiacenti(Strada strada){
		//Prelevo tutti gli archi che partono dalla strada di cui devo trovare le adiacenti
		Set<DefaultEdge> archi =mappa.edgesOf(strada);
		ArrayList<Strada> stradeAdiacenti = new ArrayList<Strada>();
		
		//itero sugli archi ottenuti
		for(DefaultEdge arc : archi){
			//per ogni arco prelevo la sorgente dell'arco
			VerticeGrafo destArco = mappa.getEdgeSource(arc);
			//controllo che il vertice sia una regione
			if(!destArco.isRegione() && destArco!=strada){
				//Se e' una regione la memorizzo nell'array delle strade adiacenti
				stradeAdiacenti.add((Strada) destArco);
			}
			
			//stesso ragionamento di prima solo per la destinazione dell'arco
			destArco = mappa.getEdgeTarget(arc);
			if(!destArco.isRegione() && destArco!=strada){
				stradeAdiacenti.add((Strada)destArco);
			}	
		}
		
		return stradeAdiacenti;
	}
	public static void main(String[] args){
		//Test per verificare il caricamento della mappa da XML e il prelievo delle strade adiacenti
		StatoPartita stato = new StatoPartita();
		System.out.println("Adiacenze"+stato.getStradeAdiacenti(stato.strade.get(0)).toString());
	}
	
	
	

}
