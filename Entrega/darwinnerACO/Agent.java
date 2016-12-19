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

import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import ontology.Types;
//import ontology.Types.ACTIONS;
//import ontology.sprites.npc.RandomInertial;
import tools.ElapsedCpuTimer;

//import java.time.Clock;
import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Random;

public class Agent extends AbstractMultiPlayer {

	/*
	 * Variables auxiliares del estado del juego
	 */
	// ArrayList de acciones disponibles
	public ArrayList<ontology.Types.ACTIONS> acciones;
	// Vida del agente
	int vida;
	// Numero de jugadores
	int no_players;
	// Id del Agente
	int id;
	// Id del Oponente
	int oppId;
	// Score del Agente
	double score;
	/*
	 * Parametros de ACO
	 */
	// Numero de hormigas
	int no_hormigas = 3;
	// Profundidad permitida para las hormigas
	int profundidad;
	// Feromona a depositar por cada hormiga cuando se transita por un estado
	double feromona = 50.0;
	// Peso del valor de la evaluacion del estado para crear el valor de probabilidad de un estado
	double pesoEvaluacion = 0.6;
	// Peso del valor de la feromona del estado para crear el valor de probabilidad de un estado
	double pesoFeromonas = 0.4;
	// Grafo de las hormigas
	Object[] grafo;
	// Estado auxiliar de ACO para la construccion del grafo
	AcoState state;
	// Estado auxiliar del juego para la construccion del grafo
	StateObservationMulti stateUtil;
	/*
	 * Parametros de la evaluacion; funcion heuristica
	 */
	private static int HUGE_ENDGAME_SCORE = 1000;
	private static int MAX_TIMESTEPS = 40;

	/**
	 * Constructor del agente
	 * 
	 * @param stateObs: Estado de observacion en el turno actual
	 * @param elapsedTimer: Temporizador para la creacion del agente
	 * @param playerID: Id del jugador
	 */
	public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID) {

		/*
		 * Variables utiles
		 */
		acciones = stateObs.getAvailableActions(playerID);

		/*
		 * Construccion del grafo inicial. Solo se expande 1 nivel.
		 * Estructura:
		 * 	[0]: estado del juego (accion, ultimaAccionRival, vida, score, ganador)
		 * 	[1]: hijos
		 * 	[2]: feromona
		 * 	[3]: evaluacion
		 * 	[4]: probabilidades
		 */
		grafo = new Object[acciones.size() * 5];
		int auxCont = 0;

		for (ontology.Types.ACTIONS accion : acciones) {
			stateUtil = stateObs.copy();
			state = new AcoState();
			state.setAccion(accion);
			state.setVida(stateUtil.getAvatarHealthPoints(id));
			state.setScore(stateUtil.getGameScore(id));
			state.setGanador(stateUtil.getMultiGameWinner());
			grafo[auxCont] = state;
			grafo[auxCont + 2] = 0.0;
			grafo[auxCont + 3] = evaluacion(stateUtil, id);
			grafo[auxCont + 4] = 0.0;
			auxCont += 5;
		}

		/*
		 * Viaje de hormigas
		 */
		// Creamos un array en el que guardaremos nuestros resultados de aplicar la funcion de relevancia de cada enlace en base a la evaluacion y las feromonas
		double[] relevancias = new double[acciones.size()];
		double totalRelevancias = 0;
		
		// Flag que utilizaremos para realizar la construccion de la tabla de decision tan solo una vez por cada nodo y turno
		boolean primeraHormiga = true;

		// Recorremos la matriz tantas veces como hormigas haya
		for (int i = 0; i < no_hormigas; i++) {

			if (primeraHormiga) {

				// Rellenamos el array de relevancias para cada camino disponible
				for (int j = 0; j < grafo.length; j += 5) {
					relevancias[j / 5] = pesoEvaluacion * (double) grafo[j + 3] + pesoFeromonas * (double) grafo[j + 2];
					// Acumulamos los valores de relevancia en una variable
					totalRelevancias += relevancias[j / 5];
				}
				
				// Creamos un rango de decision entre 0 y 1 para cada camino posible, cuanto mayor sea este rango, mayor sera la probabilidad de ser elegido
				if (totalRelevancias != 0) {
					grafo[4] = relevancias[0] / totalRelevancias;
					for (int j = 1; j < acciones.size(); j++) {
						grafo[j * 5 + 4] = (double) grafo[(j - 1) * 5 + 4] + relevancias[j] / totalRelevancias;
					}
				}

				primeraHormiga = false;
			}

			// Numero aleatorio para la decision del camino
			double aux = Math.random();
			int indiceMeta = -1;

			// Dados los rangos de decision calculados anteriormente, escogemos aquel camino en cuyo rango se encuentre el valor aleatorio aux
			for (int j = 0; j < acciones.size(); j++) {
				System.out.println(aux + " --> " + grafo[j * 5 + 4]);
				if (aux <= (double) grafo[j * 5 + 4]) {
					indiceMeta = j;
				}
			}
			// En caso de que aun no haya valores en el slot de probabilidades del nodo, escogemos un camino aleatorio
			if (indiceMeta == -1)
				indiceMeta = (int) (Math.random() * 4);

			// Aumentamos la feromona del camino que ha elegido la hormiga
			grafo[(indiceMeta * 5) + 3] = (double) grafo[(indiceMeta * 5) + 3] + feromona;
		}
	}

	/**
	 * Eleccion de la accion a realizar
	 * 
	 * @param stateObs: Estado actual de la partida.
	 * @param elapsedTimer: Tiempo en el que la accion acaba, limite de tiempo.
	 * @return accion que el agente debe realizar
	 */
	public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {

		/*
		 * TODO: localizar el estado actual en el grafo,
		 * comparando sus caracteristicas con el que posee cada estado dentro del grafo.
		 * El estado mas parecido es la nueva referencia de la variable grafo
		 */
		
		/*
		 * TODO: iterar camino de hormigas para que puedan viajar a varios niveles de profundiad
		 */
		
		/*
		 * TODO: introducir codigo del constructor adaptado para iterar varias veces segun profundidad,
		 * y que solo amplie los caminos necesarios.
		 */

		
		/*
		 * Dado que no es un agente funcional,
		 * se indica return null para evitar que de errores de compilacion en el editor con el que sea revisado
		 */
		return null;
	}

	/**
	 * Evaluacion del agente: evaluacion de los caminos de las hormigas a traves de una funcion heuristica,
	 * que ha sido utilizada en otros agentes de la practica
	 * @param stateObs: Estado actual de la partida.
	 * @param elapsedTimer: Tiempo en el que la accion acaba, limite de tiempo.
	 * @return scores: Valor de la funcion heuristica que determina la calidad del estado
	 */
	public static double evaluacion(StateObservationMulti stateObs, int id) {
		double scores = 0;

		scores = stateObs.getGameScore();

		if (stateObs.isGameOver()) {
			if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS) {
				scores += HUGE_ENDGAME_SCORE;
			} else if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES) {
				if (stateObs.getGameTick() == MAX_TIMESTEPS) {
					scores -= HUGE_ENDGAME_SCORE * 0.8;
				} else {
					scores -= HUGE_ENDGAME_SCORE;
				}
			}
		}

		if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES
				&& stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES) {
			scores += 0.8 * HUGE_ENDGAME_SCORE;
			scores += 1.0 * HUGE_ENDGAME_SCORE + 1;
		}

		else if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS
				&& stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS) {
			scores -= 0.8 * HUGE_ENDGAME_SCORE;
		}

		return scores;
	}
}