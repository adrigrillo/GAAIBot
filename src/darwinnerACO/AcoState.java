package darwinnerACO;

import ontology.Types;

public class AcoState {
	
	ontology.Types.ACTIONS accion;
	int vida;
	double score;
	Types.WINNER[] ganador;
	
	// Constructor por defecto
	public AcoState(){
	}

	public ontology.Types.ACTIONS getAccion() {
		return accion;
	}

	public void setAccion(ontology.Types.ACTIONS accion) {
		this.accion = accion;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Types.WINNER[] getGanador() {
		return ganador;
	}

	public void setGanador(Types.WINNER[] ganador) {
		this.ganador = ganador;
	}

}
