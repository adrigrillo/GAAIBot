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
package darwinnerGeneticNew;

import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import java.util.Random;
import java.time.Clock;
import java.util.ArrayList;

public class Agent extends AbstractMultiPlayer {

    public ArrayList<ontology.Types.ACTIONS> acciones;
    //ArrayList<int[]> poblacion = new ArrayList<int[]>();
    //<int[]> torneo = new ArrayList<int[]>();


    //Parametros
    private double mutacion = 0.01;
    private int poblacion_size = 6; //Tamaño problacion
    private int genes = 8;
    private  int ciclos = 1;
    private static int HUGE_ENDGAME_SCORE = 1000;
    private static int MAX_TIMESTEPS = 40;

    //Variables
    private String[] poblacion;
    private String[] torneo;
    private String cromosoma = "";
    private int i=0;
    private String hijo1="";
    private String hijo2="";
    private String hijo1_mutado="";
    private String hijo2_mutado="";
    private static int id_jugador;



    /**
     * Metodo que es el constructor del agente, aqui debemos inicializar la poblacion
     * @param stateObs Estado de observacion en el turno actual
     * @param elapsedTimer Temporizador para la creacion del agente
     * @param playerID Id del jugador
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){

        poblacion = new String[poblacion_size];
        torneo = new String[poblacion_size];
        acciones = stateObs.getAvailableActions(playerID); //Array de acciones
        id_jugador = playerID;

        //Generacion de poblacion inicial
        for(int p = 0 ; p < poblacion_size ; p++){
            cromosoma = "";
            for(int g = 0 ; g < genes ; g++ ){
                cromosoma += ((int)(Math.random()*acciones.size()));
            }
            poblacion[p]=cromosoma;
        }

        int vida = stateObs.getAvatarHealthPoints();
        //vida
        System.out.print("Player "+playerID + " Life: "+vida+" | ");
        for(int i=0;i<acciones.size();i++){
            System.out.print(acciones.get(i).toString()+" | ");
        }
        System.out.print("\nStart Game\n");

    }

    /**
     * Elige una accion. Esta funcion es la que se llama en todos los turnos de la partida,
     * es la que deberia hacer el tournament y coger le mejor individuo
     * @param stateObs Estado actual de la partida.
     * @param elapsedTimer Tiempo en el que la accion acaba, limite de tiempo.
     * @return
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer){


        double mejor_fitness = -1;
        double fitness_individuo = -1;
        int ganador = 0;
        int random ;
        long time_start;
        long time_end=0;
        int torneo_size = 2;
        int accion_elegida = 0;
        time_start = System.currentTimeMillis();
        StateObservationMulti estado = stateObs.copy();;
        int c = 0;
        //Algoritmo genetico
        for(c=0;time_end<MAX_TIMESTEPS-12;c++){
            //Torneo
            for(int p=0;p<poblacion.length;p++){
                for(int t=0;t<torneo_size;t++){
                    random = (int)(Math.random()*poblacion.length);
                    fitness_individuo = evaluar(poblacion[random],stateObs);
                    if(mejor_fitness<fitness_individuo){
                        mejor_fitness = fitness_individuo;
                        ganador = random;
                        accion_elegida = ((int)(poblacion[random].charAt(0))-48);

                    }
                    stateObs = estado.copy();
                }
                torneo[p]=poblacion[ganador];
            }

            //Cruce
            i = 0;
            while (i < poblacion.length){
                hijo1 = "";
                hijo2 = "";
                for(int g = 0;g<genes;g++){

                    Random padre_o_madre = new Random();
                    if(padre_o_madre.nextBoolean()){
                        hijo1+=torneo[i].charAt(g);
                        hijo2+=torneo[i+1].charAt(g);
                    }else{
                        hijo1+=torneo[i+1].charAt(g);
                        hijo2+=torneo[i].charAt(g);
                    }
                }

                //Mutacion
                hijo1_mutado = "";
                hijo2_mutado = "";
                for(int j=0;j<genes;j++){
                    double mutacion_hijo1 = Math.random();
                    double mutacion_hijo2 = Math.random();

                    if(mutacion_hijo1<mutacion){
                        hijo1_mutado += ((int)(Math.random()*acciones.size()));
                    }else{
                        hijo1_mutado += hijo1.charAt(j);
                    }

                    if(mutacion_hijo2<mutacion){
                        hijo2_mutado += ((int)(Math.random()*acciones.size()));
                    }else{
                        hijo2_mutado += hijo2.charAt(j);
                    }

                }

                poblacion[i]=hijo1_mutado;
                poblacion[i+1]=hijo2_mutado;
                //System.out.print(poblacion[i]+"|");
                //System.out.print(poblacion[i+1]+"|");
                i+=2;
            }
            time_end = System.currentTimeMillis()-time_start;
        }
        System.out.println("Action: "+accion_elegida+" | Time: "+(time_end)+" | Generations: "+c+" | Fitness: "+mejor_fitness);
        return acciones.get(accion_elegida);
    }

    public double evaluar(String individuo,StateObservationMulti stateObs){

        //StateObservationMulti estado;
        //estado = stateObs.copy();
        double fitness = 0;
        for(int e = 0 ; e < genes ; e++) {
            stateObs.advance(acciones.get((int)(individuo.charAt(e))-48));
            fitness+= evaluacion(stateObs);
        }

        return fitness;
    }

    public static double evaluacion(StateObservationMulti stateObs){
        double scores = 0;

            scores = stateObs.getGameScore();

            if (stateObs.isGameOver()) {
                if (stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_WINS) {
                    scores += HUGE_ENDGAME_SCORE;
                }
                else if (stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_LOSES) {
                    if(stateObs.getGameTick() == MAX_TIMESTEPS){
                        scores -= HUGE_ENDGAME_SCORE * 0.8;
                    }
                    else{
                        scores -= HUGE_ENDGAME_SCORE;
                    }
                }
            }

        if(stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_LOSES &&
                stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_LOSES){
            scores += 0.8 * HUGE_ENDGAME_SCORE;
            scores += 1.0 * HUGE_ENDGAME_SCORE + 1;
        }

        else if(stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_WINS &&
                stateObs.getMultiGameWinner()[id_jugador] == Types.WINNER.PLAYER_WINS){
            scores -= 0.8 * HUGE_ENDGAME_SCORE;
        }

        return scores;
    }

}
