package it.polimi.iodice_moro.Model;


//Classe usata solo per avere presenza contemporanea di strade e regioni come vertici del grafo
//(limitazione dovuto all'unico tipo possibile di vertice permesso dalla libreria JGraphT
public abstract class VerticeGrafo {
	
	//metodo che verrà implemetnato nelle sottoclassi per verificare se l'istanza corrente è una regione o una strada
	public abstract boolean isRegione();

}
