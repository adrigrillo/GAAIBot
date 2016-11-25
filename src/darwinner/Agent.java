package darwinner;

import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.time.Clock;
import java.util.ArrayList;

/**
 * Created by adria on 14/11/2016.
 */
public class Agent extends AbstractMultiPlayer {

    public ArrayList<ontology.Types.ACTIONS> acciones;

    /**
     * Metodo que es el constructor del agente, aqui debemos inicializar la poblacion
     * @param stateObs Estado de observacion en el turno actual
     * @param elapsedTimer Temporizador para la creacion del agente
     * @param playerID Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        // Array de acciones
        acciones = stateObs.getAvailableActions(playerID);
        int vida = stateObs.getAvatarHealthPoints();

        //vida
        System.out.println("Player "+playerID + " Vida: "+vida);

        for(int i=0;i<acciones.size();i++){
            System.out.println(acciones.get(i).toString());
        }
    }

    /**
     * Elige una accion. Esta funcion es la que se llama en todos los turnos de la partida,
     * es la que deberia hacer el tournament y coger le mejor individuo
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer){

        int accion_aleatoria = (int)(Math.random()*5);
        System.out.println("Accion: "+accion_aleatoria + " | Puntuacion: "+stateObs.getGameScore());
        return acciones.get(accion_aleatoria);
    }

}
