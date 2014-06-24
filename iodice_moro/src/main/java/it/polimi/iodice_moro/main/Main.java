package it.polimi.iodice_moro.main;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.network.ControllerRMI;
import it.polimi.iodice_moro.network.ControllerSocket;
import it.polimi.iodice_moro.network.ViewRMI;
import it.polimi.iodice_moro.network.ViewSocket;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * Classe che si occupa di istanziare gli oggetti per il funzionamento.
 * @author Antonio Iodice
 * @author Daniele Moro
 */
public class Main {
	
	public Main(){
	}
	
	private static final String LOCALHOST = "127.0.0.1";
	private static final String DEFAULT_PORT = "12345";
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.main");
	public static final int TEMPO_ATTESA = 30;


	/**
	 * Metod.o avviato all'avvio del programma.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//CREO tutte le istanze che mi servono per far funzionare il gioco
		JFrame frame = new JFrame();
		
		StatoPartita statopartita= new StatoPartita();
		IFController controller;
		IFView view;
		String[] optionsModalita = {"Online","Offline"};
		String[] optionsRete = {"Client", "Server"};
		String[] optionsTipoRete = {"Socket", "RMI"};
		int sceltaTipoRete;
		String ip = "";
		String porta = "";
		String nome = "";
		sceltaTipoRete = JOptionPane.showOptionDialog(frame,
				"Vuoi giocare in modalità Socket o RMI?",
				"Scelta tipo rete",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				optionsTipoRete,
				optionsTipoRete[0]);

		int sceltaRete = JOptionPane.showOptionDialog(frame,
				"Vuoi essere client o server?",
				"Scelta modalità di gioco",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				optionsRete,
				optionsRete[0]);
		switch (sceltaRete) {
		//Client
		case 0:
			while("".equals(ip)) {
				ip = (String)JOptionPane.showInputDialog(
						frame,
						"Inserisci Ip",
						"Inserisci IP",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						LOCALHOST);
			}
			if(sceltaTipoRete==0) {
				while("".equals(porta)) {
					porta = (String)JOptionPane.showInputDialog(
							frame,
							"Inserisci Porta a cui connettersi",
							"Inserisci Porta",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							DEFAULT_PORT);
				}
			}
			while("".equals(nome)) {
				nome = (String)JOptionPane.showInputDialog(
						frame,
						"Nome",
						"Inserisci nome del tuo giocatore",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"");
			}
			//SocketClient
			if(sceltaTipoRete==0) {
				controller = new ControllerSocket(ip, Integer.parseInt(porta));
				Color colore=controller.creaGiocatore(nome);
				if(colore!=null){
					view = new View((ControllerSocket)controller);
					((View)view).setColore(colore);
					controller.setView(view);	
				} else{
					System.out.println("ERRORE DI CONNESSIONE");
					JOptionPane.showMessageDialog(frame,
							"Errore di connessione.\nPartita già iniziata. Non puoi connetterti.");
					frame.dispose();
					System.exit(0);
				}
			} else {

				//RMIClient
				controller = new ControllerRMI(ip);
				view = new View(controller);
				try {
					Color coloreGiocatore = controller.creaGiocatore(nome);
					((View)view).setColore(coloreGiocatore);
					controller.addView(view, coloreGiocatore);
				} catch(PartitaIniziataException e) {
					JOptionPane.showMessageDialog(frame,
							"Partita già iniziata. Non puoi connetterti.");
					frame.dispose();
					System.exit(0);
				}			
			}
			break;
			//Server
		case 1:
			//ServerSocket
			if(sceltaTipoRete==0) {
				while("".equals(porta)) {
					porta = (String)JOptionPane.showInputDialog(
							frame,
							"Inserisci Porta su cui mettersi in ascolto ",
							"Inserisci Porta",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							DEFAULT_PORT);
				}
				controller = new Controller(statopartita);
				final int porta2 = Integer.parseInt(porta);
				view = new ViewSocket((Controller)controller, Integer.parseInt(porta));
				//metto in attesa il server dei gioacatori
				controller.setView(view);
				
				//Visualizzo l'IP e la PORTA del SERVER a cui dovranno connettersi i client
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						try {
							JOptionPane.showMessageDialog(null, "INDIRIZZO IP: "+InetAddress.getLocalHost().getHostAddress()+"\n PORTA: "+porta2);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					
				});
				t.start();
				((ViewSocket)view).attendiGiocatori();
				break;
			} else {
				//ServerRMI
				try {
					LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					System.out.println("Registry giÃ  presente!");
					LOGGER.log(Level.SEVERE, "Registry già presente!", e);
				}	


				try {
					controller = new Controller(statopartita);
					ViewRMI viewRMI = new ViewRMI((Controller)controller);
					
					//Rebind dell'oggeto remoto, che nel nostro caso è il controller
					Naming.rebind("//"+ip+"/Server", controller);
					controller.setView(viewRMI);
					
					//Visualizzo l'IP del SERVER a cui dovranno connettersi i client
					Thread t = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								JOptionPane.showMessageDialog(null, "INDIRIZZO IP: "+InetAddress.getLocalHost().getHostAddress());
							} catch (UnknownHostException e) {
								LOGGER.log(Level.SEVERE, "Indirizzo inesistente", e);
							}
						}
					});
					t.start();
					
					//Attesa dei giocatori ed attesa di inizio della partita
					viewRMI.attendiGiocatori();						
					
				} catch (MalformedURLException e) {
					System.err.println("Impossibile registrare l'oggetto indicato!");
					LOGGER.log(Level.SEVERE, "Impossibile registrare l'oggetto indicato!", e);
				} catch (RemoteException e) {
					System.err.println("Errore di connessione: " + e.getMessage() + "!");
					LOGGER.log(Level.SEVERE, "Impossibile registrare l'oggetto indicato", e);
				}
			}

			break;

		default:
			throw new Exception();
		}
	}

}
