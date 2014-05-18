package it.polimi.iodice_moro.model;

/**
 * Superclasse astratta delle classi Regione e Strada, usata per avere come vertici del grafo sia Regioni che Strade
 * (implementazione dovuto alla limitazione della libreria JGraphT che ammette un solo tipo per il Vertice)
 * @author Antonio Iodice, Daniele Moro
 *
 */
public abstract class VerticeGrafo {
	
	/**
	 * Metodo Abstract che verrà implemetnato nelle sottoclassi per verificare se
	 * l'istanza corrente è una regione o una strada
	 */
	public abstract boolean isRegione();

}
