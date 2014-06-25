package it.polimi.iodice_moro.view;


import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.network.ControllerSocket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * La classe View si occupa di mostrare all'utente lo stato del gioco. Riceve gli input
 * dall'utente e si occupa quindi di far partire i metodi del controller.
 * @author Antonio Iodice, Daniele Moro
 *
 */
public class View extends UnicastRemoteObject implements IFView {
	
	//Costanti per l'indirizzo delle immagini da visualizzare
	private static final String IMM_PECORA_NERA = "/immagini/pecora_nera.png";
	private static final String IMM_LUPO = "/immagini/lupo.png";
	private static final String IMM_PECORA_BIANCA = "/immagini/pecora_bianca.png";

	//Font per le label per i giocatori
	private static final Font FONT_SELECTED = new Font("Arial", Font.BOLD, 20);
	private static final Font FONT_UNSELECTED = new Font("Arial", Font.PLAIN, 12);
	
	
	//OFFSET per le label del lupo e della pecora nera (offset rispetto alla posizione della pecora bianca)
	private static final int OFFSET_X_LUPO = 28;
	private static final int OFFSET_Y_LUPO = -28;
	private static final int OFFSET_Y_NERA = -18;

	//Costante per la durata delle animazioni
	private static final int TEMPO_ANIMAZIONE_PASTORE = 380; //Da una strada alla successiva
	private static final int TEMPO_ANIMAZIONE_PECORA = 1700; //tempo animazione completa

	//costante per il colore di sfondo
	private static final Color BG_COLOR = new Color(43,163,250);
	
	//costati per i layer del JLayeredPane usato per la mappa
	public static final Integer BACKGROUD_LAYER = Integer.valueOf(0);
	public static final Integer SHEEP_LAYER = Integer.valueOf(100);
	public static final Integer FENCE_LAYER = Integer.valueOf(98);
	public static final Integer SHEPPARD_LAYER = Integer.valueOf(99);
	public static final Integer MOVE_LAYER = Integer.valueOf(101);
	public static final Integer REGION_LAYER = Integer.valueOf(50);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8928439736905632720L;
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.view");
	

	/**
	 * Questa classe è utilizzata per catturare gli eventi collegati ai bottoni per l'esecuzione delle mosse
	 *
	 */
	public class AzioniBottoni implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				//prelevo la mossa collegata al bottono premuto
				TipoMossa mossa = TipoMossa.parseInput(e.getActionCommand());
				//Controllo se la mossa che si vuole fare è possibile
				if(controller.mossaPossibile(mossa)){
					List<String> reg;
					//In base alla mossa che l'utente vuole fare, setto la mossaAttuale e 
					//comunico all'utente cosa vuole fare
					switch(mossa){
					case COMPRA_TESSERA:
						lblOutput.setText("COMPRA TESSERA");
						mossaAttuale=TipoMossa.COMPRA_TESSERA;
						//Setto le due regini tra cui posso spostare la pecora.
						reg=controller.getIDRegioniAd();
						if(!reg.isEmpty() && reg.size()<=2){
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
						//Setto le due regioni tra cui posso spostare la pecora, 
						//l'utente quando ci passerà sopra vedrà comparire la manina e vedra flashare la regione
						//Stessa cosa per le mosse ACCOPPIAMENTO1, SPARATORIA1, SPARATORIA2
						reg=controller.getIDRegioniAd();
						if(!reg.isEmpty() && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
						
					case ACCOPPIAMENTO1:
						lblOutput.setText("ACCOPPIAMENTO 1");
						mossaAttuale=TipoMossa.ACCOPPIAMENTO1;
						reg=controller.getIDRegioniAd();
						if(!reg.isEmpty() && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
						
					case SPARATORIA1:
						lblOutput.setText("SPARATORIA 1");
						mossaAttuale=TipoMossa.SPARATORIA1;
						reg=controller.getIDRegioniAd();
						if(!reg.isEmpty() && reg.size()<=2){
							mouse.setRegioni(reg.get(0), reg.get(1));
						}
						break;
						
					case SPARATORIA2:
						lblOutput.setText("SPARATORIA 2");
						mouse.setRegioni("","");
						mossaAttuale=TipoMossa.SPARATORIA2;
						reg=controller.getIDRegioniAd();
						if(!reg.isEmpty() && reg.size()<=2){
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
				
			} catch (RemoteException e1) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}
	
	//Frame su cui viene visualizzata tutta l'interfaccia grafica
	private JFrame frame;
	
	//Pannelli di destra e di sinistra dell'interfaccia grafica
	private JPanel rightPanel;
	private JPanel leftPanel;
	
	//Label e JLayeredPane per visualizzare la mappa e tutto ciò che viene posizionato sopra
	private JLabel mappa;
	private JLayeredPane layeredMappa;
	//Label delle tessere dei terreni
	private Map<TipoTerreno,JLabel> lblTessere = new HashMap<TipoTerreno,JLabel>();
	
	//Bottoni delle mosse che possono essere compiute
	private JButton btnSpostaPecora;
	private JButton btnSpostaPastore;
	private JButton btnCompraTessera;
	private JButton	btnAccoppiamento1;
	private JButton btnSparatoria1;
	private JButton btnSparatoria2;
	
	//Mappe usate per sapere le posizioni delle regioni e dei cancelli/pedine
	private Map<String,Point> posizioniRegioni = new HashMap<String,Point>();
	private Map<String,Point> posizioniCancelli = new HashMap<String, Point>();
	
	//Map per sapere i colori assegnati ad ogni giocatore
	private Map<Color,String>gioc;
	
	//Pedine dei giocatori posizionate sulla mappa
	private Map<Color,MovableLabel> pedineGiocatori = new HashMap<Color, MovableLabel>();
	
	//Label dei giocatori, per visualizzare per ogni giocatore i danari posseduti e sapere qual'è il giocatore corrente
	private Map<Color,JLabel> giocatori = new HashMap<Color,JLabel>();
	
	//Label delle pecore, presenti in ogni regione
	private Map<String,JLabel> lblPecore = new HashMap<String,JLabel>();
	
	//Label usate per evidenziare le regioni che è possibile premere per effettuare una determinata mossa
	private Map<String, JLabel> lblRegioni = new HashMap<String,JLabel>();
	
	//Queste sono le seconde pedine, per quando si gioca con due giocatori
	private Map<Color,MovableLabel> pedine2Giocatori = new HashMap<Color,MovableLabel>();
	
	//Label della pecora nera e del lupo
	private MovableLabel pecoraNera;
	private MovableLabel lupo;
	
	//label di output per comunicazioni con l'utente
	private JLabel lblOutput = new JLabel();
	
	private LabelDado lblDado;

	
	//Azioni collegate alle azioni fatte col mouse sulla mappa
	private AzioniMouse mouse;

	//Colore del giocatore a cui appartiene questa interfaccia grafica
	private Color coloreGamer;
	//Colore del giocatore corrente del turno
	private Color giocatoreCorrente;
	
	private TipoMossa mossaAttuale;
	
	private IFController controller;
	
	/**
	 * Costruttore a cui passo il controller che verrà utilizzato dall'interfaccia per compiere le mosse
	 * @param controller Implementazione del controller per poter giocare
	 * @throws RemoteException
	 */
	public View(IFController controller) throws RemoteException {
		this.controller=controller;
		mossaAttuale=TipoMossa.SELEZ_POSIZ;
		initGUI();
	}
	
	/**
	 * Inizializzazione dei componenti principali dell'interfaccia grafica
	 */
	private void initGUI(){
		//Inizializzazione del Frame principale
		frame= new JFrame("SHEEPLAND");
		frame.setBackground(BG_COLOR);
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(new ChiusuraSchermata(controller));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//CENTRALPANEL
		layeredMappa = new JLayeredPane();
		mappa = new JLabel();
		ImageIcon img = new ImageIcon(this.getClass().getResource("/immagini/game_board.png"));
		mappa.setIcon(img);
		mappa.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
		mappa.setVisible(true);
		mouse = new AzioniMouse(this.getClass().getResourceAsStream("/immagini/game_board_back.png"), this, controller);
		mouse.setRegioni("", "");
		mappa.addMouseListener(mouse);
		mappa.addMouseMotionListener(mouse);
		mappa.setLayout(null);

		layeredMappa.add(mappa, BACKGROUD_LAYER);
		layeredMappa.setSize(img.getIconWidth(), img.getIconHeight());
		layeredMappa.setBackground(BG_COLOR);
		layeredMappa.setOpaque(true);
		frame.add(layeredMappa, BorderLayout.CENTER);
		
		//RIGHTPANEL
		rightPanel = new JPanel();
		rightPanel.setBackground(BG_COLOR);
		rightPanel.setLayout(new GridLayout(6,1));
		AzioniBottoni action = new AzioniBottoni();
		
		//Creazione dei bottoni
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
		btnAccoppiamento1.setIcon(new ImageIcon(this.getClass().getResource("/immagini/pecoraaccoppiamento.png")));
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
		leftPanel.setBackground(BG_COLOR);
		leftPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//Creazione delle Tessere
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
		lblDado = new LabelDado();
		lblDado.setText("  ");
		c.gridx=0;
		c.gridy=8;
		c.fill=GridBagConstraints.BOTH;
		c.anchor=GridBagConstraints.LINE_START;
		leftPanel.add(lblDado,c);
		
		frame.add(leftPanel, BorderLayout.WEST);

		//LABEL PER GLI ERRORI e le COMUNICAZIONI CON L'UTENTE
		lblOutput = new JLabel();
		lblOutput.setText("ATTESA GIOCATORI!!");
		lblOutput.setBorder(new EmptyBorder(30,10,0,0));
		c.anchor=GridBagConstraints.WEST;
		frame.add(lblOutput,BorderLayout.SOUTH);
		
		//setto la dimensione minima della schermata
		frame.setMinimumSize(new Dimension(930,785));
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
		//Caricamento delle immagini di selezione delle regioni
		ImageIcon imgReg=null;
		JLabel lblReg=null;
		for(String s : posizioniRegioni.keySet()){
			imgReg= new ImageIcon(this.getClass().getResource("/immagini/regioni/regione_"+s+".png"));
			lblReg = new FlashLabel();
			lblReg.setIcon(imgReg);
			lblReg.setOpaque(false);
			lblReg.setVisible(false);
			lblReg.setBounds(0, 0, imgReg.getIconWidth(), imgReg.getIconHeight());
			lblRegioni.put(s,lblReg);
			layeredMappa.add(lblReg, REGION_LAYER);
		}
		
		//Visualizzazione delle label dei giocatori
		GridBagConstraints c = new GridBagConstraints();
		JLabel lbltemp2;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		int py=4;
		for(Color colore:gioc.keySet()){
			lbltemp2 = new MovableLabel();
			lbltemp2.setText(gioc.get(colore));
			lbltemp2.setName(gioc.get(colore));
			lbltemp2.setBackground(colore);
			lbltemp2.setFont(FONT_UNSELECTED);
			lbltemp2.setOpaque(true);
			lbltemp2.setBorder(new MatteBorder(10,10,10,10, colore));
			c.gridx=0;
			c.gridy=py;
			py++;
			leftPanel.add(lbltemp2,c);
			giocatori.put(colore,(MovableLabel) lbltemp2);
		}
		
		//faccio un repaint della mappa
		mappa.repaint();
		frame.pack();
		//setto la mossaAttuale come NO_MOSSA per evitare problemi
		mossaAttuale=TipoMossa.NO_MOSSA;
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
				giocatori.get(col).setFont(FONT_SELECTED);
				giocatori.get(col).setText(giocatori.get(col).getText());
			}else{
				giocatori.get(col).setEnabled(false);
				giocatori.get(col).setFont(FONT_UNSELECTED);
				giocatori.get(col).setText(giocatori.get(col).getText());
			}
		}
		if(color.equals(coloreGamer)){
			attivaGiocatore();
		}else{
			disattivaGiocatore();
		}
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
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraBianca(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraBianca(String s, String d){
		Point sorg= posizioniRegioni.get(s);
		Point dest= posizioniRegioni.get(d);
		
		//non posso usare direttamente la pecora presente per l'animazione, devo creare una nuova label
		final MovableLabel lblMovimentoPecora = new MovableLabel();
		ImageIcon img = new ImageIcon(this.getClass().getResource(IMM_PECORA_BIANCA));
		lblMovimentoPecora.setIcon(img);
		lblMovimentoPecora.setBounds(sorg.x, sorg.y, img.getIconWidth(), img.getIconHeight());
		layeredMappa.add(lblMovimentoPecora,SHEEP_LAYER);
		//setto il fatto che la label deve essere rimossa dopo aver terminato l'animazione
		lblMovimentoPecora.setRemoveAfterAnimation();
		//Avvio l'animazione della pecora
		lblMovimentoPecora.moveTo(dest, TEMPO_ANIMAZIONE_PECORA);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPastore(java.lang.String, java.lang.String, java.awt.Color)
	 */
	@Override
	public void spostaPastore(List<String> listaMov, Color colore){
		//Il campo s è inutile perchè usando la movableLabel, 
		//i movimenti sono fatti a partire dalla posizione in cui si trova ora la pedina
		
		//Costruisco la lista dei punti che il pastore deve attraversare
		List<Point> listaPunti = new ArrayList<Point>();
		for(String pos : listaMov){
			listaPunti.add(posizioniCancelli.get(pos));
		}
		
		//Prelevo la label collegata al giocatore
		MovableLabel pedGiocatore = pedineGiocatori.get(colore);

		//questo metod.o è usato anche nel caso di creazione della pedina, in questo caso,
		//la pedina non è ancora esistente e viene creata in quel momento
		if(pedGiocatore==null){
			//Se la pedina non esiste, 
			//la creo e la posiziono nella sua posizione che è quella di destinazione
			pedGiocatore= new MovableLabel();
			ImageIcon img = getImagePastore(colore);
			pedGiocatore.setIcon(img);
			pedGiocatore.setBounds(listaPunti.get(listaPunti.size()-1).x, listaPunti.get(listaPunti.size()-1).y, img.getIconWidth(), img.getIconHeight());
			layeredMappa.add(pedGiocatore, View.SHEPPARD_LAYER);
			//Aggiungo la pedina alla map delle pedine dei giocatori
			pedineGiocatori.put(colore, pedGiocatore);
		} else{
			//Se la pedina esiste, la muovo
			//Attivo l'animazione della pedina
			//Rimuovo la posizione di partenza
			listaPunti.remove(0);
			pedGiocatore.moveTo(listaPunti, TEMPO_ANIMAZIONE_PASTORE);
		}

		
		
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#posiziona2Pastore(java.lang.String, java.awt.Color)
	 */
	@Override
	public void posiziona2Pastore(String idStrada, Color colore) {
		//Creo e posiziono la pedina, per il secondo pastore, nel caso si usino due giocatori
		MovableLabel pedina2= new MovableLabel();
		//La aggiunto alla map per le seconde pedine
		pedine2Giocatori.put(colore, pedina2);
		ImageIcon img = getImagePastore(colore);
		pedina2.setIcon(img);
		Point dest = posizioniCancelli.get(idStrada);
		pedina2.setBounds(dest.x, dest.y, img.getIconWidth(), img.getIconHeight());
		layeredMappa.add(pedina2, SHEPPARD_LAYER);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraNera(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaPecoraNera(String s, String d){
		/*
		 * Questo metodo viene invocato anche per la creazione della label della pecora nera da parte del controller
		 */
		//Il campo s diventa inutile usando la movableLabel
		Point dest= (Point) posizioniRegioni.get(d).clone();
		dest.y+=OFFSET_Y_NERA;
		//se la label della pecora nera non è ancora stata creata, la creo.
		if(pecoraNera==null){
			ImageIcon pecNera = new ImageIcon(this.getClass().getResource(IMM_PECORA_NERA));
			pecoraNera = new MovableLabel();
			pecoraNera.setIcon(pecNera);
			pecoraNera.setBounds(dest.x, dest.y, pecNera.getIconWidth(), pecNera.getIconHeight());
			layeredMappa.add(pecoraNera, SHEEP_LAYER);
		}
	
		//Avvio l'animazione della pedina
		pecoraNera.moveTo(dest,TEMPO_ANIMAZIONE_PECORA);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#spostaPecoraNeraLupo(java.lang.String, java.lang.String)
	 */
	@Override
	public void spostaLupo(String s, String d){
		/*
		 * Questo metodo viene invocato anche per la creazione della label del lupo da parte del controller
		 */
		//Il campo s diventa inutile usando la movablelabel
		Point dest= (Point) posizioniRegioni.get(d).clone();
		dest.y+=OFFSET_Y_LUPO;
		dest.x+=OFFSET_X_LUPO;
		if(lupo==null){
			ImageIcon iconLupo = new ImageIcon(this.getClass().getResource(IMM_LUPO));
			lupo = new MovableLabel();
			lupo.setIcon(iconLupo);
			lupo.setBounds(dest.x, dest.y, iconLupo.getIconWidth(), iconLupo.getIconHeight());
			layeredMappa.add(lupo, SHEEP_LAYER);
		}
		//Attivo l'animazione
		lupo.moveTo(dest, TEMPO_ANIMAZIONE_PECORA);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#modificaQtaPecora(java.lang.String, int)
	 */
	@Override
	public void modificaQtaPecora(String idReg, int num, String testo){
		/*
		 * Questo metodo viene invocato anche per la creazione delle label delle pecore da parte del controller
		 */
		//Prelevo la label di cui modificare il testo
		JLabel lblPecora =lblPecore.get(idReg);
		//Se la label della pecora non è ancora stata creata, viene inizializzata
		if(lblPecora==null){
			//Alloco la nuova label della pecora che mi serve
			Point pos=posizioniRegioni.get(idReg);
			ImageIcon iconBianca = new ImageIcon(this.getClass().getResource(IMM_PECORA_BIANCA));
			lblPecora = new MovableLabel(this.getClass().getResourceAsStream(IMM_PECORA_BIANCA));
			lblPecora.setBounds(pos.x, pos.y, iconBianca.getIconWidth(), iconBianca.getIconHeight());
			lblPecora.setVisible(true);
			lblPecore.put(idReg, lblPecora);
		}
		//Controllo se la label va o meno nascosta, 
		//viene nascosta se il numero di pecore è 0
		if(num>0){
			layeredMappa.add(lblPecora, SHEEP_LAYER);
			lblPecora.setText("      "+num);
		} else{
			layeredMappa.remove(lblPecora);
		}
		layeredMappa.repaint();
		if(testo!=null && !" ".equals(testo) && !"".equals(testo)){
			visMessaggioNuovoThread(testo);
		}
	}
	
	
	/*/**
	 * Modifica il testo visualizzato su di una JLabel passata per parametro
	 * @param lblModificare Label dal modificare 
	 * @param qta Numero da visualizzare sopra la label
	 */
	/*private void modificaQtaLabel(JLabel lblModificare, int qta){
		//Controllo se la label va o meno nascosta, 
		//viene nascosta se il numero di pecore è 0
		if(qta>0){
			layeredMappa.add(lblModificare, SHEEP_LAYER);
			lblModificare.setText("  "+qta);
		} else{
			layeredMappa.remove(lblModificare);
		}
		layeredMappa.repaint();
	}
	
	public void modificaQtaAriete(String idReg, int num, String testo){
		/*
		 * Questo metodo viene invocato anche per la creazione delle label dell'ariete da parte del controller
		 */
		//Prelevo la label di cui modificare il testo
		/*JLabel lblAriete =lblArieti.get(idReg);
		//Se la label dell'ariete non è ancora stata creata, viene inizializzata
		if(lblAriete==null){
			//Alloco la nuova label dell'ariete che mi serve
			Point pos=posizioniRegioni.get(idReg);
			ImageIcon iconBianca = new ImageIcon(this.getClass().getResource(IMM_ARIETE));
			lblAriete = new MovableLabel(this.getClass().getResourceAsStream(IMM_ARIETE));
			lblAriete.setBounds(pos.x+OFFSET_X_ARIETE, pos.y+OFFSET_Y_ARIETE, iconBianca.getIconWidth(), iconBianca.getIconHeight());
			lblAriete.setVisible(true);
			lblArieti.put(idReg, lblAriete);
		}
		
		//Modifico il testo visualizzato sulla label
		modificaQtaLabel(lblAriete,num);
		
		if(testo!=null && !" ".equals(testo) && !"".equals(testo)){
			visMessaggioNuovoThread(testo);
		}
	}
	
	public void modificaQtaAgnello(String idReg, int num, String testo){
		/*
		 * Questo metodo viene invocato anche per la creazione delle label dell'ariete da parte del controller
		 */
		//Prelevo la label di cui modificare il testo
	/*	JLabel lblAgnello =lblAgnelli.get(idReg);
		//Se la label dell'ariete non è ancora stata creata, viene inizializzata
		if(lblAgnello==null){
			//Alloco la nuova label dell'ariete che mi serve
			Point pos=posizioniRegioni.get(idReg);
			ImageIcon iconBianca = new ImageIcon(this.getClass().getResource(IMM_AGNELLO));
			lblAgnello = new MovableLabel(this.getClass().getResourceAsStream(IMM_AGNELLO));
			lblAgnello.setBounds(pos.x+OFFSET_X_AGNELLO, pos.y+OFFSET_Y_AGNELLO, iconBianca.getIconWidth(), iconBianca.getIconHeight());
			lblAgnello.setVisible(true);
			lblAgnelli.put(idReg, lblAgnello);
		}
		//Modifico la quantità visualizzata nella label
		modificaQtaLabel(lblAgnello,num);
		if(testo!=null && !" ".equals(testo) && !"".equals(testo)){
			visMessaggioNuovoThread(testo);
		}
	}*/

	/**
	 * Metodo per visualizzare un messaggio all'utente sul JOptionPane che gira in un thread separato dal chiamante
	 * @param testo
	 */
	private void visMessaggioNuovoThread(String testo) {
			//creo un thread per comunicare all'utente l'azione che è avvenuta
			final String text=testo;
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					JOptionPane.showMessageDialog(View.this.frame, text);
					
				}
			});
			t.start();
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
		//Prelevo la label di cui modificare i soldi, e modifico i soldi visualizzati
		JLabel lblGiocModSoldi=giocatori.get(coloreGiocatoreDaModificare);
		String nome = lblGiocModSoldi.getName();
		lblGiocModSoldi.setText(nome+" SOLDI: "+soldi);
		lblGiocModSoldi.repaint();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#incPrezzoTessera(it.polimi.iodice_moro.model.TipoTerreno)
	 */
	@Override
	public void incPrezzoTessera(TipoTerreno tess){
		//Prelevo la tessera di cui modificare il prezzo
		JLabel lblTessera = (JLabel)lblTessere.get(tess).getParent();
		int posx = 20* (lblTessera.getComponentCount()-1);
		if(posx<0){
			lblOutput.setText("ERRORE NELL'INCREMENTO DEL PREZZO TESSERA!!");
			return;
		}
		//Creo la nuova label per visualizzare una moneta per ogni unità di costo
		JLabel lblDanaro = new JLabel();
		ImageIcon imgDanaro = new ImageIcon(this.getClass().getResource("/immagini/danaro.png"));
		lblDanaro.setIcon(imgDanaro);
		//aggiungo la label con l'immagine del danaro alla label della tessera di cui modificare il prezzo
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
			lblOutput.setText(vis);
		} else{
			//Non è il turno di questo giocatore
			lblOutput.setText("Non è il tuo turno!!");
		}
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFView#visRisDado(int)
	 */
	@Override
	public void visRisDado(int numero) {
		if(giocatoreCorrente.equals(coloreGamer)){
			disattivaGiocatore();
		}
		lblDado.visualizzaDado(numero,"    ");
		if(giocatoreCorrente.equals(coloreGamer)){
			attivaGiocatore();
		}
	}
	
	
	//METODI per settare le posizioni delle regioni, dei cancelli e dei giocatori
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#setPosizionoRegioni
	 */
	@Override
	public void setPosizioniRegioni(Map<String, Point> posizioniRegioni) {
		this.posizioniRegioni=posizioniRegioni;
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#setPosizioniStrade
	 */
	@Override
	public void setPosizioniStrade(Map<String, Point> posizioniCancelli) {
		this.posizioniCancelli=posizioniCancelli;
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#setGiocatori
	 */
	@Override
	public void setGiocatori(Map<Color, String> giocatori) {
		this.gioc=giocatori;
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#close
	 */
	@Override
	public void close() throws RemoteException {
		//Creo un Thread per chiudere l'applicazione, 
		//cosi il controllo può tornare al metodo remoto che l'ha chiamato
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				JOptionPane.showMessageDialog(frame,"Un utente si è disconnesso, la partita termina qui. \n Chiusura dell'applicazione");
				System.exit(0);
			}
		});
		t.start();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#selezPast(java.awt.Color)
	 */
	@Override
	public void selezPast(Color colore){
		if(coloreGamer.equals(colore)){
			disattivaGiocatore();
			lblOutput.setText("Seleziona il pastore che vuoi utilizzare!!");
			mossaAttuale=TipoMossa.G2_SELEZ_PAST;
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.view.IFVie#usaPast2(java.awt.Color)
	 */
	@Override
	public void usaPast2(Color colore) throws RemoteException {
		//metodo usato solo in caso di due giocatori
		//scambio le pedine tra la prima e la seconda pedina, 
		//perchè va mossa la seconda pedina e non la prima
		MovableLabel lblCambio=pedineGiocatori.get(colore);
		pedineGiocatori.put(colore, pedine2Giocatori.get(colore));
		pedine2Giocatori.put(colore, lblCambio);
		
	}
	
	@Override
	public void attendiGiocatori() {
		//Nella vera implementazione della view(GUI) non viene usato questo metodo
	}
	
	
	
	/**
	 * Setting del colore del giocatore associato a questa interfaccia grafica
	 * @param colore
	 */
	public void setColore(Color colore) {
		//assegno il colore all'interfaccia
		this.coloreGamer=colore;
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		JLabel lblGamer = new JLabel();
		//Visualizzo una label che comunica all'utente qual'è il colore delle sue pedine
		lblGamer.setText("Questo è il tuo colore!");
		lblGamer.setBackground(colore);
		lblGamer.setOpaque(true);
		lblGamer.setBorder(new MatteBorder(20,20,20,20, colore));
		leftPanel.add(lblGamer,c);
	}
	
	/**
	 * Attiva la possibilità di cliccare sui bottoni delle mosse
	 */
	private void attivaGiocatore(){
		btnCompraTessera.setEnabled(true);
		btnSpostaPastore.setEnabled(true);
		btnSpostaPecora.setEnabled(true);
		btnAccoppiamento1.setEnabled(true);
		btnSparatoria1.setEnabled(true);
		btnSparatoria2.setEnabled(true);
		mossaAttuale=TipoMossa.NO_MOSSA;
	}

	/**
	 * Disattiva la possibità di cliccare sui bottoni delle mosse
	 */
	private void disattivaGiocatore(){
		btnCompraTessera.setEnabled(false);
		btnSpostaPastore.setEnabled(false);
		btnSpostaPecora.setEnabled(false);
		btnAccoppiamento1.setEnabled(false);
		btnSparatoria1.setEnabled(false);
		btnSparatoria2.setEnabled(false);
		mossaAttuale=TipoMossa.NO_MOSSA;
	}
	
	/**
	 * Metodo per aggiungere un cancello alla strada passata come ID
	 * @param stradaID ID della strada dove aggiungere il cancello
	 * @param imgCancello Immagine da visualizzare
	 */
	private void mettiCancello(String stradaID, ImageIcon imgCancello){
		JLabel lblCancello = new JLabel();
		//Setto l'icona del cancello adeguata
		lblCancello.setIcon(imgCancello);
		//Prelevo la posizione in cui posizionare il cancello
		Point pos = posizioniCancelli.get(stradaID);
		//Setto posizione e dimenisone della label
		lblCancello.setBounds(pos.x, pos.y, imgCancello.getIconWidth(), imgCancello.getIconHeight());
		//Aggiungo la label alla layeredMappa nel layer dei cancelli
		layeredMappa.add(lblCancello, FENCE_LAYER);
		layeredMappa.repaint();
	}
	
	/**
	 * Utilizzato per prelevare l'immagine della pedina collegata al colore del pastore
	 * @param colore Colore del pastore di cui si vuole prelevare l'immagine
	 * @return Immagine della pedina del pastore
	 */
	private ImageIcon getImagePastore(Color colore) {
		ImageIcon img = null;
		//carico l'icona della pedina corretta
		if(colore.equals(new Color(255,0,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinarossa.png"));
		}
		if(colore.equals(new Color(0,255,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinaverde.png"));
		}
		if(colore.equals(new Color(0,0,255))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinaazzurra.png"));
		}
		if(colore.equals(new Color(255,255,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinagialla.png"));
		}
		return img;
	}
	
	/**
	 * @return Ritorna l'hashMap delle posizioni dei giocatori collegate ai loro colori
	 */
	public Map<String,Point> getPosizioniRegioni() {
		return posizioniRegioni;
	}
	
	/**
	 * @return Ritorna la mossa attuale che il giocatore ha intenzione di compiere
	 */
	public TipoMossa getMossaAttuale() {
		return mossaAttuale;
	}

	/**
	 * @return Ritorna il riferimento alla label per l'output e la comunicazione con l'utente
	 */
	public JLabel getLBLOutput() {
		return lblOutput;
		
	}

	/**
	 * @param mossa Mossa che vogliamo diventi la mossa attuale
	 */
	public void setMossaAttuale(TipoMossa mossa) {
		mossaAttuale=mossa;	
	}

	/**
	 * @return Ritorna l'hashMap delle posizioni dei cancelli
	 */
	public Map<String, Point> getPosizioniCancelli() {
		return posizioniCancelli;
	}
	
	/**
	 * @param idReg ID della regione di cui voglio prelevare la label associata(quella della pecora)
	 * @return Label associata alla regione
	 */
	public JLabel getLBLRegione(String idReg){
		return lblRegioni.get(idReg);
	}
	
	/**
	 * @return Ritorna il colore del giocatore corrente
	 */
	public Color getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	/**
	 * @return Ritorna il riferimento alla label della pecora nera
	 */
	public JLabel getLBLPecoraNera() {
		return pecoraNera;
	}

	/**
	 * @return Ritorna il colore del giocatore a cui è associata questa interfaccia grafica
	 */
	public Color getColoreGamer() {
		return coloreGamer;
	}
}