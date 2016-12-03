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
    ArrayList<int[]> poblacion = new ArrayList<int[]>();

    /**
     * Metodo que es el constructor del agente, aqui debemos inicializar la poblacion
     * @param stateObs Estado de observacion en el turno actual
     * @param elapsedTimer Temporizador para la creacion del agente
     * @param playerID Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        //Variables
        int[] cromosoma = new int[3]; //4 forwards + accion
        int poblacion_size = 5; //Tamaño problacion


        acciones = stateObs.getAvailableActions(playerID); //Array de acciones

        //Generacion de poblacion inicial
        for(int i = 0 ; i < poblacion_size ; i++){
            for(int j = 0 ; j < cromosoma.length ; j++ ){
                cromosoma[j] = (int)(Math.random()*acciones.size());
            }
            //System.out.print(cromosoma[0]+","+cromosoma[1]+","+cromosoma[2]+","+cromosoma[3]+","+cromosoma[4]+"\n");
            poblacion.add(cromosoma);

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

        //tarda demasiado, descalifica al agente.
        ArrayList<int[]> torneo = new ArrayList<int[]>();
        int mejor_fitness = 0;
        int fitness_individuo = 0;
        int ganador = 0;
        int random = 0;
        double mutacion = 0.01;
        int torneo_size = 3;
        int accion_elegida = 0;

        //Algoritmo genetico
        for(int c=0;c<5;c++){
            //Torneo
            for(int p=0;p<poblacion.size();p++){
                for(int t=0;t<torneo_size;t++){
                    random = (int)(Math.random()*poblacion.size());
                    fitness_individuo = evaluacion(poblacion.get(random),stateObs);
                    if(mejor_fitness<fitness_individuo){
                        mejor_fitness = fitness_individuo;
                        ganador = random;
                        System.out.println(mejor_fitness);
                    }
                }
                torneo.add(poblacion.get(ganador));
            }
        }
        //System.out.println((poblacion.get(ganador))[0]);
        poblacion.clear();
        poblacion = (ArrayList)torneo.clone();
        //torneo.clear();
        return acciones.get(accion_elegida);
    }

    public int evaluacion(int[] individuo,StateObservationMulti stateObs){

        // SIN TERMINAR, problemas con los fordwards
        int fitness = 0;
        int score = 0;

        //Estado actual
        ArrayList<StateObservationMulti> estados = new ArrayList<StateObservationMulti>();
        estados.add(stateObs.copy());

        //Estados futuros
        for(int e = 0 ; e < individuo.length ; e++){
            estados.add(estados.get(e).copy());
            estados.get(e).advance(acciones.get(individuo[e]));
        }

        //Ponderacion individuo
        for(int i = 0;i < estados.size(); i++){
            score+=estados.get(i).getGameScore();
        }

        //System.out.println(score);

        return (int)(Math.random()*poblacion.size()); // esto solo es una prueba
    }

}
