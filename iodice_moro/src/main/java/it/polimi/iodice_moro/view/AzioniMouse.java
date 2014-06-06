package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.TipoMossa;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
		JLabel lbl=(JLabel) e.getComponent();
		if(e.getX()< 0 || e.getY()<0 || e.getX()>image.getWidth() || e.getY()>image.getHeight()) return;
		int color=image.getRGB(e.getX(),e.getY());
		System.out.println("X:"+e.getX()+" Y:"+e.getY() + "  COLOR:0x"+ Integer.toHexString(color));
		if(view.getMossaAttuale().equals(TipoMossa.SELEZ_POSIZ) 
				&& view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color))){
			//STO SELEZIONANDO LE POSIZIONI DEI PASTORI
			System.out.println("SELEZPOSIZ");
			view.spostaPastore("", Integer.toHexString(color), view.getGiocatoreCorrente());
			try {
				controller.setStradaGiocatore(view.getGiocatoreCorrente(), Integer.toHexString(color));
			} catch (Exception e1) {
				view.getLBLOutput().setText( e1.getMessage());
			}
			
		}
				
		if((view.getPosizioniRegioni().keySet().contains(Integer.toHexString(color))
				|| view.getPosizioniCancelli().keySet().contains(Integer.toHexString(color)))
				&& view.getMossaAttuale()!=TipoMossa.NO_MOSSA
				&& view.getMossaAttuale()!=TipoMossa.SELEZ_POSIZ){
			System.out.println("A");
			if(controller.mossaPossibile(view.getMossaAttuale())){
				System.out.println("B");
				switch(view.getMossaAttuale()){
				case COMPRA_TESSERA:
					System.out.println("COMPRA TESSERA");
					try {
						controller.acquistaTessera(Integer.toHexString(color));
					} catch (Exception e2) {
						view.getLBLOutput().setText(e2.getMessage());
					}
					break;
				case SPOSTA_PASTORE:
					try {
						controller.spostaPedina(Integer.toHexString(color));
					} catch (Exception e1) {
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
								Object[] options = {new ImageIcon("immagini/pecora_bianca.png"),
										new ImageIcon("immagini/pecora_nera.png")};
								int n = JOptionPane.showOptionDialog(null,
								    "Quale pecora vuoi spostare?",
								    "Spostamento Pecora",
								    JOptionPane.YES_NO_CANCEL_OPTION,
								    JOptionPane.QUESTION_MESSAGE,
								    new ImageIcon("immagini/question-png"),
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
								controller.checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
							}
						}
					} catch (Exception e1) {
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
