package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.model.TipoMossa;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

class AzioniMouse extends MouseAdapter{
	//Regioni da evidenziare
	private String reg1;
	private String reg2;
	private int cont;

	View view;
	Controller controller;

	BufferedImage image;
	public AzioniMouse(File image, View view, Controller controller){
		super();
		reg1="";
		reg2="";
		cont=0;
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
				// TODO Auto-generated catch block
				view.getLBLOutput().setText("<html>" + e1+"</html>");
				view.getLBLOutput().setMaximumSize(new Dimension(100,20));
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
						view.getLBLOutput().setText("ERRORE nell'acquisto della tessera" + e2);
					}
					break;
				case SPOSTA_PASTORE:
					try {
						System.out.println("S");
						controller.spostaPedina(Integer.toHexString(color));
					} catch (Exception e1) {
						view.getLBLOutput().setText("Non puoi spostare qui il tuo pastore!!");
					}
					break;
				case SPOSTA_PECORA:
					try {
						System.out.println("B");
						if(Integer.toHexString(color).equals(reg1) || Integer.toHexString(color).equals(reg2))
						{
							System.out.println("SPOSTAPECORA");
							controller.spostaPecora(Integer.toHexString(color));
							controller.checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
							
						}
					} catch (Exception e1) {
						view.getLBLOutput().setText("Non ci sono pecore in questa regione");
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
		case 0xff18d111:
			System.out.println("Regione 2");
			break;
		case 0xff007b0e:
			System.out.println("Regione 1");
			break;
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
