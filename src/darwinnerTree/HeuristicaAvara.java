package darwinnerTree;

import core.game.Observation;
import core.game.StateObservationMulti;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;

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

public class HeuristicaAvara {
    /* Heuristica que toma como lo más importante la puntuacion, sufre si no tiene la puntuacion
     * intenta moverse hacia los recursos si es que hay para recogerlos y si no hacia el portal al que haya
     * que ir */
    public static double stateEval (StateObservationMulti stateObs, int playerID){

        double stateVal = 0;

        int oppID = (playerID + 1) % stateObs.getNoPlayers();

        double score = stateObs.getGameScore();
        Vector2d myPosition = stateObs.getAvatarPosition();
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(myPosition);
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(myPosition);
        ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions(myPosition);

        if (stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS) { return 999999999; }
        if (stateObs.getGameWinner() == Types.WINNER.PLAYER_LOSES) { return -99999999; }

        // cuanto mas alto la puntuacion mejor
        stateVal += score * 100;

        // Aumenta el valor cuanto mas cerca de los recursos y cuanto menos haya
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

        // Mejor cuanto mas cerca esta del portal, aunque falla cuando hay una pared en medio
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

        // Cuanto menos npc mejor
        int noNPC = 0;
        if (npcPositions != null) {
            for (int i = 0; i < npcPositions.length; i++) {
                noNPC = npcPositions[i].size();
                if (npcPositions[i].size() > 0) {
                    Vector2d closestNPCPos = npcPositions[i].get(0).position;
                    double distToNPC = myPosition.dist(closestNPCPos);
                    // buscamos que se aleje
                    stateVal -= distToNPC / 80;
                }
            }
        }
        stateVal -= noNPC*300;

        return stateVal;
    }
}
