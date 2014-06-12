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
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

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

	BufferedImage image;
	public AzioniMouse(File image, View view, IFController controller){
		super();
		reg1="";
		reg2="";
		try {
			this.image= ImageIO.read(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.view=view;
		this.controller=controller;
	}

	//Evento di click del mouse
	public void mouseClicked(MouseEvent e)
	{
		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
			return;
		}
		int color=image.getRGB(e.getX(),e.getY());
		System.out.println("X:"+e.getX()+" Y:"+e.getY() + "  COLOR:0x"+ Integer.toHexString(color));

		if(view.getMossaAttuale().equals(TipoMossa.SELEZ_POSIZ) 
				&& view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)) 
				&& view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			//STO SELEZIONANDO LE POSIZIONI DEI PASTORI
			System.out.println("SELEZPOSIZ");
			//Metto il pastore nella posizione che ho appena selezionato
			view.spostaPastore("", Integer.toHexString(color), view.getGiocatoreCorrente());
			try {
				//provo a settare la strada del Pastore
				controller.setStradaGiocatore(view.getGiocatoreCorrente(), Integer.toHexString(color));
			} catch (IllegalClickException e1) {
				view.getLBLOutput().setText( e1.getMessage());
			} catch (NotAllowedMoveException e1) {
				view.getLBLOutput().setText( e1.getMessage());
			} catch (RemoteException e1) {
				view.getLBLOutput().setText( e1.getMessage());
			}
		}

		//Controllo che il click sia avvenuto all'interno della mappa(quindi o su regioni o su caselle),
		//inoltre controllo che la mossa da fare non sia ne NO_MOSSA(in cui non si deve fare null) 
		//ne SELEZ_POSIZ(caso gia gestito prima)
		if((view.getPosizioniRegioni().keySet().contains(Integer.toHexString(color))
				|| view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)))
				&& view.getMossaAttuale()!=TipoMossa.NO_MOSSA
				&& view.getMossaAttuale()!=TipoMossa.SELEZ_POSIZ){
			//&&view.getGiocatoreCorrente().equals(view.getColoreGamer())){

			System.out.println("A");
			//ricontrollo per sicurezza che la mossa da fare sia possibile
			try{
				if(controller.mossaPossibile(view.getMossaAttuale())){
					System.out.println("B");
					switch(view.getMossaAttuale()){


					case COMPRA_TESSERA:
						System.out.println("COMPRA TESSERA");
						try {
							controller.acquistaTessera(Integer.toHexString(color));
						} catch (NotAllowedMoveException e2) {
							view.getLBLOutput().setText(e2.getMessage());
						} catch (IllegalClickException e2) {
							view.getLBLOutput().setText(e2.getMessage());
						}
						break;


					case SPOSTA_PASTORE:
						try {
							controller.spostaPedina(Integer.toHexString(color));
						} catch (NotAllowedMoveException e1) {
							view.getLBLOutput().setText(e1.getMessage());
						} catch (IllegalClickException e1) {
							view.getLBLOutput().setText(e1.getMessage());
						}
						break;


					case SPOSTA_PECORA:
						try {
							//if(Integer.toHexString(color).equals(reg1) || Integer.toHexString(color).equals(reg2))
							{
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
									case 0:
										//Pecora Bianca
										controller.spostaPecora(Integer.toHexString(color));
										break;
									case 1:
										//Pecora Nera
										controller.spostaPecoraNera(Integer.toHexString(color));
										break;
									}
								}
								else{
									System.out.println("SPOSTAPECORA");
									controller.spostaPecora(Integer.toHexString(color));
								}
							}
						} catch (RemoteException e1) {
							System.out.println("PROBLEMI DI RETE!!");
							view.getLBLOutput().setText(e1.getMessage());
						} catch (NotAllowedMoveException e1) {
							System.out.println("ERRORE CLICK MAPPA!!");
							view.getLBLOutput().setText(e1.getMessage());
						}
						break;


					default:
						break;

					}
					view.setMossaAttuale(TipoMossa.NO_MOSSA);
					setRegioni("","");

				}
			}catch(RemoteException e1){
				//TODO
				e1.printStackTrace();
			}
			System.out.println("Presente");

		}

		switch(color){
		case 0x00ffffff:
			System.out.println("VUOTO");
			break;
		case 0xff000000:
			System.out.println("STRADA");
			break;
		}
	}

		//Evento di movimento del mouse all'interno del componente
		public void mouseMoved(MouseEvent e){
			JLabel lbl=(JLabel) e.getComponent();

			if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()){
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
			int color=image.getRGB(e.getX(),e.getY());
			//	System.out.println("X:"+e.getX()+" Y:"+e.getY() + "  COLOR:0x"+ Integer.toHexString(color));

			//Controllo se il cursore è in una regione tra quelle che devo evidenziare
			if(Integer.toHexString(color).equals(reg1) || Integer.toHexString(color).equals(reg2)){
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}else{
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		public void setRegioni(String reg1, String reg2){
			this.reg1=reg1;
			this.reg2=reg2;
		}
	}
