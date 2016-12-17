package darwinnerEE1;

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
    // evaluate a specific state based on some heuristics
    // the current heuristics: value victory and higher score most, and attempt to move towards resources (if they exist)
    // if no resources exist, move towards portals (if they exist)
    // npc's in different games hold different meanings--some you want to get close to, some you don't
    // this works badly in games that do not utilize 'score'
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
