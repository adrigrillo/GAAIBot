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
    ArrayList<int[]> poblacion_0 = new ArrayList<int[]>(); //Jugador 0
    ArrayList<int[]> poblacion_1 = new ArrayList<int[]>(); //Jugador 1
    /**
     * Metodo que es el constructor del agente, aqui debemos inicializar la poblacion
     * @param stateObs Estado de observacion en el turno actual
     * @param elapsedTimer Temporizador para la creacion del agente
     * @param playerID Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        //Variables
        int[] cromosoma = new int[5]; //4 forwards + accion
        int poblacion_size = 100; //Tamaño problacion
        acciones = stateObs.getAvailableActions(playerID); //Array de acciones

        //Generacion de poblacion inicial
        for(int i = 0 ; i < poblacion_size ; i++){
            for(int j = 0 ; j < cromosoma.length ; j++ ){
                cromosoma[j] = (int)(Math.random()*acciones.size());
            }
            System.out.print(cromosoma[0]+","+cromosoma[1]+","+cromosoma[2]+","+cromosoma[3]+","+cromosoma[4]+"\n");
            if(playerID == 0){
                poblacion_0.add(cromosoma);
            }else{
                poblacion_1.add(cromosoma);
            }
        }

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

        // FALTA CRUCE (TORNEOS), MUTACION, EVALUACION (TERMINAR)

        //Prueba de evaluacion
        if(!stateObs.isGameOver()){
            for(int p = 0 ; p < poblacion_0.size() ; p++){
                evaluacion(poblacion_0.get(p),stateObs);
            }
        }

        int accion_aleatoria = (int)(Math.random()*acciones.size());
        return acciones.get(accion_aleatoria);
    }

    public int evaluacion(int[] individuo,StateObservationMulti stateObs){

        // SIN TERMINAR

        // Prueba de Forwads y cambios de estado
        stateObs.advance(acciones.get(individuo[0]));
        StateObservationMulti estado_1 = stateObs.copy();
        stateObs.advance(acciones.get(individuo[1]));
        StateObservationMulti estado_2 = stateObs.copy();
        stateObs.advance(acciones.get(individuo[2]));
        StateObservationMulti estado_3 = stateObs.copy();
        stateObs.advance(acciones.get(individuo[3]));
        StateObservationMulti estado_4 = stateObs.copy();
        stateObs.advance(acciones.get(individuo[4]));

        System.out.println("0: "+stateObs.getGameScore(0)
                +", 1: "+estado_1.getGameScore(0)
                +", 2: "+estado_2.getGameScore(0)
                +", 3: "+estado_3.getGameScore(0)
                +", 4: "+estado_4.getGameScore(0));

        return 0;
    }

}
