package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.model.TipoMossa;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe del server che si occupa di ricevere i messaggi da un client e gestirli sul controller
 * @author m-daniele
 *
 */
public class ServerMessageReader implements Runnable {
	
	private IFController controller;
	private BufferedReader input;
	private Socket socket;
	private PrintWriter output;
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	public ServerMessageReader(IFController controller, Socket socket, PrintWriter output, BufferedReader input) {
		this.controller=controller;
		this.socket=socket;
		this.input=input;
		this.output=output;
	}

	@Override
	public void run() {
		while(!socket.isClosed()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				LOGGER.log(Level.SEVERE, "Errore nella thread.sleep", e2);
			}
			try {
				if(input.ready()){
					System.out.println("Attesa input da stream ");
					String mossa = input.readLine();
					String [] parametri=mossa.split("#");
					System.out.println(mossa);
					switch(parametri[0]){

					case "COMPRA_TESSERA":
						controller.acquistaTessera(parametri[1]);
						break;

					case "SELEZ_POSIZ":
						controller.setStradaGiocatore(new Color(Integer.parseInt(parametri[1])), parametri[2]);
						break;

					case "CAMBIA_PASTORE":
						controller.cambiaPastore(parametri[1]);
						break;

					case "SPOSTA_PASTORE":
						controller.spostaPedina(parametri[1]);
						break;

					case "SPOSTA_PECORA":
						controller.spostaPecora(parametri[1]);
						break;

					case "SPOSTA_PECORA_NERA":
						controller.spostaPecoraNera(parametri[1]);
						break;

					case "ACCOPPIAMENTO1":
						controller.accoppiamento1(parametri[1]);
						break;

					case "SPARATORIA1":
						controller.sparatoria1(parametri[1]);
						break;

					case "SPARATORIA2":
						controller.sparatoria2(parametri[1]);
						break;
						
					case "MOSSA_POSSIBILE":
						try {
							Boolean risp = controller.mossaPossibile(TipoMossa.parseInput(parametri[1]));
							System.out.println("mossapossibile   "+ risp.toString());
							output.println("OK#"+risp.toString());
						} catch (RemoteException e) {
							LOGGER.log(Level.SEVERE, "Errore di rete", e);
							System.out.println("ERRORE nella mossa possibile. Problemi di rete!!");
							output.println("ERROR#"+e.getMessage()+"\n");
						}
						break;

					case "GET_ID_REG_AD":
						List<String> reg= controller.getIDRegioniAd();
						for(String r : reg){
							output.println(r);
							output.flush();
						}
						output.println("END");
						break;

					case "END":
						socket.close();
						controller.end();
						break;
					default:
						break;
					}
					
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			} catch (IllegalClickException e) {
				LOGGER.log(Level.SEVERE, "Area non clickabile", e);
				output.println("EXCEPTION#"+e.getMessage()+"\n");
			} catch (NotAllowedMoveException e) {
				LOGGER.log(Level.SEVERE, "Mossa proibita", e);
				output.println("EXCEPTION#"+e.getMessage()+"\n");
			} finally{
				output.flush();
			}
		}
		System.out.println("Chiusura thread di attesa dei comandi del client");
	}
}
