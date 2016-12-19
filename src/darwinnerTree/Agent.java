package darwinnerTree;

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
//import darwinnerTree.HeuristicaAvara;
//import darwinnerTree.HeuristicaSample;
import ontology.Types;
import tools.ElapsedCpuTimer;
import java.util.ArrayList;
import java.util.Collections;

public class Agent extends AbstractMultiPlayer{
    private ArrayList<Integer> posiblesMovimientos = new ArrayList<Integer>();
    private int nPosiblesMovimientos;
    private final int tamPoblacion = 6;
    private final int muSize = 2;
    private final int lamSize = tamPoblacion - muSize;
    private final int profundidad = 30;
    private int playerID;
    private int indice = 0;

    /**
     * Clase que estara compuesta por el estado que se esta examinando
     * y la posicion de su antecesor, es decir, del movimiento que se puede realizar
     * en el turno actual */
    public class Individuo_Antecesor implements Comparable<Individuo_Antecesor>{
        public Individuo_Antecesor(StateObservationMulti individuo, int antecesor, double puntuacion) {
            estado = individuo;
            indAntecesor = antecesor;
            puntuacion = puntuacion;
        }
        public int indAntecesor;
        public StateObservationMulti estado;
        public double puntuacion;
        public int compareTo(Individuo_Antecesor another){
            return (this.puntuacion > another.puntuacion) ? 1 : -1;
        }

    }
    public Agent(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer, int playerID){
        // Dependiendo del juego se tiene un numero de acciones disponibles
        nPosiblesMovimientos = stateObs.getAvailableActions().size();
        for (int i = 0; i < nPosiblesMovimientos; i++)
            posiblesMovimientos.add(i);
        this.playerID = playerID;
        this.indice = 0;
    }

    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
        // Hay juegos en los que cuando vas a ganar deja de mostrar acciones, esto evita el error
        if (stateObs.getAvailableActions().size() == 0)
            return null;
        // Creamos una poblacion con el estado actual
        ArrayList<Individuo_Antecesor> poblacion = new ArrayList<Individuo_Antecesor>();
        for (int i = 0; i < tamPoblacion; i++)
            poblacion.add(new Individuo_Antecesor(stateObs.copy(), i, 0));
        /* La poblacion inicial esta compuesta por el conjunto de individuos formado
         * por los posibles movimientos del juego, es decir, habra un individuo por
         * cada movimiento */
        ArrayList<Types.ACTIONS> movimientos = new ArrayList<Types.ACTIONS>();
        Collections.shuffle(posiblesMovimientos);
        int x = 0;
        for (int i = 0; i < tamPoblacion; i++){
            movimientos.add(stateObs.getAvailableActions().get(posiblesMovimientos.get(x)));
            poblacion.get(x).estado.advance(stateObs.getAvailableActions().get(posiblesMovimientos.get(x)));
            if (++x == nPosiblesMovimientos)
                x = 0;
        }
        int nGeneracion = 1;
        int mejorIndividuo = 0;
        double tiempoRestante = elapsedTimer.remainingTimeMillis();
        // Empezamos a explorar en profundidad hasta que nos acabemos sin tiempo o lleguemos al maximo
        while ( nGeneracion < profundidad && tiempoRestante > 5.0 ) {
            // Evaluamos cada individuo
            for (int i = 0; i < tamPoblacion; i++) {
                double puntuacion = HeuristicaSample.stateEval(poblacion.get(i).estado, this.playerID);
                poblacion.get(i).puntuacion += puntuacion;
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0) break;
            }
            // Ordenamos las puntuaciones por valor y cogemos el mejor individuo
            Collections.sort(poblacion, Collections.reverseOrder());
            mejorIndividuo = poblacion.get(indice=0).indAntecesor;
            tiempoRestante = elapsedTimer.remainingTimeMillis();
            if (tiempoRestante < 3.0) break;
            // Sacamos los hijos de los mejores individuos y los meteremos en la poblacion
            int j = 0;
            Collections.shuffle(posiblesMovimientos);
            while (poblacion.size() < tamPoblacion + lamSize){
                Individuo_Antecesor mejor = poblacion.get(0);
                Individuo_Antecesor nuevoIndividuo = new Individuo_Antecesor(mejor.estado, mejorIndividuo, mejor
                        .puntuacion);
                nuevoIndividuo.estado.advance(stateObs.getAvailableActions().get(posiblesMovimientos.get(0)));
                tiempoRestante = elapsedTimer.remainingTimeMillis();
                if (tiempoRestante < 3.0)
                    break;
                j++;
                if (j == posiblesMovimientos.size()) {
                    j = 0;
                    indice++;
                }
            }
            // Eliminamos los lambda peores individuos de la poblacion original
            ArrayList<Individuo_Antecesor> eliminar = new ArrayList<Individuo_Antecesor>();
            for (int i = muSize; i < tamPoblacion; i++)
                eliminar.add(poblacion.get(i));
            for (int i = 0; i < lamSize; i++)
                poblacion.remove(eliminar.get(i));
            nGeneracion++;
        }
        Types.ACTIONS finalAction = movimientos.get(mejorIndividuo);
        return finalAction;
    }
}
