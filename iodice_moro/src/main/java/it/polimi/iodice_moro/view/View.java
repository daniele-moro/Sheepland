package it.polimi.iodice_moro.view;


import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class View implements IFView {
	
	public class AzioniBottoni implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				TipoMossa mossa = TipoMossa.parseInput(e.getActionCommand());
				
				if(controller.mossaPossibile(mossa)){
					List<String> reg;
					switch(mossa){
					case COMPRA_TESSERA:
						lblOutput.setText("COMPRA TESSERA");
						mossaAttuale=TipoMossa.COMPRA_TESSERA;
						//Setto le due regini tra cui posso spostare la pecora.
						reg=controller.getIDRegioniAd();
						if(reg.size()>0 && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
					case SPOSTA_PASTORE:
						lblOutput.setText("SPOSTA PASTORE");
						mouse.setRegioni("", "");
						mossaAttuale=TipoMossa.SPOSTA_PASTORE;
						break;
					case SPOSTA_PECORA:
						lblOutput.setText("SPOSTA PECORA");
						mossaAttuale=TipoMossa.SPOSTA_PECORA;
						//Setto le due regioni tra cui posso spostare la pecora, l'utente quando ci passerà sopra vedrà comparire la manina
						reg=controller.getIDRegioniAd();
						if(reg.size()>0 && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
					default:
						lblOutput.setText("Non puoi effettuare questa mossa!!!");
						mouse.setRegioni("", "");
						mossaAttuale=TipoMossa.NO_MOSSA;
						break;

					}
				} else{
					lblOutput.setText("Non puoi effettuare questa mossa!!!");
					mouse.setRegioni("", "");
					mossaAttuale=TipoMossa.NO_MOSSA;
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}
	
	public static JFrame frame;
	
	private JPanel rightPanel;
	private JPanel leftPanel;
	
	private JLabel mappa;
	private Map<TipoTerreno,JLabel> lblTessere = new HashMap<TipoTerreno,JLabel>();
	private Color giocatoreCorrente;
	
	private JButton btnSpostaPecora;
	private JButton btnSpostaPastore;
	private JButton btnCompraTessera;
	
	private Map<String,Point> posizioniRegioni = new HashMap<String,Point>();
	private Map<Color,JLabel> giocatori = new HashMap<Color,JLabel>();
	private Map<Color,JLabel> pedineGiocatori = new HashMap<Color, JLabel>();
	private Map<String,JLabel> lblRegioni = new HashMap<String,JLabel>();
	private Map<String,Point> posizioniCancelli = new HashMap<String, Point>();
	private JLabel pecoraNera;
	private JLabel lblOutput = new JLabel();
	
	private TipoMossa mossaAttuale;
	private IFController controller;
	
	private AzioniMouse mouse;
	
	public View(Controller controller){
		this.controller=controller;
		mossaAttuale=TipoMossa.SELEZ_POSIZ;
		initGUI();
	}
	
	//Inizializzazione della GUI
	public void initGUI(){
		//Prelevo le posizioni delle regioni ed il loro id memorizzati nel MODEL
		posizioniRegioni=controller.getPosRegioni();
		
		//Prelevo le posizioni dei cancelli ed i loro iD memorizzati nel MODEL
		posizioniCancelli = controller.getPosStrade();
		
		//Inizializzazione del Frame principale
		frame= new JFrame("SHEEPLAND");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		
		//CENTRALPANEL
		mappa = new JLabel();
		mappa.setIcon(new ImageIcon("immagini/game_board.png"));
		
		mouse = new AzioniMouse(new File("immagini/game_board_back.png"), this, controller);
		mouse.setRegioni("ff18d111", "ff007b0e");
		mappa.addMouseListener(mouse);
		mappa.addMouseMotionListener(mouse);
		mappa.setLayout(null);
		
		frame.add(mappa, BorderLayout.CENTER);
		
		//RIGHTPANEL
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(4,1));
		AzioniBottoni action = new AzioniBottoni();
		
		btnSpostaPecora = new JButton("SPOSTA PECORA");
		btnSpostaPecora.setActionCommand(TipoMossa.SPOSTA_PECORA.toString());
		btnSpostaPecora.addActionListener(action);
		
		btnSpostaPastore = new JButton("SPOSTA PASTORE");
		btnSpostaPastore.setActionCommand(TipoMossa.SPOSTA_PASTORE.toString());
		btnSpostaPastore.addActionListener(action);
		
		btnCompraTessera = new JButton("COMPRA TESSERA");
		btnCompraTessera.setActionCommand(TipoMossa.COMPRA_TESSERA.toString());
		btnCompraTessera.addActionListener(action);
		
		//Disattivo i bottoni che per ora non servono
		btnSpostaPastore.setEnabled(false);
		btnSpostaPecora.setEnabled(false);
		btnCompraTessera.setEnabled(false);
		
		
		rightPanel.add(btnSpostaPecora);
		rightPanel.add(btnSpostaPastore);
		rightPanel.add(btnCompraTessera);
		frame.add(rightPanel, BorderLayout.EAST);
		
		//LEFTPANEL
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel lbltemp = new JLabel();
		JLabel lbltext = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/bosco.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBackground(Color.RED);
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.BOSCO, lbltext);
		c.gridx=0;
		c.gridy=0;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/coltivazione.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setBackground(Color.RED);
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.COLTIVAZIONI, lbltext);
		c.gridx=1;
		c.gridy=0;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/montagne.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setBackground(Color.RED);
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.MONTAGNA, lbltext);
		c.gridx=0;
		c.gridy=1;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/paludi.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setBackground(Color.RED);
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.PALUDI, lbltext);
		c.gridx=1;
		c.gridy=1;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/pianura.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setBackground(Color.RED);
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.PIANURA, lbltext);
		c.gridx=0;
		c.gridy=2;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon("immagini/sabbia.png"));
		lbltemp.setBorder(new EmptyBorder(5,5,5,5));
		lbltext = new JLabel();
		lbltext.setText("0");
		lbltext.setBorder(new MatteBorder(1,5,1,5, Color.RED));
		lbltext.setBackground(Color.RED);
		lbltext.setOpaque(true);
		lbltemp.add(lbltext);
		lbltext.setBounds(90, 90, 20, 20);
		lblTessere.put(TipoTerreno.SABBIA, lbltext);
		c.gridx=1;
		c.gridy=2;
		leftPanel.add(lbltemp,c);

		//LABEL PER GLI ERRORI
		lblOutput = new JLabel();
		lblOutput.setText("sdasd ");
		lblOutput.setBorder(new EmptyBorder(30,10,0,0));
		c.gridx=0;
		c.gridy=7;
		c.anchor=GridBagConstraints.WEST;
		frame.add(lblOutput,BorderLayout.SOUTH);
		
		frame.add(leftPanel, BorderLayout.WEST);
		
		frame.setVisible(true);
		frame.setResizable(false);
		frame.pack();
	}
	
	//INIZIALIZZA TUTTI GLI OGGETTI CHE SONO POSIZIONATI SOPRA LA MAPPA E I GIOCATORI
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#initMappa()
	 */
	@Override
	public void initMappa(){
		//Visualizzo tutte le pecore
		//File da passare al BackgroundedLabel per l'immagine di sfondo
		File pecBianca = new File("immagini/pecora_bianca.png");
		//Usata solo per sapere le dimensioni dell'immagine della pecora bianca
		ImageIcon iconBianca = new ImageIcon("immagini/pecora_bianca.png");
		ImageIcon pecNera = new ImageIcon("immagini/pecora_nera.png");
		for(String s: posizioniRegioni.keySet()){
			Point p = posizioniRegioni.get(s);
			if(s.equals("ff002e73")){
				//devo posizionare la pecora nera perchè sono in sheepsburg
				JLabel lblNera = new JLabel();
				lblNera.setIcon(pecNera);
				lblNera.setBounds(p.x+10, p.y-20, pecNera.getIconWidth(), pecNera.getIconHeight());
				mappa.add(lblNera);
				pecoraNera=lblNera;
				//comunque devo inizializzare la label per la pecora normale, però senza numero di pecore
				BackgroundedLabel lblPecora = new BackgroundedLabel(pecBianca);
				lblPecora.setBounds(p.x, p.y, iconBianca.getIconWidth(), iconBianca.getIconHeight());
				lblRegioni.put(s,lblPecora);
			}else{
				//Visualizzo le pecore bianche
				BackgroundedLabel lblPecora = new BackgroundedLabel(pecBianca);
				lblPecora.setText("      1");
				mappa.add(lblPecora);
				lblPecora.setBounds(p.x, p.y, iconBianca.getIconWidth(), iconBianca.getIconHeight());
				lblRegioni.put(s,lblPecora);
			}
		}
		
		//Prelevo i giocatori dal controller e li visualizzo
		Map<Color,String>gioc=controller.getGiocatori();
		GridBagConstraints c = new GridBagConstraints();
		JLabel lbltemp2;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		int py=3;
		for(Color colore:gioc.keySet()){
			lbltemp2 = new JLabel();
			lbltemp2.setText(gioc.get(colore)+" SOLDI: 20");
			lbltemp2.setBackground(colore);
			lbltemp2.setFont(new Font("Arial", Font.PLAIN, 12));
			lbltemp2.setOpaque(true);
			lbltemp2.setBorder(new MatteBorder(10,10,10,10, colore));
			c.gridx=0;
			c.gridy=py;
			py++;
			leftPanel.add(lbltemp2,c);
			giocatori.put(colore,lbltemp2);
			
		}
		
		mappa.repaint();
		frame.pack();
		mossaAttuale=TipoMossa.NO_MOSSA;
		attivaGiocatore();

	}

	public static void main(String[] args) {
		//CREO tutte le istanze che mi servono per far funzionare il gioco
		StatoPartita statopartita= new StatoPartita();
		
		Controller controller = new Controller(statopartita);
		controller.creaGiocatore("prova");
		controller.creaGiocatore("prova 2");
		controller.creaGiocatore("prova 3");
		controller.creaGiocatore("prova 4");
		
		View view = new View(controller);
		controller.setView(view);
		
		controller.iniziaPartita();
		
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#cambiaGiocatore(java.awt.Color)
	 */
	@Override
	public void cambiaGiocatore(Color color){
		//Cambio l'enable delle tessere
		for(Color col: giocatori.keySet()){
			if(col.equals(color)){
				giocatori.get(col).setEnabled(true);
				giocatori.get(col).setFont(new Font("Arial", Font.BOLD, 20));
				giocatori.get(col).setText(giocatori.get(col).getText());
				giocatoreCorrente=color;
			}else{
				giocatori.get(col).setEnabled(false);
				giocatori.get(col).setFont(new Font("Arial", Font.PLAIN, 12));
				giocatori.get(col).setText(giocatori.get(col).getText());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#attivaGiocatore()
	 */
	@Override
	public void attivaGiocatore(){
		btnCompraTessera.setEnabled(true);
		btnSpostaPastore.setEnabled(true);
		btnSpostaPecora.setEnabled(true);
	}
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#disattivaGiocatore()
	 */
	@Override
	public void disattivaGiocatore(){
		btnCompraTessera.setEnabled(false);
		btnSpostaPastore.setEnabled(false);
		btnSpostaPecora.setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#addCancelloNormale(java.lang.String)
	 */
	@Override
	public void addCancelloNormale(String stradaID){
		mettiCancello(stradaID, new ImageIcon("immagini/cancello.png"));
	}
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#addCancelloFinale(java.lang.String)
	 */
	@Override
	public void addCancelloFinale(String stradaID){
		mettiCancello(stradaID, new ImageIcon("immagini/cancello_finale.png"));
	}
	
	/**
	 * Metodo per aggiungere un cancello alla strada passata come ID
	 * @param stradaID ID della strada dove aggiungere il cancello
	 * @param imgCancello Immagine da visualizzare
	 */
	private void mettiCancello(String stradaID, ImageIcon imgCancello){
		JLabel lblCancello = new JLabel();
		lblCancello.setIcon(imgCancello);
		Point pos = posizioniCancelli.get(stradaID);
		lblCancello.setBounds(pos.x, pos.y, imgCancello.getIconWidth(), imgCancello.getIconHeight());
		mappa.add(lblCancello);
		mappa.repaint();
	}
	
	/**
	 * Animazione di spostamento di un immagine, nel nostro caso sarà una pecora
	 * @param sorg Point sorgente
	 * @param dest Point Destinazione
	 * @param image Sfondo della label da spostare
	 */
	private void spostaPecora(Point sorg, Point dest, ImageIcon image){
		JLabel lblMove = new JLabel();
		lblMove.setIcon(image);
		
		//Posizioni x e y
		double posx=sorg.x;
		double posy=sorg.y;
		lblMove.setLocation((int)posx,(int)posy);
		
		//incrementi di ogni passo nei due assi
		double incx=(dest.x-sorg.x)/100.0;
		double incy=(dest.y-sorg.y)/100.0;
		try{
			mappa.add(lblMove);
			for(int i=0; i<100;i++){
				lblMove.setBounds((int)posx, (int)posy,image.getIconWidth(),image.getIconHeight());
				Thread.sleep(15);
				posx+=incx;
				posy+=incy;
			}
			mappa.remove(lblMove);
			mappa.repaint();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraBianca(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraBianca(String s, String d){
		Point sorg= posizioniRegioni.get(s);
		Point dest= posizioniRegioni.get(d);
		
		spostaPecora(sorg, dest, new ImageIcon("immagini/pecora_bianca.png"));
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPastore(java.lang.String, java.lang.String, java.awt.Color)
	 */
	@Override
	public void spostaPastore(String s, String d, Color colore){
		ImageIcon img = null;
		if(colore.equals(new Color(255,0,0))){
			img=new ImageIcon("immagini/pedinarossa.png");
		}
		if(colore.equals(new Color(0,255,0))){
			img=new ImageIcon("immagini/pedinaverde.png");
		}
		if(colore.equals(new Color(0,0,255))){
			img=new ImageIcon("immagini/pedinaazzurra.png");
		}
		if(colore.equals(new Color(255,255,0))){
			img=new ImageIcon("immagini/pedinagialla.png");
		}
		
		if(!s.equals("")){
			spostaPecora(posizioniCancelli.get(s), posizioniCancelli.get(d), img);
		}
		JLabel pedGiocatore = pedineGiocatori.get(colore);
		if(pedGiocatore == null){
			pedGiocatore = new JLabel();
			pedGiocatore.setIcon(img);
			mappa.add(pedGiocatore);
			pedineGiocatori.put(colore, pedGiocatore);
		}
		pedGiocatore.setBounds(posizioniCancelli.get(d).x, posizioniCancelli.get(d).y, img.getIconWidth(), img.getIconHeight());
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraNera(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraNera(String s, String d){
		mappa.remove(pecoraNera);
		Point sorg= posizioniRegioni.get(s);
		Point sorg2=(Point) sorg.clone();
		Point dest= posizioniRegioni.get(d);
		Point dest2=(Point) dest.clone();
		sorg2.y-=21;
		dest2.y-=21;
		spostaPecora(sorg2,dest2, new ImageIcon("immagini/pecora_nera.png"));
		mappa.add(pecoraNera);
		pecoraNera.setBounds(dest2.x, dest2.y, pecoraNera.getWidth(), pecoraNera.getHeight());
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#modificaQtaPecora(java.lang.String, int)
	 */
	@Override
	public void modificaQtaPecora(String idReg, int num){
		JLabel lblPecore =lblRegioni.get(idReg);
		if(num>0){
			mappa.add(lblPecore);
			lblPecore.setText("      "+num);
		}
		else{
			mappa.remove(lblPecore);
		}
		mappa.repaint();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#modQtaTessera(it.polimi.iodice_moro.model.TipoTerreno, int)
	 */
	@Override
	public void modQtaTessera(TipoTerreno tess, int num){
		lblTessere.get(tess).setText(Integer.toString(num));
		frame.repaint();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#modSoldiGiocatore(java.awt.Color, int)
	 */
	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		Map<Color,String> mappaColoriGiocatori=controller.getGiocatori();
		giocatori.get(coloreGiocatoreDaModificare).setText(mappaColoriGiocatori.get(coloreGiocatoreDaModificare)+" SOLDI: "+soldi);
		giocatori.get(coloreGiocatoreDaModificare).repaint();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#incPrezzoTessera(it.polimi.iodice_moro.model.TipoTerreno)
	 */
	@Override
	public void incPrezzoTessera(TipoTerreno tess){
		JLabel lblTessera = (JLabel)lblTessere.get(tess).getParent();
		int posx = 20* (lblTessera.getComponentCount()-1);
		if(posx<0){
			lblOutput.setText("ERRORE NELL'INCREMENTO DEL PREZZZO TESSERA!!");
			return;
		}
		JLabel lblDanaro = new JLabel();
		ImageIcon imgDanaro = new ImageIcon("immagini/danaro.png");
		lblDanaro.setIcon(imgDanaro);
		lblTessera.add(lblDanaro);
		lblDanaro.setBounds(posx, 0, imgDanaro.getIconWidth(), imgDanaro.getIconHeight());
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#visualizzaPunteggi(java.util.Map)
	 */
	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {		
		JTable tabellaPunteggi = new JTable(punteggiOrdinati.size(),2);
		int row = 0;
		for(Map.Entry<Giocatore,Integer> entry: punteggiOrdinati.entrySet()){
		      tabellaPunteggi.setValueAt(entry.getKey().getNome(),row,0);
		      tabellaPunteggi.setValueAt(entry.getValue(),row,1);
		      row++;
		 }
		tabellaPunteggi.setEnabled(false);
		JOptionPane.showMessageDialog(null, tabellaPunteggi, "Lista Punteggi", JOptionPane.INFORMATION_MESSAGE);
	}

	public Map<String,Point> getPosizioniRegioni() {
		return posizioniRegioni;
	}

	public TipoMossa getMossaAttuale() {
		return mossaAttuale;
	}

	public JLabel getLBLOutput() {
		return lblOutput;
		
	}

	public void setMossaAttuale(TipoMossa mossa) {
		mossaAttuale=mossa;	
	}

	public Map<String, Point> getPosizioniCancelli() {
		return posizioniCancelli;
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#setGiocatoreCorrente(java.awt.Color)
	 */
	@Override
	public void setGiocatoreCorrente(Color colore) {
		giocatoreCorrente=colore;
	}

	public Color getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	public JLabel getLBLPecoraNera() {
		return pecoraNera;
	}

	public JFrame getFrame() {
		return frame;
	}


	

}