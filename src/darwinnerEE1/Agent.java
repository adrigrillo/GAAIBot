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
package darwinnerEE1;

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

    /**
     * Clase utilizada para manipular los individuos en base a puntuacion. Esta clase
     * tiene dos razones:
     * - Poder ordenar la lista por puntuacion
     * - Poder comparar dos individuos en base a su puntuacion
     */
    public class puntuacionIndividuo implements Comparable<puntuacionIndividuo> {
        public puntuacionIndividuo(int x, double y) {
            indIndividuo = x;
            puntuacion = y;
        }
        public int compareTo(puntuacionIndividuo another) {
            return (this.puntuacion > another.puntuacion) ? 1 : -1;
        }
        public int indIndividuo;
        public double puntuacion;
    }

    /**
     * Clase que estara compuesta por el estado que se esta examinando
     * y la posicion de su antecesor, es decir, del movimiento que se puede realizar
     * en el turno actual */
    public class Individuo_Antecesor {
        public Individuo_Antecesor(StateObservation individuo, int antecesor) {
            estado = individuo;
            indAntecesor = antecesor;
        }
        public int indAntecesor;
        public StateObservation estado;
    }

    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){
        // Dependiendo del juego se tiene un numero de acciones disponibles
        posiblesMovimientos = stateObs.getAvailableActions().size();
    }

    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
        // Creamos una poblacion con el estado actual
        ArrayList<Individuo_Antecesor> poblacion = new ArrayList<Individuo_Antecesor>();
        for (int i = 0; i < tamPoblacion; i++)
            poblacion.add(new Individuo_Antecesor(stateObs.copy(), i));
        /* Realizamos un movimiento aleatorio para cada individuo de la poblacion
         * y guardamos el primer movimiento para aplicarlo en la partida */
        ArrayList<Types.ACTIONS> movimientos = new ArrayList<Types.ACTIONS>();
        for (int i = 0; i < tamPoblacion; i++) {
            int movimiento = alt.nextInt(posiblesMovimientos);
            movimientos.add(stateObs.getAvailableActions().get(movimiento));
            poblacion.get(i).estado.advance(stateObs.getAvailableActions().get(movimiento));
        }
        int nGeneracion = 1;
        int mejorIndividuo = 0;
        double tiempoRestante = elapsedTimer.remainingTimeMillis();
        ArrayList<puntuacionIndividuo> puntuacionEstado = new ArrayList<puntuacionIndividuo>();
        // Empezamos a explorar en profundidad hasta que nos acabemos sin tiempo o lleguemos al maximo
        while ( nGeneracion < profundidad && tiempoRestante > 5.0 ) {
            puntuacionEstado.clear();
            // Evaluamos cada individuo
            for (int i = 0; i < tamPoblacion; i++) {
                puntuacionEstado.add(new puntuacionIndividuo(i, stateEval(poblacion.get(i).estado)));
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }
            // Ordenamos las puntuaciones por valor y cogemos el mejor individuo
            Collections.sort(puntuacionEstado, Collections.reverseOrder());
            mejorIndividuo = poblacion.get(puntuacionEstado.get(0).indIndividuo).indAntecesor;
            tiempoRestante = elapsedTimer.remainingTimeMillis();
            if (tiempoRestante < 3.0) break;
            // Sacamos los hijos del mejor individuo y los meteremos en
            // la poblacion
            for (int i = 0; i < lamSize; i++){
                // Creamos el individuo con el estado del padre
                Individuo_Antecesor nuevoIndividuo = new Individuo_Antecesor(poblacion.get(mejorIndividuo).estado,
                        mejorIndividuo);
                // Elegimos una accion sucesora al azar
                nuevoIndividuo.estado.advance(stateObs.getAvailableActions().get(alt.nextInt(posiblesMovimientos)));
                // Lo añadimos a la poblacion
                poblacion.add(nuevoIndividuo);
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }
            // Eliminamos los lambda peores individuos de la poblacion original
            ArrayList<Individuo_Antecesor> eliminar = new ArrayList<Individuo_Antecesor>();
            for (int i = muSize; i < tamPoblacion; i++)
                eliminar.add(poblacion.get(puntuacionEstado.get(i).indIndividuo));
            for (int i = 0; i < lamSize; i++)
                poblacion.remove(eliminar.get(i));
            nGeneracion++;
        }
        Types.ACTIONS finalAction = movimientos.get(mejorIndividuo);
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
