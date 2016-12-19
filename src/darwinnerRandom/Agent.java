package darwinnerRandom;

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

import core.game.StateObservationMulti;
import core.player.AbstractMultiPlayer;
import tools.ElapsedCpuTimer;
import ontology.Types;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class Agent extends AbstractMultiPlayer {
    
    int posiblesMovimientos = 0;
    final int tamPoblacion = 6;
    final int muSize = 2;
    final int lamSize = tamPoblacion - muSize;
    final int profundidad = 30;
    int playerID;
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
        public Individuo_Antecesor(StateObservationMulti individuo, int antecesor) {
            estado = individuo;
            indAntecesor = antecesor;
        }
        public int indAntecesor;
        public StateObservationMulti estado;
    }

    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){
        // Dependiendo del juego se tiene un numero de acciones disponibles
        posiblesMovimientos = stateObs.getAvailableActions().size();
        this.playerID = playerID;
    }

    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
        // Hay juegos en los que cuando vas a ganar deja de mostrar acciones, esto evita el error
        if (stateObs.getAvailableActions().size() == 0)
            return null;
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
                // Aqui podemos elegir la heuristica que queremos usar
                /*puntuacionEstado.add(new puntuacionIndividuo(i, HeuristicaSample.stateEval(poblacion.get(i).estado,
                        this.playerID)));*/
                puntuacionEstado.add(new puntuacionIndividuo(i, HeuristicaAvara.stateEval(poblacion.get(i).estado,
                        this.playerID)));
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
                Individuo_Antecesor nuevoIndividuo = new Individuo_Antecesor(poblacion.get(puntuacionEstado.get(0).indIndividuo).estado,
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
}
