package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.awt.Color;
import java.util.Map;

public interface IFView {
	
	//INIZIALIZZA TUTTI GLI OGGETTI CHE SONO POSIZIONATI SOPRA LA MAPPA E I GIOCATORI
	public abstract void initMappa();

	/**
	 * Cambio giocatore attivo, attivando la label corrispondente al giocatore che deve giocare
	 * @param color Colore del giocatore che deve giocare
	 */
	public abstract void cambiaGiocatore(Color color);

	/**
	 * Attiva il giocatore collegato alla schermata di questa istanza
	 */
	public abstract void attivaGiocatore();

	/**
	 * Disattiva il giocatore collegato alla schermata di questa istanza
	 */
	public abstract void disattivaGiocatore();

	/**
	 * Aggiunta del cancello normale alla strada collegata all'ID
	 * @param stradaID
	 */
	public abstract void addCancelloNormale(String stradaID);

	/**
	 * Aggiunta del cancello finale alla strada collegata all'ID
	 * @param stradaID
	 */
	public abstract void addCancelloFinale(String stradaID);

	/**
	 * Animazione di spostamento della pecora bianca
	 * @param s Id della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 */
	public abstract void spostaPecoraBianca(String s, String d);

	public abstract void spostaPastore(String s, String d, Color colore);

	/**
	 * Animazione di spostamento della pecora nera
	 * @param s ID della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 */
	public abstract void spostaPecoraNera(String s, String d);

	/**
	 * Cambia il numero di pecore della regione con ID passato come parametro
	 * @param idReg Id della regione di cui cambiare il numero di pecore
	 * @param num Numero di pecore presenti nella regione
	 */
	public abstract void modificaQtaPecora(String idReg, int num);

	/**
	 * Cambia il numero di tessere del terreno passato come parametro
	 * @param tess Terreno di cui cambiare il numero di tessere
	 * @param num Numero di tessere di quel terreno
	 */
	public abstract void modQtaTessera(TipoTerreno tess, int num);

	public abstract void modSoldiGiocatore(Color coloreGiocatoreDaModificare,
			int soldi);

	public abstract void incPrezzoTessera(TipoTerreno tess);

	public abstract void visualizzaPunteggi(
			Map<Giocatore, Integer> punteggiOrdinati);

	/**
	 * Serve per settare, da parte del controller il giocatore corrente
	 * @param colore
	 */
	public abstract void setGiocatoreCorrente(Color colore);

}