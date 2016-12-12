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

package darwinnerSample;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import controllers.multiPlayer.heuristics.StateHeuristicMulti;
import controllers.multiPlayer.heuristics.WinScoreHeuristic;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;

public class Agent extends AbstractMultiPlayer{

	private static double GAMMA = 0.90;
    private static long BREAK_MS = 5;
    private static int SIMULATION_DEPTH = 7;
    private static int POPULATION_SIZE = 5;

    private static double RECPROB = 0.1;
    private double MUT = (1.0 / SIMULATION_DEPTH);
    private final int[] N_ACTIONS;

    private ElapsedCpuTimer timer;

    private int genome[][][][];
    private final HashMap<Integer, Types.ACTIONS>[] action_mapping;
    private final HashMap<Types.ACTIONS, Integer>[] r_action_mapping;
    protected Random randomGenerator;

    private int numSimulations;

    private int id, oppID, no_players;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID) {
        id = playerID;
        oppID = (id + 1) % stateObs.getNoPlayers();
        // Saca el numero de jugadores en la partida
        no_players = stateObs.getNoPlayers();

        randomGenerator = new Random();
        // Este array guarda el numero de acciones disponibles para cada jugador
        N_ACTIONS = new int[no_players];

        // Este bucle lo que calcula es el numero de acciones disponibles para cada jugador
        action_mapping = new HashMap[no_players];
        r_action_mapping = new HashMap[no_players];
        for (int j = 0; j < no_players; j++) {
            action_mapping[j] = new HashMap<>();
            r_action_mapping[j] = new HashMap<>();
            int i = 0;
            for (Types.ACTIONS action : stateObs.getAvailableActions(j)) {
                action_mapping[j].put(i, action);
                r_action_mapping[j].put(action, i);
                i++;
            }

            N_ACTIONS[j] = stateObs.getAvailableActions(j).size();
        }
        // Iniciamos el individuo al azar
        initGenome(stateObs);
    }


    double microbial_tournament(int[][] actionGenome, StateObservationMulti stateObs, StateHeuristicMulti heuristic, int playerID) throws TimeoutException {
        int a, b, c, W, L;
        int i;


        a = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        do {
            b = (int) ((POPULATION_SIZE - 1) * randomGenerator.nextDouble());
        } while (a == b);

        double score_a = simulate(stateObs, heuristic, actionGenome[a]);
        double score_b = simulate(stateObs, heuristic, actionGenome[b]);

        if (score_a > score_b) {
            W = a;
            L = b;
        } else {
            W = b;
            L = a;
        }

        int LEN = actionGenome[0].length;

        for (i = 0; i < LEN; i++) {
            if (randomGenerator.nextDouble() < RECPROB) {
                actionGenome[L][i] = actionGenome[W][i];
            }
        }

        for (i = 0; i < LEN; i++) {
            if (randomGenerator.nextDouble() < MUT) actionGenome[L][i] = randomGenerator.nextInt(N_ACTIONS[playerID]);
        }

        return Math.max(score_a, score_b);

    }

    private void initGenome(StateObservationMulti stateObs) {
        // Saca el numero maximo de acciones comparando cada jugador
        int max = 0;
        for (int i = 0; i < stateObs.getNoPlayers(); i++) {
            if (N_ACTIONS[i] > max) max = N_ACTIONS[i];
        }
        /*
            genoma[1][2][3][4]:
            1. Crea las poblaciones para cada jugador, en este caso 2
                2. Para cada jugador, hay una lista de N posibles acciones.
                    3. Por cada acción posible hay una población de M individuos (Tamaño de población)
                        4. Se crea una lista de X acciones para cada individuo de las poblaciones: profundidad de la simulación
         */
        genome = new int[stateObs.getNoPlayers()][max][POPULATION_SIZE][SIMULATION_DEPTH];


        // Randomize initial genome
        for (int i = 0; i < genome.length; i++) {
            for (int j = 0; j < genome[i].length; j++) {
                for (int k = 0; k < genome[i][j].length; k++) {
                    for (int m = 0; m < genome[i][j][k].length; m++) {
                        genome[i][j][k][m] = randomGenerator.nextInt(N_ACTIONS[i]);
                    }
                }
            }
        }
    }


    private double simulate(StateObservationMulti stateObs, StateHeuristicMulti heuristic, int[] policy) throws TimeoutException {

    	// esta comprobacion ha dicho que la podemos cambiar de sitio para mejorar
    	long remaining = timer.remainingTimeMillis();
        if (remaining < BREAK_MS) {
            throw new TimeoutException("Timeout");
        }


        int depth = 0;
        stateObs = stateObs.copy();
        for (; depth < policy.length; depth++) {
            Types.ACTIONS[] acts = new Types.ACTIONS[stateObs.getNoPlayers()];
            for (int i = 0; i < stateObs.getNoPlayers(); i++) {
                acts[i] = action_mapping[i].get(policy[depth]);
                if(acts[i] == null)
                    acts[i] = action_mapping[1-i].get(policy[depth]);
            }

            stateObs.advance(acts);

            if (stateObs.isGameOver()) {
                break;
            }

        }

        numSimulations++;
        double score = Math.pow(GAMMA, depth) * heuristic.evaluateState(stateObs, id);
        return score;


    }

    private Types.ACTIONS microbial(StateObservationMulti stateObs, int maxdepth, StateHeuristicMulti heuristic, int iterations) {

        double[][] maxScores = new double[no_players][];
        for (int i = 0; i < no_players; i++) {
            maxScores[i] = new double[stateObs.getAvailableActions(i).size()];

            for (int j = 0; j < maxScores[i].length; j++) {
                maxScores[i][j] = Double.NEGATIVE_INFINITY;
            }
        }

        outerloop:
        for (int i = 0; i < iterations; i++) {

            for (Types.ACTIONS action : stateObs.getAvailableActions(id)) {
                for (Types.ACTIONS action2 : stateObs.getAvailableActions(oppID)) {

                    Types.ACTIONS[] acts = new Types.ACTIONS[no_players];
                    acts[id] = action;
                    acts[oppID] = action2;

                    StateObservationMulti stCopy = stateObs.copy();
                    stCopy.advance(acts);

                    double score = 0, scoreOpp = 0;
                    try {
                        score = microbial_tournament(genome[id][r_action_mapping[id].get(acts[id])], stCopy, heuristic, id) + randomGenerator.nextDouble() * 0.00001;
                        scoreOpp = microbial_tournament(genome[oppID][r_action_mapping[oppID].get(acts[oppID])], stCopy, heuristic, oppID) + randomGenerator.nextDouble() * 0.00001;
                    } catch (TimeoutException e) {
                        break outerloop;
                    }

                    try {
                        int int_act = this.r_action_mapping[id].get(acts[id]);
                        int int_act_opp = this.r_action_mapping[oppID].get(acts[oppID]);

                        if (score > maxScores[id][int_act]) {
                            maxScores[id][int_act] = score;
                        }
                        if (scoreOpp > maxScores[oppID][int_act_opp]) {
                            maxScores[oppID][int_act_opp] = scoreOpp;
                        }
                    } catch (Exception e) {}

                }

            }
        }

        Types.ACTIONS maxAction = this.action_mapping[id].get(Utils.argmax(maxScores[id]));


        return maxAction;

    }

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {

        this.timer = elapsedTimer;
        numSimulations = 0;

        Types.ACTIONS lastGoodAction = microbial(stateObs, SIMULATION_DEPTH, new WinScoreHeuristic(stateObs), 100);

        return lastGoodAction;
    }

}
