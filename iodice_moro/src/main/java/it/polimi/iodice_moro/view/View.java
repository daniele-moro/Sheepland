package it.polimi.iodice_moro.view;


import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.network.ControllerRMI;
import it.polimi.iodice_moro.network.ControllerSocket;
import it.polimi.iodice_moro.network.ViewRMI;
import it.polimi.iodice_moro.network.ViewSocket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class View extends UnicastRemoteObject implements IFView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8928439736905632720L;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.view");

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
					case ACCOPPIAMENTO1:
						lblOutput.setText("ACCOPPIAMENTO 1");
						mossaAttuale=TipoMossa.ACCOPPIAMENTO1;
						reg=controller.getIDRegioniAd();
						if(reg.size()>0 && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
					case SPARATORIA1:
						lblOutput.setText("SPARATORIA 1");
						mossaAttuale=TipoMossa.SPARATORIA1;
						reg=controller.getIDRegioniAd();
						if(reg.size()>0 && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}break;
					case SPARATORIA2:
						lblOutput.setText("SPARATORIA 2");
						mouse.setRegioni("","");
						mossaAttuale=TipoMossa.SPARATORIA2;
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
				
			} catch (RemoteException e1) {
				logger.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}
	
	private static JFrame frame;
	
	private JPanel rightPanel;
	private JPanel leftPanel;
	
	private JLabel mappa;
	private Map<TipoTerreno,JLabel> lblTessere = new HashMap<TipoTerreno,JLabel>();
	private Color giocatoreCorrente;
	
	private JButton btnSpostaPecora;
	private JButton btnSpostaPastore;
	private JButton btnCompraTessera;
	private JButton	btnAccoppiamento1;
	private JButton btnSparatoria1;
	private JButton btnSparatoria2;
	
	private Map<String,Point> posizioniRegioni = new HashMap<String,Point>();
	private Map<Color,JLabel> giocatori = new HashMap<Color,JLabel>();
	private Map<Color,JLabel> pedineGiocatori = new HashMap<Color, JLabel>();
	
	private Map<String,JLabel> lblRegioni = new HashMap<String,JLabel>();
	private Map<String,Point> posizioniCancelli = new HashMap<String, Point>();
	//Queste sono le seconde pedine, per quando si gioca con due giocatori
	private Map<Color,JLabel> pedine2Giocatori = new HashMap<Color,JLabel>();
	
	private Map<Color,String>gioc;
	private JLabel pecoraNera;
	private JLabel lupo;
	private JLabel lblOutput = new JLabel();
	private JLabel lblDado;
	
	private TipoMossa mossaAttuale;
	private IFController controller;
	
	private AzioniMouse mouse;

	private Color coloreGamer;
	
	public View(IFController controller) throws RemoteException {
		this.controller=controller;
		mossaAttuale=TipoMossa.SELEZ_POSIZ;
		initGUI();
	}
	
	//Inizializzazione della GUI
	public void initGUI(){
		//Inizializzazione del Frame principale
		frame= new JFrame("SHEEPLAND");
		frame.setBackground(new Color(43,163,250));
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(new ChiusuraSchermata(controller));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//frame.setAlwaysOnTop(true);
		
		//CENTRALPANEL
		mappa = new JLabel();
		mappa.setIcon(new ImageIcon(this.getClass().getResource("/immagini/game_board.png")));
		mouse = new AzioniMouse(this.getClass().getResourceAsStream("/immagini/game_board_back.png"), this, controller);
		mouse.setRegioni("", "");
		mappa.addMouseListener(mouse);
		mappa.addMouseMotionListener(mouse);
		mappa.setLayout(null);
		
		frame.add(mappa, BorderLayout.CENTER);
		
		//RIGHTPANEL
		rightPanel = new JPanel();
		rightPanel.setBackground(new Color(43,163,250));
		rightPanel.setLayout(new GridLayout(6,1));
		AzioniBottoni action = new AzioniBottoni();
		
		btnSpostaPecora = new JButton("<html>SPOSTA <br>PECORA</html>");
		btnSpostaPecora.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pecora.png")));
		btnSpostaPecora.setActionCommand(TipoMossa.SPOSTA_PECORA.toString());
		btnSpostaPecora.addActionListener(action);
		
		btnSpostaPastore = new JButton("<html>SPOSTA <br>PASTORE</html>");
		btnSpostaPastore.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pastore.png")));
		btnSpostaPastore.setActionCommand(TipoMossa.SPOSTA_PASTORE.toString());
		btnSpostaPastore.addActionListener(action);
		
		btnCompraTessera = new JButton("<html>COMPRA <br>TESSERA</html>");
		btnCompraTessera.setIcon(new ImageIcon(this.getClass().getResource("/immagini/compratessere.png")));
		btnCompraTessera.setActionCommand(TipoMossa.COMPRA_TESSERA.toString());
		btnCompraTessera.addActionListener(action);
		
		btnAccoppiamento1 = new JButton("<html>ACCOPPIAMENTO <br>1</html>");
		btnAccoppiamento1.setActionCommand(TipoMossa.ACCOPPIAMENTO1.toString());
		btnAccoppiamento1.addActionListener(action);
		
		btnSparatoria1 = new JButton("<html>SPARATORIA <br>1</html>");
		btnSparatoria1.setActionCommand(TipoMossa.SPARATORIA1.toString());
		btnSparatoria1.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pistola.png")));
		btnSparatoria1.addActionListener(action);
		
		btnSparatoria2 = new JButton("<html>SPARATORIA <br>2</html>");
		btnSparatoria2.setActionCommand(TipoMossa.SPARATORIA2.toString());
		btnSparatoria2.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pistola.png")));
		btnSparatoria2.addActionListener(action);
		
		//Disattivo i bottoni che per ora non servono
		btnSpostaPastore.setEnabled(false);
		btnSpostaPecora.setEnabled(false);
		btnCompraTessera.setEnabled(false);
		btnAccoppiamento1.setEnabled(false);
		btnSparatoria1.setEnabled(false);
		btnSparatoria2.setEnabled(false);
		
		rightPanel.add(btnSpostaPecora);
		rightPanel.add(btnSpostaPastore);
		rightPanel.add(btnCompraTessera);
		rightPanel.add(btnAccoppiamento1);
		rightPanel.add(btnSparatoria1);
		rightPanel.add(btnSparatoria2);
		frame.add(rightPanel, BorderLayout.EAST);
		
		//LEFTPANEL
		leftPanel = new JPanel();
		leftPanel.setBackground(new Color(43,163,250));
		leftPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel lbltemp = new JLabel();
		JLabel lbltext = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/bosco.png")));
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
		c.gridy=1;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/coltivazione.png")));
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
		c.gridy=1;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/montagne.png")));
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
		c.gridy=2;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/paludi.png")));
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
		c.gridy=2;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pianura.png")));
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
		c.gridy=3;
		leftPanel.add(lbltemp,c);
		
		lbltemp = new JLabel();
		lbltemp.setIcon(new ImageIcon(this.getClass().getResource("/immagini/sabbia.png")));
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
		c.gridy=3;
		leftPanel.add(lbltemp,c);
		
		//LABEL per il dado
		lblDado = new JLabel();
		lblDado.setText("  ");
		c.gridx=0;
		c.gridy=8;
		c.fill=GridBagConstraints.BOTH;
		c.anchor=GridBagConstraints.LINE_START;
		leftPanel.add(lblDado,c);
		
		frame.add(leftPanel, BorderLayout.WEST);

		//LABEL PER GLI ERRORI
		lblOutput = new JLabel();
		lblOutput.setText("  ");
		lblOutput.setBorder(new EmptyBorder(30,10,0,0));
		c.anchor=GridBagConstraints.WEST;
		frame.add(lblOutput,BorderLayout.SOUTH);
		
		frame.setVisible(true);
		frame.setResizable(false);
		frame.pack();
		
	}
	
	//INIZIALIZZA TUTTI GLI OGGETTI CHE SONO POSIZIONATI SOPRA LA MAPPA E I GIOCATORI
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#initMappa()
	 */
	@Override
	public void initMappa() throws RemoteException{
		
		//Visualizzo tutte le pecore
		//File da passare al BackgroundedLabel per l'immagine di sfondo
		//InputStream pecBianca = this.getClass().getResourceAsStream("/immagini/pecora_bianca.png");
		//Usata solo per sapere le dimensioni dell'immagine della pecora bianca
		ImageIcon iconBianca = new ImageIcon(this.getClass().getResource("/immagini/pecora_bianca.png"));
		ImageIcon pecNera = new ImageIcon(this.getClass().getResource("/immagini/pecora_nera.png"));
		ImageIcon iconLupo = new ImageIcon(this.getClass().getResource("/immagini/lupo.png"));
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
				BackgroundedLabel lblPecora = new BackgroundedLabel(this.getClass().getResourceAsStream("/immagini/pecora_bianca.png"));
				lblPecora.setBounds(p.x, p.y, iconBianca.getIconWidth(), iconBianca.getIconHeight());
				lblRegioni.put(s,lblPecora);
				//posiziono anche il lupo.
				JLabel lblLupo = new JLabel();
				lblLupo.setIcon(iconLupo);
				//TODO
				lblLupo.setBounds(p.x-25, p.y+20, iconLupo.getIconWidth(), iconLupo.getIconHeight());
				mappa.add(lblLupo);
				lupo=lblLupo;
			}else{
				//Visualizzo le pecore bianche
				JLabel lblPecora = new BackgroundedLabel(this.getClass().getResourceAsStream("/immagini/pecora_bianca.png"));
				lblPecora.setText("      1");
				mappa.add(lblPecora);
				lblPecora.setBounds(p.x, p.y, iconBianca.getIconWidth(), iconBianca.getIconHeight());
				lblRegioni.put(s,lblPecora);
			}
		}
		
		//Prelevo i giocatori dal controller e li visualizzo
		//li prelevo prima in inizia partita Map<Color,String>gioc=controller.getGiocatori();
		System.out.println("Ricezione dei giocatori");
		
		GridBagConstraints c = new GridBagConstraints();
		JLabel lbltemp2;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		int py=4;
		for(Color colore:gioc.keySet()){
			lbltemp2 = new JLabel();
			lbltemp2.setText(gioc.get(colore));//+" SOLDI: 20");
			lbltemp2.setName(gioc.get(colore));
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
	}

	
	
	public void setColore(Color colore) {
		this.coloreGamer=colore;
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		JLabel lblGamer = new JLabel();
		lblGamer.setText("Questo è il tuo colore!");
		lblGamer.setBackground(colore);
		lblGamer.setOpaque(true);
		lblGamer.setBorder(new MatteBorder(20,20,20,20, colore));
		leftPanel.add(lblGamer,c);
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#cambiaGiocatore(java.awt.Color)
	 */
	@Override
	public void cambiaGiocatore(Color color){
		setGiocatoreCorrente(color);
		//Evidenzio il giocatore del turno corrente
		for(Color col: giocatori.keySet()){
			if(col.equals(color)){
				giocatori.get(col).setEnabled(true);
				giocatori.get(col).setFont(new Font("Arial", Font.BOLD, 20));
				giocatori.get(col).setText(giocatori.get(col).getText());
			}else{
				giocatori.get(col).setEnabled(false);
				giocatori.get(col).setFont(new Font("Arial", Font.PLAIN, 12));
				giocatori.get(col).setText(giocatori.get(col).getText());
			}
		}
		if(color.equals(coloreGamer)){
			attivaGiocatore();
		}else{
			disattivaGiocatore();
		}
	}
	
	public void attivaGiocatore(){
		btnCompraTessera.setEnabled(true);
		btnSpostaPastore.setEnabled(true);
		btnSpostaPecora.setEnabled(true);
		btnAccoppiamento1.setEnabled(true);
		btnSparatoria1.setEnabled(true);
		btnSparatoria2.setEnabled(true);
	}

	
	public void disattivaGiocatore(){
		btnCompraTessera.setEnabled(false);
		btnSpostaPastore.setEnabled(false);
		btnSpostaPecora.setEnabled(false);
		btnAccoppiamento1.setEnabled(false);
		btnSparatoria1.setEnabled(false);
		btnSparatoria2.setEnabled(false);
		mossaAttuale=TipoMossa.NO_MOSSA;
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#addCancelloNormale(java.lang.String)
	 */
	@Override
	public void addCancelloNormale(String stradaID){
		mettiCancello(stradaID, new ImageIcon(this.getClass().getResource("/immagini/cancello.png")));
	}
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#addCancelloFinale(java.lang.String)
	 */
	@Override
	public void addCancelloFinale(String stradaID){
		mettiCancello(stradaID, new ImageIcon(this.getClass().getResource("/immagini/cancello_finale.png")));
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
	void spostaImmagine(Point sorg, Point dest, ImageIcon image){
		JLabel lblMove = new JLabel();
		lblMove.setIcon(image);
		
		//Posizioni x e y
		double posx=sorg.x;
		double posy=sorg.y;
		lblMove.setLocation((int)posx,(int)posy);
		lblMove.setVisible(true);
		//incrementi di ogni passo nei due assi
		double incx=(dest.x-sorg.x)/100.0;
		double incy=(dest.y-sorg.y)/100.0;
		try{
			mappa.add(lblMove);
			for(int i=0; i<100;i++){
				lblMove.setBounds((int)posx, (int)posy,image.getIconWidth(),image.getIconHeight());
				lblMove.setVisible(true);
				Thread.sleep(20);
				posx+=incx;
				posy+=incy;
			}
			mappa.remove(lblMove);
			mappa.repaint();
		}catch(InterruptedException e){
			logger.log(Level.SEVERE, "Errore nell'esecuzione della thread sleep", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraBianca(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraBianca(String s, String d){
		Point sorg= posizioniRegioni.get(s);
		Point dest= posizioniRegioni.get(d);
		
		ThreadAnimazionePecoraBianca r = new ThreadAnimazionePecoraBianca(this, sorg, dest);
		Thread t = new Thread(r);
		t.start();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPastore(java.lang.String, java.lang.String, java.awt.Color)
	 */
	@Override
	public void spostaPastore(String s, String d, Color colore){
		Point sorg=null;
		Point dest =posizioniCancelli.get(d);
		if(!s.equals("")){
			sorg=posizioniCancelli.get(s);
		}
		JLabel pedGiocatore = pedineGiocatori.get(colore);
		if(pedGiocatore!=null){
			if(!pedGiocatore.getLocation().equals(sorg)){
				pedGiocatore=pedine2Giocatori.get(colore);
			}
		}
		ThreadAnimazionePastore r = new ThreadAnimazionePastore(this,
				mappa,
				pedGiocatore,
				sorg,
				dest,
				colore);
		Thread t = new Thread(r);
		t.start();
	}
	
	@Override
	public void posiziona2Pastore(String idStrada, Color colore) {
		JLabel pedina2= new JLabel();
		pedine2Giocatori.put(colore, pedina2);
		pedina2.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pedinagialla.png")));
		mappa.add(pedina2);
		Point dest =posizioniCancelli.get(idStrada);
		ThreadAnimazionePastore p = new ThreadAnimazionePastore(this, mappa, pedina2, null, dest, colore);
		Thread t = new Thread(p);
		t.start();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraNera(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraNera(String s, String d){
		Point sorg= posizioniRegioni.get(s);
		Point dest= posizioniRegioni.get(d);
		
		//Avvio il Thread per l'animazione sulla schermata
		ThreadAnimazionePecoraNera r = new ThreadAnimazionePecoraNera(this, mappa, pecoraNera, sorg, dest);
		Thread t = new Thread(r);
		t.start();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraNeraLupo(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaLupo(String s, String d){
		if(giocatoreCorrente.equals(coloreGamer)){
			disattivaGiocatore();
		}
		Point sorg= posizioniRegioni.get(s);
		Point dest= posizioniRegioni.get(d);
		
		//Avvio il Thread per l'animazione sulla schermata
		ThreadAnimazioneLupo r = new ThreadAnimazioneLupo(this, mappa, lupo, sorg, dest);
		Thread t = new Thread(r);
		t.start();
		if(giocatoreCorrente.equals(coloreGamer)){
			attivaGiocatore();
		}
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
	public void modQtaTessera(TipoTerreno tess, int num, Color colore){
		lblTessere.get(tess).setText(Integer.toString(num));
		frame.repaint();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#modSoldiGiocatore(java.awt.Color, int)
	 */
	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		
		String nome = giocatori.get(coloreGiocatoreDaModificare).getName();
		giocatori.get(coloreGiocatoreDaModificare).setText(nome+" SOLDI: "+soldi);
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
			lblOutput.setText("ERRORE NELL'INCREMENTO DEL PREZZO TESSERA!!");
			return;
		}
		JLabel lblDanaro = new JLabel();
		ImageIcon imgDanaro = new ImageIcon(this.getClass().getResource("/immagini/danaro.png"));
		lblDanaro.setIcon(imgDanaro);
		lblTessera.add(lblDanaro);
		lblDanaro.setBounds(posx, 0, imgDanaro.getIconWidth(), imgDanaro.getIconHeight());
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#visualizzaPunteggi(java.util.Map)
	 */
	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {	
		disattivaGiocatore();
		JTable tabellaPunteggi = new JTable(punteggiOrdinati.size(),2);
		int row = 0;
		for(Map.Entry<Giocatore,Integer> entry: punteggiOrdinati.entrySet()){
		      tabellaPunteggi.setValueAt(entry.getKey().getNome(),row,0);
		      tabellaPunteggi.setValueAt(entry.getValue(),row,1);
		      row++;
		 }
		tabellaPunteggi.setEnabled(false);
		JOptionPane.showMessageDialog(null, tabellaPunteggi, "Lista Punteggi", JOptionPane.INFORMATION_MESSAGE);
		
		if(controller instanceof ControllerSocket){
			System.out.println("CHIUSURA CONNESSIONE");
			((ControllerSocket)controller).end();
		}
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
		if(giocatoreCorrente.equals(coloreGamer)){
			//è il turno di questo giocatore
			String vis="E' il tuo turno!";
			if(mossaAttuale.equals(TipoMossa.SELEZ_POSIZ)){
				vis+="\nSeleziona la posizione del tuo pastore";
			}
			//JOptionPane.showMessageDialog(frame, vis,"TURNO" , JOptionPane.INFORMATION_MESSAGE);
			lblOutput.setText(vis);
		} else{
			lblOutput.setText("Non è il tuo turno!!");
		}
	}

	public Color getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	public JLabel getLBLPecoraNera() {
		return pecoraNera;
	}
	
	public JLabel getLBLLupo() {
		return lupo;
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void attendiGiocatori() {
		// TODO Auto-generated method stub
	}

	public Color getColoreGamer() {
		return coloreGamer;
	}

	@Override
	public void visRisDado(int numero) {
		/*if(giocatoreCorrente.equals(coloreGamer)){
			disattivaGiocatore();
		}
		lblDado.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("immagini/dado.gif")));
		frame.repaint();
		try {
			//metto in pausa il thread per dare la sensazione che si stia lanciando il dado
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			logger.log(Level.SEVERE, "Errore nell'esecuzione della thread sleep", e);
		}
		lblDado.setIcon(null);
		lblDado.setText("Risultato: "+numero);
		frame.repaint();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
			logger.log(Level.SEVERE, "Errore nell'esecuzione della thread sleep", e);
		}
		lblDado.setText("    ");
		frame.repaint();
		if(giocatoreCorrente.equals(coloreGamer)){
			attivaGiocatore();
		}*/
		//JOptionPane.showMessageDialog(frame, "Risultato dado: "+numero);
		lblOutput.setText("Risultato dado: "+numero);
	}
	
	public void addPedinaGiocatore(Color colore, JLabel pedGiocatore) {
		pedineGiocatori.put(colore, pedGiocatore);		
	}

	//METODI per settare le posizioni delle regioni, dei cancelli e dei giocatori
	@Override
	public void setPosizioniRegioni(Map<String, Point> posizioniRegioni) {
		this.posizioniRegioni=posizioniRegioni;
	}

	@Override
	public void setPosizioniStrade(Map<String, Point> posizioniCancelli) {
		this.posizioniCancelli=posizioniCancelli;
	}

	@Override
	public void setGiocatori(Map<Color, String> giocatori) {
		this.gioc=giocatori;
		
	}

	@Override
	public void close() throws RemoteException {
		controller.end();
		JOptionPane.showMessageDialog(frame,"Un utente si è disconnesso, la partita termina qui. \n Chiusura dell'applicazione");
		System.exit(0);
	}
	
	@Override
	public void selezPast(Color colore){
		if(coloreGamer.equals(colore)){
			disattivaGiocatore();
			lblOutput.setText("Seleziona il pastore che vuoi utilizzare!!");
			mossaAttuale=TipoMossa.G2_SELEZ_PAST;
		}
	}


}