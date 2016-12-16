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
import tools.ElapsedCpuTimer;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Random;


public class Agent extends AbstractMultiPlayer {

	/*
	 * Variables globales
	 */
	// ArrayList de acciones
    public ArrayList <ontology.Types.ACTIONS> acciones;
    // Vida del agente
    int vida;
    // Numero de jugadores
    int no_players;
    // Id del Agente
    int id;
    // Id del Oponente
    int oppId;
    // Numero de hormigas
    int no_hormigas;
    // Grafo de las hormigas
    public ArrayList <StateObservationMulti> grafo = new ArrayList<>();
    // Estado auxiliar
    StateObservationMulti stateAux;
    // Accion auxiliar
    ontology.Types.ACTIONS accionAux;
    // Lista auxiliar de acciones
    public ArrayList <ontology.Types.ACTIONS> accionesAux = new ArrayList<>();

    /**
     * Constructor del agente
     * @param stateObs: Estado de observacion en el turno actual
     * @param elapsedTimer: Temporizador para la creacion del agente
     * @param playerID: Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        /*
         * Parametros de la Colonia de Hormigas
         */
    	no_hormigas = 3;

    	/*
    	 * Variables utiles
    	 */
        acciones = stateObs.getAvailableActions(playerID);
        vida = stateObs.getAvatarHealthPoints();
        no_players = stateObs.getNoPlayers();
        id = playerID;
        oppId = (playerID + 1) % stateObs.getNoPlayers();
        
        /*
         * Debug
         */
        // Id y vida del agente
        System.out.println("Player "+playerID + " Vida: "+vida);
        // Acciones
        for(int i=0;i<acciones.size();i++){
            System.out.println(acciones.get(i).toString());
        }

        /*
         *  Generacion del grafo inicial
         */
        grafo.add(stateObs);
        for (ontology.Types.ACTIONS actions : acciones) {
        	stateAux = stateObs;
        	stateAux.advance(actions);
        	grafo.add(stateObs);
        	if (actions != null)
        		accionesAux.add(actions);
		}
    }

    /**
     * Eleccion de la accion a realizar
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer){
    	
    	acciones = stateObs.getAvailableActions(id);
    	
    	/*
         *  Generacion del grafo inicial
         */
    	accionesAux = new ArrayList<>();
    	grafo = new ArrayList<>();
        grafo.add(stateObs);
        for (ontology.Types.ACTIONS actions : acciones) {
        	stateAux = stateObs;
        	stateAux.advance(actions);
        	grafo.add(stateObs);
        	accionesAux.add(actions);
		}
        
        return accionesAux.get(new Random().nextInt(accionesAux.size()));

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
    	
    	
        //return null;
    }

    /**
     * Evaluacion del agente: evaluacion de los caminos de las hormigas
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public int evaluacion(int[] individuo,StateObservationMulti stateObs){

        return 0;
    }

}
