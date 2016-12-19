/**************************************************************************************************
 * Autores:
 * 		Ruben Rodriguez - 100303579@alumnos.uc3m.es
 * 		Raul Lopez Rayo - 100073776@alumnos.uc3m.es
 * 		Daniel Jerez Garrido - 100303628@alumnos.uc3m.es
 * 		Juan Poblete Sandoval - 100303554@alumnos.uc3m.es
 * 		Luis Buceta Ojeda - 100303573@alumnos.uc3m.es
 * 		Adrian Rodriguez Grillo - 100316457@alumnos.uc3m.es
 * Algoritmos geneticos y evolutivos
 * Practica 2: Competicion de Inteligencia Artificial Generica aplicada a Videojuegos (2 Jugadores)
 *************************************************************************************************/

package darwinnerACO;

import ontology.Types;

public class AcoState {
	
	/*
	 * Atributos de la clase
	 */
	// Accion que ha permitido alcanzar el estado
	ontology.Types.ACTIONS accion;
	// Ultima accion que el rival ha realizado
	ontology.Types.ACTIONS ultimaAccionRival;
	// Vida actual del jugador
	int vida;
	// Puntuacion actual del jugador
	double score;
	// Ganador de la partida, si existe
	Types.WINNER[] ganador;
	
	// Constructor por defecto
	public AcoState(){
	}
	
	/*
	 * Geters y seters auto-generados para construir el estado
	 */
	
	public ontology.Types.ACTIONS getUltimaAccionRival() {
		return ultimaAccionRival;
	}

	public void setUltimaAccionRival(ontology.Types.ACTIONS ultimaAccionRival) {
		this.ultimaAccionRival = ultimaAccionRival;
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