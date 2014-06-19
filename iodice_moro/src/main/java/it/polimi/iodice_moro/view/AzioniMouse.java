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

class AzioniMouse extends MouseAdapter{
	//Regioni da evidenziare
	private String reg1;
	private String reg2;

	View view;
	IFController controller;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.view");

	BufferedImage image;
	public AzioniMouse(InputStream image, View view, IFController controller){
		super();
		reg1="";
		reg2="";
		try {
			this.image= ImageIO.read(image);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Errore di IO", e);
		}
		this.view=view;
		this.controller=controller;
		prevPos=0;
	}

	//Evento di click del mouse
	public void mouseClicked(MouseEvent e)
	{
		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
			return;
		}
		int color=image.getRGB(e.getX(),e.getY());
		System.out.println("X:"+e.getX()+" Y:"+e.getY() + "  COLOR:0x"+ Integer.toHexString(color));
		
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
						logger.log(Level.SEVERE, "Area non clickabile", e1);
					} catch (RemoteException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						logger.log(Level.SEVERE, "Errore di rete", e1);
					}
				} 
			});
			t4.start();
		}

		if(view.getMossaAttuale().equals(TipoMossa.SELEZ_POSIZ) 
				&& view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)) 
				&& view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			
			final int c1=color;
			//STO SELEZIONANDO LE POSIZIONI DEI PASTORI
			System.out.println("SELEZPOSIZ");
			//Metto il pastore nella posizione che ho appena selezionato
			Thread t4 = new Thread( new Runnable(){
				@Override
				public void run(){
					try {
						//provo a settare la strada del Pastore
						controller.setStradaGiocatore(view.getGiocatoreCorrente(), Integer.toHexString(c1));
					} catch (IllegalClickException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						logger.log(Level.SEVERE, "Area non clickabile", e1);
					} catch (NotAllowedMoveException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						logger.log(Level.SEVERE, "Mossa proibita", e1);
					} catch (RemoteException e1) {
						view.getLBLOutput().setText( e1.getMessage());
						logger.log(Level.SEVERE, "Errore di rete", e1);
					}
				} 
			});
			t4.start();
		}

		//Controllo che il click sia avvenuto all'interno della mappa(quindi o su regioni o su caselle),
		//inoltre controllo che la mossa da fare non sia ne NO_MOSSA(in cui non si deve fare null) 
		//ne SELEZ_POSIZ(caso gia gestito prima)
		if((view.getPosizioniRegioni().keySet().contains(Integer.toHexString(color))
				|| view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)))
				&& view.getMossaAttuale()!=TipoMossa.NO_MOSSA
				&& view.getMossaAttuale()!=TipoMossa.SELEZ_POSIZ
				&& view.getMossaAttuale()!=TipoMossa.G2_SELEZ_PAST){
			//&&view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			System.out.println("A");
			//ricontrollo per sicurezza che la mossa da fare sia possibile
			try{
				if(controller.mossaPossibile(view.getMossaAttuale())){
					System.out.println("B");
					final int c=color;
					switch(view.getMossaAttuale()){

					case COMPRA_TESSERA:
						System.out.println("COMPRA TESSERA");
						Thread t = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									controller.acquistaTessera(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									logger.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t.start();
						break;


					case SPOSTA_PASTORE:{
						Thread t1 = new Thread(new Runnable(){

							@Override
							public void run() {
								try {
									controller.spostaPedina(Integer.toHexString(c));
								} catch (NotAllowedMoveException e1) {
									view.getLBLOutput().setText(e1.getMessage());
									logger.log(Level.SEVERE, "Mossa proibita", e1);
								} catch (IllegalClickException e1) {
									view.getLBLOutput().setText(e1.getMessage());
									logger.log(Level.SEVERE, "Area non clickabile", e1);
								} catch (RemoteException e) {
									logger.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();

					}break;


					case SPOSTA_PECORA:{
						Point posPecoraNera=view.getLBLPecoraNera().getLocation();
						if(image.getRGB((int)posPecoraNera.getX()+10,(int)posPecoraNera.getY()+10)==color){
							System.out.println("PECORA NERA");
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
											controller.spostaPecora(Integer.toHexString(c));
										} catch (RemoteException e) {
											System.out.println("PROBLEMI DI RETE!!");
											view.getLBLOutput().setText(e.getMessage());
											logger.log(Level.SEVERE, "Errore di rete", e);
										} catch (NotAllowedMoveException e) {
											System.out.println("ERRORE CLICK MAPPA!!");
											view.getLBLOutput().setText(e.getMessage());
										} catch (IllegalClickException e) {
											view.getLBLOutput().setText(e.getMessage());
											logger.log(Level.SEVERE, "Area non clickabile", e);
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
											controller.spostaPecoraNera(Integer.toHexString(c));
										} catch (RemoteException e) {
											System.out.println("PROBLEMI DI RETE!!");
											view.getLBLOutput().setText(e.getMessage());
											logger.log(Level.SEVERE, "Errore di rete", e);
										} catch (NotAllowedMoveException e) {
											System.out.println("ERRORE CLICK MAPPA!!");
											view.getLBLOutput().setText(e.getMessage());
										}
									}
								});
								t3.start();
							}break;
							}
						}
						else{
							System.out.println("SPOSTAPECORA");
							Thread t3 = new Thread(new Runnable(){

								@Override
								public void run() {
									try {
										controller.spostaPecora(Integer.toHexString(c));
									} catch (RemoteException e) {
										System.out.println("PROBLEMI DI RETE!!");
										view.getLBLOutput().setText(e.getMessage());
										logger.log(Level.SEVERE, "Errore di rete", e);
									} catch (NotAllowedMoveException e) {
										System.out.println("ERRORE CLICK MAPPA!!");
										view.getLBLOutput().setText(e.getMessage());
									} catch (IllegalClickException e) {
										view.getLBLOutput().setText(e.getMessage());
										logger.log(Level.SEVERE, "Area non clickabile", e);
									}
								}
							});
							t3.start();
						}
					}break;
					
					case ACCOPPIAMENTO1:{
						System.out.println("ACCOPPIAMENTO 1");
						Thread t1 = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									controller.accoppiamento1(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									logger.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();

					}break;
					
					case SPARATORIA1:{
						System.out.println("SPARATORIA 1");
						Thread t1 = new Thread( new Runnable(){
							@Override
							public void run(){
								try {
									controller.sparatoria1(Integer.toHexString(c));
								} catch (NotAllowedMoveException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Mossa proibita", e2);
								} catch (IllegalClickException e2) {
									view.getLBLOutput().setText(e2.getMessage());
									logger.log(Level.SEVERE, "Area non clickabile", e2);
								} catch (RemoteException e) {
									logger.log(Level.SEVERE, "Errore di rete", e);
								}
							}
						});
						t1.start();
						
					}break;
					
					case SPARATORIA2:{
						System.out.println("SPARATORIA2");
						Thread t1 = new Thread( new Runnable() {
							@Override
							public void run() {
								try {
									controller.sparatoria2(Integer.toHexString(c));
								} catch (RemoteException e) {
									logger.log(Level.SEVERE, "Errore di rete", e);
								} catch (IllegalClickException e) {
									view.getLBLOutput().setText(e.getMessage());
									logger.log(Level.SEVERE, "Area non clickabile", e);
								} catch (NotAllowedMoveException e) {
									view.getLBLOutput().setText(e.getMessage());
									logger.log(Level.SEVERE, "Mossa proibita", e);
								}
								
							}
						});
						t1.start();
					}


					default:
						break;

					}
					view.setMossaAttuale(TipoMossa.NO_MOSSA);
					setRegioni("","");

				}
			}catch(RemoteException e1){
				logger.log(Level.SEVERE, "Errore di rete", e1);
			}

		}
	}

	//Uso prevPos per memorizzarmi il colore della posizione precedente, 
	//altrimenti ogni micromovimento del mouse verrebbe invocato il metodo di flashing della regione
	private int prevPos;
	
	//Evento di movimento del mouse all'interno del componente
	public void mouseMoved(MouseEvent e){
		JLabel lbl=(JLabel) e.getComponent();

		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
			lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		int color=image.getRGB(e.getX(),e.getY());
		//Controllo se il cursore è in una regione tra quelle che devo evidenziare
		if(color!=prevPos){
			if(Integer.toHexString(color).equals(reg1) || Integer.toHexString(color).equals(reg2)){
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				((FlashLabel)view.getLBLRegione(Integer.toHexString(color))).flash(1);
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
			((FlashLabel)view.getLBLRegione(reg1)).flash(3);
			((FlashLabel)view.getLBLRegione(reg2)).flash(3);
		}

	}
}
