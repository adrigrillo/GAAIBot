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

package darwinnerAntColony;

import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.time.Clock;
import java.util.ArrayList;


public class Agent extends AbstractMultiPlayer {

    public ArrayList<ontology.Types.ACTIONS> acciones;

    /**
     * Constructor del agente
     * @param stateObs: Estado de observacion en el turno actual
     * @param elapsedTimer: Temporizador para la creacion del agente
     * @param playerID: Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        /*
         * Par�metros de la Colonia de Hormigas
         */

    	/*
    	 * Variables �tiles
    	 */
    	// ArrayList de acciones
        acciones = stateObs.getAvailableActions(playerID);
        // Vida del agente
        int vida = stateObs.getAvatarHealthPoints();
        
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
         *  Generaci�n del grafo
         */
        
        /*
         * Exploraci�n de las hormigas (Trasladar al m�todo que haga falta)
         */
    
        /*
         * Evaluaci�n de caminos de las hormigas (Trasladar al m�todo que haga falta)
         */
        
        /*
         * Potenciar caminos de las hormigas (Trasladar al m�todo que haga falta)
         */
    }

    /**
     * Elecci�n de la acci�n a realizar
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer){


        return null;
    }

    /**
     * Evaluaci�n del agente: evaluaci�n de los caminos de las hormigas
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public int evaluacion(int[] individuo,StateObservationMulti stateObs){

        return 0;
    }

}
