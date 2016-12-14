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
package darwinnerTree;

// basic imports to allow the controller to work
import core.game.Observation;
import core.game.StateObservation;
import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import tools.ElapsedCpuTimer;
// import needed for getting Types.ACTIONS and Types.WINNER
import ontology.Types;
// import needed for dealing with locations on the grid
import tools.Vector2d;
// import needed for dealing with some java functionality
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
// import needed for random number generation
import java.util.Random;

public class Agent extends AbstractMultiPlayer {
    
    int posiblesMovimientos = 0;
    final int tamPoblacion = 6;
    final int muSize = 2;
    final int lamSize = tamPoblacion - muSize;
    final int profundidad = 30;
    Random alt = new Random();

    public class StateTuple implements Comparable<StateTuple> {
        public StateTuple(int x, double y) {
            stateno = x;
            statescore = y;
        }
        public int compareTo(StateTuple another) {
            return (this.statescore > another.statescore) ? 1 : -1;
        }
        public int stateno;
        public double statescore;
    }

    public class StateAndAncestor {
        public StateAndAncestor(StateObservation me, int ances) {
            myState = me;
            ancesNo = ances;
        }
        public int ancesNo;
        public StateObservation myState;
    }

    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){
        // Dependiendo del juego se tiene un numero de acciones disponibles
        posiblesMovimientos = stateObs.getAvailableActions().size();
    }

    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
        // Creamos una poblacion con el estado actual
        ArrayList<StateAndAncestor> population = new ArrayList<StateAndAncestor>();
        StateObservation actual = stateObs.copy();
        for (int i = 0; i < tamPoblacion; i++){
            population.add(new StateAndAncestor(actual, i));
        }

        /* Realizamos un movimiento aleatorio para cada individuo de la poblacion
         * y guardamos el primer movimiento para aplicarlo en la partida */
        ArrayList<Types.ACTIONS> movimientos = new ArrayList<Types.ACTIONS>();
        for (int i = 0; i < tamPoblacion; i++) {
            Types.ACTIONS movimiento = stateObs.getAvailableActions().get(alt.nextInt(posiblesMovimientos));
            movimientos.add(movimiento);
            population.get(i).myState.advance(movimiento);
        }

        int nGeneracion = 1;
        int mejorIndividuo = 0;
        double tiempoRestante = elapsedTimer.remainingTimeMillis();
        ArrayList<StateTuple> puntuacionEstado = new ArrayList<StateTuple>();
        // Empezamos a explorar en profundidad hasta que nos acabemos sin tiempo o lleguemos al maximo
        while ( nGeneracion < profundidad && tiempoRestante > 5.0 ) {
            puntuacionEstado.clear();
            // Evaluamos cada individuo
            for (int i = 0; i < tamPoblacion; i++) {
                puntuacionEstado.add(new StateTuple(i, stateEval(population.get(i).myState)));
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }
            if (elapsedTimer.remainingTimeMillis() < 3.0) break;
            // Ordenamos las puntuaciones por valor
            Collections.sort(puntuacionEstado, Collections.reverseOrder());
            // Cogemos a los mu
            ArrayList<StateTuple> toSortScores = new ArrayList<StateTuple>();


            for (StateTuple p : puntuacionEstado)
                toSortScores.add(new StateTuple(p.stateno, p.statescore));

            // this will sort them in ascending order of their score
            Collections.sort(toSortScores);

            int sortedsize = toSortScores.size();
            // the best actor is the ancestor of who is sorted the highest -- just keep track of index of who it is!
            mejorIndividuo = population.get(toSortScores.get(sortedsize-1).stateno).ancesNo;
            tiempoRestante = elapsedTimer.remainingTimeMillis();
            if (tiempoRestante < 3.0) break;
            // go through the lowest 'lambda' inviduals and replace them in the original population
            // note: we will only copy over the *best* state; this may limit diversity
            //System.out.println("-------");
            for (int i = 0; i < lamSize; i++) {

                // get indexes of the current individual (to replace) and index of the best individual
                int indexOfReplacedIndividual = toSortScores.get(i).stateno;
                int indexOfBestIndividual = toSortScores.get(tamPoblacion-1).stateno;

                // copy state of the best individual, and descend it to our new individual (reproduction)
                // remember to retain memory of the best ancestor of this individual as well
                StateObservation stateOfBestIndividual = population.get(indexOfBestIndividual).myState.copy();
                int ancestorOfBestIndividual = population.get(indexOfBestIndividual).ancesNo;
                StateAndAncestor newIndividual = new StateAndAncestor(stateOfBestIndividual, ancestorOfBestIndividual);

                population.set( indexOfReplacedIndividual, newIndividual );
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }

            tiempoRestante = elapsedTimer.remainingTimeMillis();
            if (tiempoRestante < 3.0) break;

            // now that everything is in the 'best' next state, we will generate another set of random actions to perform
            // we will perform a random action for each copied state (individual) to progress the tree search
            for (int i = 0; i < tamPoblacion; i++) {
                int numMoves = population.get(i).myState.getAvailableActions().size();
                // this will happen if one of the individuals has died and no move remains
                if (numMoves > 0) {
                    int moveSelect = alt.nextInt(numMoves);
                    population.get(i).myState.advance( population.get(i).myState.getAvailableActions().get( moveSelect ) );
                    //System.out.println("i: " + i + " new move: " + moveSelect);
                }
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }
            nGeneracion++;
        }

        // Return the 'next action' of the best individual in the population
        Types.ACTIONS finalAction = movimientos.get(mejorIndividuo);

        //System.out.println("Best actor: " + mejorIndividuo);
        //System.out.println(elapsedTimer.remainingTimeMillis()); // if this is 0, then we are out of time

        //System.out.println("-------");
        //System.out.println("-------");

        return finalAction;
    }

    // evaluate a specific state based on some heuristics
    // the current heuristics: value victory and higher score most, and attempt to move towards resources (if they exist)
    // if no resources exist, move towards portals (if they exist)
    // npc's in different games hold different meanings--some you want to get close to, some you don't
    // this works badly in games that do not utilize 'score'
    public double stateEval ( StateObservation someState ) {

        double stateVal = 0;

        double score = someState.getGameScore();
        Vector2d myPosition = someState.getAvatarPosition();
        ArrayList<Observation>[] npcPositions = someState.getNPCPositions(myPosition);
        ArrayList<Observation>[] portalPositions = someState.getPortalsPositions(myPosition);
        ArrayList<Observation>[] resourcesPositions = someState.getResourcesPositions(myPosition);

        if (someState.getGameWinner() == Types.WINNER.PLAYER_WINS) { return 999999999; }
        if (someState.getGameWinner() == Types.WINNER.PLAYER_LOSES) { return -99999999; }

        // better value for higher scores
        stateVal += score * 100;

        // better value if closer to closest resource of each type
        // but even better if less resources (means we picked it up)
        int noResources = 0;
        if (resourcesPositions != null) {
            for (int i = 0; i < resourcesPositions.length; i++) {
                noResources += resourcesPositions[i].size();
                if (resourcesPositions[i].size() > 0) {
                    Vector2d closestResPos = resourcesPositions[i].get(0).position;
                    double distToResource = myPosition.dist(closestResPos);
                    // the farther away it is, the worst the stateVal will be
                    stateVal -= distToResource*5;
                }
            }
        }
        stateVal -= noResources * 100;

        // better value if closer to closest portal of each type
        // what if there is a wall between us and the portal? --> in 'zelda' this is why we die
        if (portalPositions != null) {
            for (int i = 0; i < portalPositions.length; i++) {
                if (portalPositions[i].size() > 0) {
                    Vector2d closestPorPos = portalPositions[i].get(0).position;
                    double distToPortal = myPosition.dist(closestPorPos);
                    // the farther away it is, the worst the stateVal will be
                    stateVal -= distToPortal / 5;
                }
            }
        }

        // better value if less NPCs
        int noNPC = 0;
        if (npcPositions != null) {
            for (int i = 0; i < npcPositions.length; i++) {
                noNPC = npcPositions[i].size();
                if (npcPositions[i].size() > 0) {
                    Vector2d closestNPCPos = npcPositions[i].get(0).position;
                    double distToNPC = myPosition.dist(closestNPCPos);
                    // to be a bit more *aggressive* on the gameplay, we will
                    // make our heuristic move us *closer* to NPCs, regardless of
                    // whether they are harmful or not
                    stateVal -= distToNPC / 80;
                }
                if (npcPositions[i].size() > 1) {
                    Vector2d farthestNPCPos = npcPositions[i].get(npcPositions[i].size()-1).position;
                    double distToFar = myPosition.dist(farthestNPCPos);
                    //stateVal -= distToFar / 50;
                }

            }
        }
        stateVal -= noNPC*300;

        return stateVal;
    }
}
