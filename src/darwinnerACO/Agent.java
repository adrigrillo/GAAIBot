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
import ontology.Types.ACTIONS;
import ontology.sprites.npc.RandomInertial;
import tools.ElapsedCpuTimer;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class Agent extends AbstractMultiPlayer {

	/*
	 * Variables auxiliares del estado del juego
	 */
	// ArrayList de acciones disponibles
    public ArrayList <ontology.Types.ACTIONS> acciones;
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
    // Feromona a depositar
    double feromona = 50.0;
	//Pesos que se daran a evaluacion y feromona a la hora de escoger las hormigas un camino 
    double pesoEvaluacion = 0.6;
    double pesoFeromonas = 0.4;
    // Grafo de las hormigas
    Object [] grafo;
    // Estado de ACO
    AcoState state;
    // Estado auxiliar del juego
    StateObservationMulti stateUtil;
    // Variable auxiliar para indicar si es la primera accion del juego
    int flagInit = 0;
    /*
     * Parametros de la evaluacion
     */
    private static int HUGE_ENDGAME_SCORE = 1000;
    private static int MAX_TIMESTEPS = 40;

    /**
     * Constructor del agente
     * @param stateObs: Estado de observacion en el turno actual
     * @param elapsedTimer: Temporizador para la creacion del agente
     * @param playerID: Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

    	/*
    	 * Variables utiles
    	 */
        acciones = stateObs.getAvailableActions(playerID);
        id = playerID;
        
        /*
         * Construccion del grafo inicial.
         * Solo se expande 1 nivel.
         * Estructura:
         * [0]: estado del juego (accion, vida, score, winner)
         * [1]: hijos
         * [2]: feromona
         * [3]: evaluacion
         * [4]: probabilidades
         */
        grafo = new Object [acciones.size()*5];
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
        // Recorremos la matriz tantas veces como hormigas haya
        for (int i = 0; i < no_hormigas; i++) {
        	
    		/*
    		 * Calculamos las probabilidades de decision de camino de las hormigas
    		 */
            // Array de relevancias: indica cuanto de importante es cada estado para la hormiga
    		double [] relevancias = new double[acciones.size()];
    		double totalRelevancias = 0;
    		for (int j = 0; j < grafo.length; j+=5) {
    			relevancias[j/5] = pesoEvaluacion * (double)grafo[j+3] + pesoFeromonas * (double)grafo[j+2];
    			// Acumulamos los valores de relevancia
    			totalRelevancias += relevancias[j/5]; 
    			System.out.println(j/5 + " rel - " + relevancias[j/5]);
    		}
    		
    		  double [] probabilidades = new double[acciones.size()];
    		  probabilidades[0] = relevancias[0]/totalRelevancias;
    		  for(int j = 1; j < probabilidades.length; j++){
    		   probabilidades[j] = probabilidades[j-1] + relevancias[j]/totalRelevancias;
    		  }
    		  
    		// Numero aleatorio para la decision
    		  double aux = Math.random();
    		  int indiceMeta = 0;
    		  
    		  for(int j = 0; j < probabilidades.length; j++){
    		   if (aux <= probabilidades[j]){
    			   indiceMeta = j;
    		   }
    		  }
    		
			// Aumentamos la feromona del camino que ha elegido la hormiga
			grafo[(indiceMeta*5)+3] = (double)grafo[(indiceMeta*5)+3] + feromona;
			
			// Debug indiceMeta
			System.out.println(indiceMeta);
		}
        
        // Debug grafo
        for (int i = 0; i < grafo.length; i++) {
			System.out.println(grafo[i]);
		}
    }

    /**
     * Eleccion de la accion a realizar
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer){
    	    	
    	// Acciones disponibles
    	acciones = stateObs.getAvailableActions(id);
		
    	// Primera accion de la partida
    	if (flagInit == 0){
    		
            return null;
    	}
    	else{
            return null;
    	}
		
        /*
         * Exploracion de las hormigas (Trasladar al metodo que haga falta)
         */
    
        /*
         * Evaluacion de caminos de las hormigas (Trasladar al metodo que haga falta)
         */
        
    	/*
    	 * Podar grafo
    	 */
    	
        /*
         * Potenciar caminos de las hormigas (Trasladar al metodo que haga falta)
         */	
    }

    /**
     * Evaluacion del agente: evaluacion de los caminos de las hormigas
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public static double evaluacion(StateObservationMulti stateObs, int id){
        double scores = 0;

            scores = stateObs.getGameScore();

            if (stateObs.isGameOver()) {
                if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS) {
                    scores += HUGE_ENDGAME_SCORE;
                }
                else if (stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES) {
                    if(stateObs.getGameTick() == MAX_TIMESTEPS){
                        scores -= HUGE_ENDGAME_SCORE * 0.8;
                    }
                    else{
                        scores -= HUGE_ENDGAME_SCORE;
                    }
                }
            }

        if(stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES &&
                stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_LOSES){
            scores += 0.8 * HUGE_ENDGAME_SCORE;
            scores += 1.0 * HUGE_ENDGAME_SCORE + 1;
        }

        else if(stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS &&
                stateObs.getMultiGameWinner()[id] == Types.WINNER.PLAYER_WINS){
            scores -= 0.8 * HUGE_ENDGAME_SCORE;
        }

        return scores;
    }
}