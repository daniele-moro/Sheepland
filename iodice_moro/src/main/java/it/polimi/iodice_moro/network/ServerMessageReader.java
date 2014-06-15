package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.Controller;
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
	
	private Controller controller;
	private BufferedReader input;
	private Socket socket;
	private PrintWriter output;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	public ServerMessageReader(Controller controller, Socket socket, PrintWriter output, BufferedReader input) {
		this.controller=controller;
		this.socket=socket;
		this.input=input;
		this.output=output;
		//this.input=new BufferedReader(socket.getInputStream());
	}

	@Override
	public void run() {
		while(!socket.isClosed()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				logger.log(Level.SEVERE, "Errore nella thread.sleep", e2);
				//e2.printStackTrace();
			}
			try {
				if(input.ready())
				{
					System.out.println("Attesa input da stream ");
					String mossa = input.readLine();
					String [] parametri=mossa.split("#");
					System.out.println(mossa);
					switch(parametri[0]){

					case "COMPRA_TESSERA":
						try {
							System.out.println("Acquista Tessera");
							controller.acquistaTessera(parametri[1]);
						} catch (IllegalClickException e) {
							logger.log(Level.SEVERE, "Area non clickabile", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						} catch (NotAllowedMoveException e) {
							logger.log(Level.SEVERE, "Mossa proibita", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;

					case "SELEZ_POSIZ":
						System.out.println("SELEZ_POSIZ SERVER");
						try {
							controller.setStradaGiocatore(new Color(Integer.parseInt(parametri[1])), parametri[2]);
							//giocaRisposta.println("OK#"+"Inserimento avvenuto con successo");
						} catch (NotAllowedMoveException e1) {
							logger.log(Level.SEVERE, "Mossa proibita", e1);
							System.out.println("eccezione");
							output.println("EXCEPTION#"+e1.getMessage());
						}
						break;

					case "SPOSTA_PASTORE":
						try {
							System.out.println("spostaPastore");
							controller.spostaPedina(parametri[1]);
						} catch (IllegalClickException e) {
							logger.log(Level.SEVERE, "Area non clickabile", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						} catch (NotAllowedMoveException e) {
							logger.log(Level.SEVERE, "Mossa probita", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;

					case "SPOSTA_PECORA":
						try {
							System.out.println("sposta Pecora");
							controller.spostaPecora(parametri[1]);
						} catch (NotAllowedMoveException e) {
							logger.log(Level.SEVERE, "Mossa proibita", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;

					case "SPOSTA_PECORA_NERA":
						try {
							System.out.println("sposta pecora nera");
							controller.spostaPecoraNera(parametri[1]);
						} catch (NotAllowedMoveException e) {
							logger.log(Level.SEVERE, "Mossa proibita", e);
							output.println("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;

					case "MOSSA_POSSIBILE":
						try {

							Boolean risp = controller.mossaPossibile(TipoMossa.parseInput(parametri[1]));
							System.out.println("mossapossibile   "+ risp.toString());
							output.println("OK#"+risp.toString());
						} catch (RemoteException e) {
							logger.log(Level.SEVERE, "Errore di rete", e);
							System.out.println("ERRORE nella mossa possibile. Problemi di rete!!");
							output.println("ERROR#"+e.getMessage()+"\n");
						}
						break;

					/*case "INIZIA_PARTITA":
						//devo attendere che tutti i giocatori siano pronti a giocare
						cont++;
						if(cont==socketGiocatori.size()){
							cont=0;
							controller.iniziaPartita();
						}
						break;

					case "POS_STRADE":
						System.out.println("POS_STRADE");
						for(Entry<String,Point> str : controller.getPosStrade().entrySet()){
							giocaRisposta.println(str.getKey()+"#"+str.getValue().x+"#"+str.getValue().y);
							giocaRisposta.flush();
						}
						giocaRisposta.println("END");
						break;

					case "POS_REGIONI":
						System.out.println("POS_REGIONI");
						for(Entry<String,Point> reg : controller.getPosRegioni().entrySet()){
							giocaRisposta.println(reg.getKey()+"#"+reg.getValue().x+"#"+reg.getValue().y);
							giocaRisposta.flush();
						}
						giocaRisposta.println("END");
						break;

					case "GET_GIOCATORI":
						System.out.println("GET GIOCATORI");
						for(Entry<Color,String> reg : controller.getGiocatori().entrySet()){
							giocaRisposta.println(reg.getKey().getRGB()+"#"+reg.getValue());
							giocaRisposta.flush();
						}
						giocaRisposta.println("END");
						break;	*/
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
					output.flush();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Errore di IO", e);
				//e.printStackTrace();
			}
		}
		System.out.println("Chiusura thread di attesa dei comandi del client");
		

	}

}
