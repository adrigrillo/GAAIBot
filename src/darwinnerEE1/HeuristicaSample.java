package darwinnerEE1;

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

import core.game.Observation;
import core.game.StateObservationMulti;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

public class HeuristicaSample{
    public static double stateEval(StateObservationMulti stateObs, int playerID) {
        double stateVal = 0;
        Vector2d avatarPosition = stateObs.getAvatarPosition(playerID);
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(avatarPosition);

        int oppID = (playerID + 1) % stateObs.getNoPlayers();
        Types.WINNER[] winners = stateObs.getMultiGameWinner();

        boolean ambosVictoria = (winners[playerID] == Types.WINNER.PLAYER_WINS) && (winners[oppID] == Types.WINNER
                .PLAYER_WINS);
        boolean victoria = (winners[playerID] == Types.WINNER.PLAYER_WINS) && (winners[oppID] == Types.WINNER
                .PLAYER_LOSES);
        boolean derrota = (winners[playerID] == Types.WINNER.PLAYER_LOSES) && (winners[oppID] == Types.WINNER
                .PLAYER_WINS);
        boolean ambosDerrota = (winners[playerID] == Types.WINNER.PLAYER_LOSES) && (winners[oppID] == Types.WINNER
                .PLAYER_LOSES);

        if (victoria || ambosVictoria)
            stateVal = 1000000000;
        else if (derrota)
            return -999999999;
        // Mejor perder los dos que solo uno
        else if (ambosDerrota)
            return -500000000;

        double minDistance = Double.POSITIVE_INFINITY;
        Vector2d minObject = null;
        int minNPC_ID = -1;
        int minNPCType = -1;

        int npcCounter = 0;
        if (npcPositions != null) {
            for (ArrayList<Observation> npcs : npcPositions) {
                if(npcs.size() > 0)
                {
                    minObject   = npcs.get(0).position; //This is the closest guy
                    minDistance = npcs.get(0).sqDist;   //This is the (square) distance to the closest NPC.
                    minNPC_ID   = npcs.get(0).obsID;    //This is the id of the closest NPC.
                    minNPCType  = npcs.get(0).itype;    //This is the type of the closest NPC.
                    npcCounter += npcs.size();
                }
            }
        }

        if (portalPositions == null) {

            double score = 0;
            if (npcCounter == 0) {
                score = stateObs.getGameScore(playerID) + stateVal*100000000;
            } else {
                score = -minDistance / 100.0 + (-npcCounter) * 100.0 + stateObs.getGameScore(playerID) + stateVal*100000000;
            }

            return score;
        }

        double minDistancePortal = Double.POSITIVE_INFINITY;
        Vector2d minObjectPortal = null;
        for (ArrayList<Observation> portals : portalPositions) {
            if(portals.size() > 0)
            {
                minObjectPortal   =  portals.get(0).position; //This is the closest portal
                minDistancePortal =  portals.get(0).sqDist;   //This is the (square) distance to the closest portal
            }
        }

        double score = 0;
        if (minObjectPortal == null) {
            score = stateObs.getGameScore() + stateVal*100000000;
        }
        else {
            score = stateObs.getGameScore() + stateVal*1000000 - minDistancePortal * 10.0;
        }

        return score;
    }
}
