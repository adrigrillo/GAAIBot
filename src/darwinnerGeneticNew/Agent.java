package darwinnerGeneticNew;

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
    //ArrayList<int[]> poblacion = new ArrayList<int[]>();
    //<int[]> torneo = new ArrayList<int[]>();

    //Variables
    private StateObservationMulti[] estados;
    private String[] poblacion;
    private String[] torneo;
    private int poblacion_size = 30; //Tamaño problacion
    private String cromosoma = ""; //4 forwards + accion
    private int genes = 5;
    private int i=0;
    private String hijo1="";
    private String hijo2="";

    /**
     * Metodo que es el constructor del agente, aqui debemos inicializar la poblacion
     * @param stateObs Estado de observacion en el turno actual
     * @param elapsedTimer Temporizador para la creacion del agente
     * @param playerID Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        poblacion = new String[poblacion_size];
        torneo = new String[poblacion_size];
        estados = new StateObservationMulti[genes];
        acciones = stateObs.getAvailableActions(playerID); //Array de acciones

        //Generacion de poblacion inicial
        for(int p = 0 ; p < poblacion_size ; p++){
            cromosoma = "";
            for(int g = 0 ; g < genes ; g++ ){
                cromosoma += ((int)(Math.random()*acciones.size()));
            }
            //System.out.print(cromosoma[0]+","+cromosoma[1]+","+cromosoma[2]+","+cromosoma[3]+","+cromosoma[4]+"\n");
            poblacion[p]=cromosoma;

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


        int mejor_fitness = 0;
        int fitness_individuo = 0;
        int ganador = 0;
        int random = 0;
        double mutacion = 0.01;
        int torneo_size = 4;
        int accion_elegida = 0;

        //Algoritmo genetico
        for(int c=0;c<50;c++){
            //Torneo
            for(int p=0;p<poblacion.length;p++){
                for(int t=0;t<torneo_size;t++){
                    random = (int)(Math.random()*poblacion.length);
                    //System.out.println(poblacion[random]);
                    //fitness_individuo = evaluacion(poblacion[random],stateObs);

                    if(mejor_fitness<fitness_individuo){
                        mejor_fitness = fitness_individuo;
                        ganador = random;

                    }
                }
                torneo[p]=poblacion[ganador];
            }

            //Cruce
            //i = 0;
            /*while (i < poblacion.length){
                hijo1 = "";
                hijo2 = "";
                /*for(int g = 0;g<genes;g++){
                    //random = (int)(Math.random());
                    //System.out.println(random);

                }

                i++;
            }*/




        }
        //System.out.println((poblacion.get(ganador))[0]);

        poblacion = torneo.clone();
        //torneo.clear();
        return acciones.get(accion_elegida);
    }

    public int evaluacion(String individuo,StateObservationMulti stateObs){

        // SIN TERMINAR, problemas con los fordwards
        int fitness = 0;
        int score = 0;

        //Estado actual
        estados[0] = stateObs.copy();
        //Estados futuros
        for(int e = 1 ; e < genes ; e++){
            estados[e]=estados[e-1].copy();
            estados[e-1].advance(acciones.get((int)(individuo.charAt(e))-48));
        }

        //System.out.println();
        //Ponderacion individuo
        for(int i = 0;i < estados.length; i++){
            score+=estados[i].getGameScore();
        }

        //System.out.println(score);

        return (int)(Math.random()*poblacion_size); // esto solo es una prueba
    }

}
