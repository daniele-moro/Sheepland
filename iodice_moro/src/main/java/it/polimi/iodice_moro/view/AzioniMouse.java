package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.model.TipoMossa;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Questa classe implementa gli eventi collegati alla mappa,
 *  i movimenti del mouse ed i click sulla mappa vengono intercettati dagli eventi qui implementati
 *
 */
class AzioniMouse extends MouseAdapter{
	//Regioni da evidenziare
	private String reg1;
	private String reg2;

	/**
	 * Riferimenti alla view e al controller
	 */
	View view;
	IFController controller;
	
	//Uso prevPos per memorizzarmi il colore della posizione precedente, 
	//altrimenti ogni micromovimento del mouse verrebbe invocato il metod.o di flashing della regione
	private int prevPos;
	
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.view");

	/**
	 * Questa immagine è l'immagine utilizzata per vedere in quale regione/strada della mappa il giocatore ha cliccato
	 */
	BufferedImage image;
	
	/**
	 * Costruttore che inizializza l'immagine, ed assegna i riferimenti alla view ed al controller
	 * @param image InputStream collegato all'immagine usata per riferirsi alle regioni/strade
	 * @param view Riferimento alla view collegata a questo listener
	 * @param controller Riferimento al controller su cui la classe deve chiamare i metodi
	 */
	public AzioniMouse(InputStream image, View view, IFController controller){
		super();
		reg1="";
		reg2="";
		try {
			this.image= ImageIO.read(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Errore di IO", e);
		}
		this.view=view;
		this.controller=controller;
		prevPos=0;
	}

	/**
	 * Collegamento al click del mouse
	 */
	public void mouseClicked(MouseEvent e){
		//Controllo che la zona cliccata rientri nell'immagine a cui fare riferimento per i colori
		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
			return;
		}
		//prelevo il colore associato al click del mouse
		int color=image.getRGB(e.getX(),e.getY());
		System.out.println("X:"+e.getX()+" Y:"+e.getY() + "  COLOR:0x"+ Integer.toHexString(color));
		
		//Ora in base alla mossa attuale e quindi al momento in cui si trova la partita, 
		//devo chiamare i metodi del controller
		
		//CASO 1: Selezione del pastore nel caso di due giocatori
		if(view.getMossaAttuale().equals(TipoMossa.G2_SELEZ_PAST) 
				&& view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)) 
				&& view.getGiocatoreCorrente().equals(view.getColoreGamer())){
			//in questo caso sto selezionando quale dei due pastori voglio usare (sono nel caso di due giocatori)
			final int c1=color;
			Thread t4 = new Thread( new Runnable(){
				@Override
				public void run(){
					try {
						//provo a comunicare quale pastore voglio usare
						controller.cambiaPastore(Integer.toHexString(c1));
					} catch (IllegalClickException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						LOGGER.log(Level.SEVERE, "Area non clickabile", e1);
					} catch (RemoteException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						LOGGER.log(Level.SEVERE, "Errore di rete", e1);
					}
				} 
			});
			t4.start();
		}

		//CASO 2: Selezione della posizione iniziale dei pastori
		if(view.getMossaAttuale().equals(TipoMossa.SELEZ_POSIZ) 
				&& view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)) 
				&& view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			
			final int c1=color;
			//STO SELEZIONANDO LE POSIZIONI DEI PASTORI
			
			//Metto il pastore nella posizione che ho appena selezionato
			//Creo thread per evitare problemi con i thread in cui gira la GUI
			Thread t4 = new Thread( new Runnable(){
				@Override
				public void run(){
					try {
						//provo a settare la strada del Pastore
						controller.setStradaGiocatore(view.getGiocatoreCorrente(), Integer.toHexString(c1));
					} catch (IllegalClickException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						LOGGER.log(Level.SEVERE, "Area non clickabile", e1);
					} catch (NotAllowedMoveException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						LOGGER.log(Level.SEVERE, "Mossa proibita", e1);
					} catch (RemoteException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						LOGGER.log(Level.SEVERE, "Errore di rete", e1);
					}
				} 
			});
			t4.start();
		}

		//CASO 3: MOSSA NORMALE
		//Controllo che il click sia avvenuto all'interno della mappa(quindi o su regioni o su caselle),
		//inoltre controllo che la mossa da fare non sia ne NO_MOSSA(in cui non si deve fare null) 
		//ne SELEZ_POSIZ(caso gia gestito prima)
		if((view.getPosizioniRegioni().keySet().contains(Integer.toHexString(color))
				|| view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)))
				&& view.getMossaAttuale()!=TipoMossa.NO_MOSSA
				&& view.getMossaAttuale()!=TipoMossa.SELEZ_POSIZ
				&& view.getMossaAttuale()!=TipoMossa.G2_SELEZ_PAST){
			//&&view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			try{
				//ricontrollo per sicurezza che la mossa da fare sia possibile
				if(controller.mossaPossibile(view.getMossaAttuale())){
					final int c=color;
					switch(view.getMossaAttuale()){

					//MOSSA: COMPRA TESSERA
					case COMPRA_TESSERA:
						Thread t = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									//effettuo la mossa nel controller, il quale controlla che l'acquisto sia possibile
									//in caso contrario torna un eccezione
									controller.acquistaTessera(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									LOGGER.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t.start();
						break;

					//MOSSA: SPOSTA PASTORE
					case SPOSTA_PASTORE:{
						Thread t1 = new Thread(new Runnable(){

							@Override
							public void run() {
								try {
									//effettuo la mossa sul controller, il quale controlla se il movimento è possibile
									//in caso contrario genera una eccezione
									controller.spostaPedina(Integer.toHexString(c));
								} catch (NotAllowedMoveException e1) {
									view.getLBLOutput().setText(e1.getMessage());
									LOGGER.log(Level.SEVERE, "Mossa proibita", e1);
								} catch (IllegalClickException e1) {
									view.getLBLOutput().setText(e1.getMessage());
									LOGGER.log(Level.SEVERE, "Area non clickabile", e1);
								} catch (RemoteException e) {
									LOGGER.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();

					}break;

					//MOSSA:SPOSTA PECORA (con gestione anche dello spostamento della pecora nera
					case SPOSTA_PECORA:{
						Point posPecoraNera=view.getLBLPecoraNera().getLocation();
						//Controllo se nella regione è presente anche la pecora nera
						if(image.getRGB((int)posPecoraNera.getX()+10,(int)posPecoraNera.getY()+10)==color){
							//In questo caso nel terreno c'è anche la pecora nera,
							//quindi bisogna far scegliere all'utente cosa spostare
							Object[] options = {new ImageIcon(this.getClass().getClassLoader().getResource("immagini/pecora_bianca.png")),
									new ImageIcon(this.getClass().getClassLoader().getResource("immagini/pecora_nera.png"))};
							int n = JOptionPane.showOptionDialog(null,
									"Quale pecora vuoi spostare?",
									"Spostamento Pecora",
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									new ImageIcon(this.getClass().getClassLoader().getResource("immagini/question.png")),
									options,
									options[0]);
							switch(n){
							case 0:{
								//Pecora Bianca
								Thread t3 = new Thread(new Runnable(){

									@Override
									public void run() {
										try {
											//Effettuo lo spostamento della pecora bianca sul controller
											//in caso di problemi genera eccezione
											controller.spostaPecora(Integer.toHexString(c));
										} catch (RemoteException e) {
											view.getLBLOutput().setText(e.getMessage());
											LOGGER.log(Level.SEVERE, "Errore di rete", e);
										} catch (NotAllowedMoveException e) {
											view.getLBLOutput().setText(e.getMessage());
										} catch (IllegalClickException e) {
											view.getLBLOutput().setText(e.getMessage());
											LOGGER.log(Level.SEVERE, "Area non clickabile", e);
										}
									}
								});
								t3.start();
							}break;
							case 1:{
								//Pecora Nera
								Thread t3 = new Thread(new Runnable(){
									@Override
									public void run() {
										try {
											//effettuo il movimento della pecora nera sul controller
											//nel caso di problemi vengono generate eccezioni
											controller.spostaPecoraNera(Integer.toHexString(c));
										} catch (RemoteException e) {
											view.getLBLOutput().setText(e.getMessage());
											LOGGER.log(Level.SEVERE, "Errore di rete", e);
										} catch (NotAllowedMoveException e) {
											view.getLBLOutput().setText(e.getMessage());
										}
									}
								});
								t3.start();
							}break;
							}
						}
						else{
							//Spostamento normale della pecora nera
							//Creo il thread su cui avviene il movimento
							Thread t3 = new Thread(new Runnable(){
								@Override
								public void run() {
									try {
										//effettuo il movimento della pecora sul controller
										//nel caso di problemi genera eccezioni
										controller.spostaPecora(Integer.toHexString(c));
									} catch (RemoteException e) {
										view.getLBLOutput().setText(e.getMessage());
										LOGGER.log(Level.SEVERE, "Errore di rete", e);
									} catch (NotAllowedMoveException e) {
										view.getLBLOutput().setText(e.getMessage());
									} catch (IllegalClickException e) {
										view.getLBLOutput().setText(e.getMessage());
										LOGGER.log(Level.SEVERE, "Area non clickabile", e);
									}
								}
							});
							t3.start();
						}
					}break;
					
					//MOSSA: ACCOPPIAMENTO 1
					case ACCOPPIAMENTO1:{
						
						Thread t1 = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									//effettuo la mossa sul controller
									controller.accoppiamento1(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									LOGGER.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();
					}break;
					
					//MOSSA: SPARATORIA 1
					case SPARATORIA1:{
						Thread t1 = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									//Effettuo la mossa sul controller
									controller.sparatoria1(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									LOGGER.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									LOGGER.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();
						
					}break;
					
					//MOSSA: SPARATORIA 2
					case SPARATORIA2:{
						Thread t1 = new Thread( new Runnable() {
							@Override
							public void run() {
								try {
									//Effettuo la mossa sul controller
									controller.sparatoria2(Integer.toHexString(c));
								} catch (RemoteException e) {
									LOGGER.log(Level.SEVERE, "Errore di rete", e);
								} catch (IllegalClickException e) {
									view.getLBLOutput().setText(e.getMessage());
									LOGGER.log(Level.SEVERE, "Area non clickabile", e);
								} catch (NotAllowedMoveException e) {
									view.getLBLOutput().setText(e.getMessage());
									LOGGER.log(Level.SEVERE, "Mossa proibita", e);
								}
								
							}
						});
						t1.start();
					}break;
					
					default:
						break;

					}
					//Effettuata la mossa, imposto il fatto che non ci siano mosse da effettuare in questo momento
					view.setMossaAttuale(TipoMossa.NO_MOSSA);
					setRegioni("","");
				}
			}catch(RemoteException e1){
				LOGGER.log(Level.SEVERE, "Errore di rete", e1);
			}
		}
	}
	
	
	/**
	 * Evento di movimento del mouse all'interno della mappa
	 */
	public void mouseMoved(MouseEvent e){
		JLabel lbl=(JLabel) e.getComponent();

		//Controllo per sicurezza che il movimento avvenga all'interno dell'immagine che abbiamo per i riferimenti
		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
			lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		//Memorizzo il colore della regione/strada che ho cliccato, riferendomi all'immagine salvata
		int color=image.getRGB(e.getX(),e.getY());
		//Controllo se il cursore è in una regione tra quelle che devo evidenziare
		if(color!=prevPos){
			if(Integer.toHexString(color).equals(reg1) || Integer.toHexString(color).equals(reg2)){
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				FlashLabel lblFlash= (FlashLabel) view.getLBLRegione(Integer.toHexString(color));
				//Controllo che la label non stia già flashando, per farla flashare
				if(!lblFlash.isFlashing()){
					lblFlash.flash(1);
				}
			}else{
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		prevPos=color;
	}

	/**
	 * Metodo usato per settare le regioni adiacenti alla posizione del pastore,
	 * queste regioni alla selezione, vengono fatte flashare per 3 volte
	 * @param reg1 Id della prima regione
	 * @param reg2 Id della seconda regione
	 */
	public void setRegioni(String reg1, String reg2){
		this.reg1=reg1;
		this.reg2=reg2;
		//se le due regioni sono effettivamente regioni, allora le faccio flashare
		if(!reg1.equals("") || !reg2.equals("")){
			//controllo prima che le label non stiano gia flashando
			FlashLabel lblReg1 = (FlashLabel)view.getLBLRegione(reg1);
			FlashLabel lblReg2 = (FlashLabel)view.getLBLRegione(reg2);
			if(!lblReg1.isFlashing()){
				lblReg1.flash(3);
			}
			if(!lblReg2.isFlashing()){
				lblReg2.flash(3);
			}
		}

	}
}
