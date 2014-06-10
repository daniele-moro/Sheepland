package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ViewRMI implements IFView {
	
	Map<Color, IFView> listaView;

	public ViewRMI() {
		listaView = new HashMap<Color, IFView>();
		
	}
	
	public void initMappa() {
		for(IFView view : listaView.values()) {
			try {
				view.initMappa();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void cambiaGiocatore(Color color) {
		for(IFView view : listaView.values()){
			try {
				view.cambiaGiocatore(color);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	
	/*public void attivaGiocatore() {
		// TODO Auto-generated method stub

	}*/
	@Override
	public void disattivaGiocatore() {
		for(IFView view : listaView.values()) {
			try {
				view.disattivaGiocatore();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	public void disattivaGiocatore(Color giocatoreDaDisattivare) {
		try {
			listaView.get(giocatoreDaDisattivare).disattivaGiocatore();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	/*@Override
	public void attivaGiocatore(Color giocatoreCorrente) {
		try {
			listaView.get(giocatoreCorrente).attivaGiocatore(giocatoreCorrente);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

	@Override
	public void addCancelloNormale(String stradaID) {
		for(IFView view : listaView.values()) {
			try {
				view.addCancelloNormale(stradaID);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		for(IFView view : listaView.values()) {
			try {
				view.addCancelloFinale(stradaID);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void spostaPecoraBianca(String s, String d) {
		for(IFView view : listaView.values()) {
			try {
				view.spostaPecoraBianca(s, d);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void spostaPastore(String s, String d, Color colore) {
		for(IFView view : listaView.values()) {
			try {
				view.spostaPastore(s, d, colore);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void spostaPecoraNera(String s, String d) {
		for(IFView view : listaView.values()) {
			try {
				view.spostaPecoraNera(s, d);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		for(IFView view : listaView.values()) {
			try {
				view.modificaQtaPecora(idReg, num);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num, Color colore) {
		try {
			listaView.get(colore).modQtaTessera(tess, num, colore);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		for(IFView view : listaView.values()) {
			try {
				view.modSoldiGiocatore(coloreGiocatoreDaModificare, soldi);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) {
		for(IFView view : listaView.values()) {
			try {
				view.incPrezzoTessera(tess);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {
		for(IFView view : listaView.values()) {
			try {
				view.visualizzaPunteggi(punteggiOrdinati);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setGiocatoreCorrente(Color colore) {
		for(IFView view : listaView.values()) {
			try {
				view.setGiocatoreCorrente(colore);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	//Aggiunge istanza View alla lista della View. Utilizzato in implementazione View RMI.
	public void addView(IFView view, Color coloreGiocatore) throws RemoteException {
		listaView.put(coloreGiocatore, view);
	}

	@Override
	public void setColore(Color coloreGiocatore) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attivaGiocatore() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attendiGiocatori() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visRisDado(int numero) throws RemoteException {
		// TODO Auto-generated method stub
		
	}



}
