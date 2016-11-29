import java.util.Random;
import core.ArcadeMachine;

public class TestMultiPlayer
{
    public static void main(String[] args) {
        //Available controllers:
        String doNothingController = "controllers.multiPlayer.doNothing.Agent";
        String randomController = "controllers.multiPlayer.sampleRandom.Agent";
        String oneStepController = "controllers.multiPlayer.sampleOneStepLookAhead.Agent";
        String sampleMCTSController = "controllers.multiPlayer.sampleMCTS.Agent";
        String sampleOLMCTSController = "controllers.multiPlayer.sampleOLMCTS.Agent";
        String sampleGAController = "controllers.multiPlayer.sampleGA.Agent";
        String humanController = "controllers.multiPlayer.human.Agent";
        String darwinerController = "darwinner.Agent";

        // Controladores: jugador y enemigo
        String controllers = darwinerController + " " + darwinerController;

        //Available games:
        String gamesPath = "examples/2player/";

        //All public games
        String games [] = new String[]{"accelerator", "akkaarrh", "asteroids", "beekeeper", "bombergirl",     // 0-4
                "breedingdragons", "captureflag", "competesokoban", "copsNrobbers", "donkeykong",   // 5-9
                "dragonattack", "drowning", "egghunt", "fatty", "firetruck",                        // 10-14
                "football", "ghostbusters", "gotcha", "isawsanta", "klax",                          // 15-19
                "mimic", "minesweeper", "minions", "oopsbrokeit", "reflection",                     // 20-24
                "rivalry", "romeoNjuliet", "samaritan", "sokoban", "steeplechase",                  // 25-29
                "teamescape", "thebridge", "trainride", "treasuremap", "tron",                      // 30-34
                "upgrade-x", "uphigh", "warzone", "watchout", "wheelme"};                           // 35-39

        //Other settings
        boolean visuals = true;
        int seed = new Random().nextInt();

        //Game and level to play
        // Indice del juego a ejecutar
        int gameIdx = 1;
        // Indice del nivel del juego
        int levelIdx = 0; //level names from 0 to 4 (game_lvlN.txt).
        
        String game = gamesPath + games[gameIdx] + ".txt";
        String level1 = gamesPath + games[gameIdx] + "_lvl" + levelIdx +".txt";

        // Con esto guardamos los resultados en un archivo, mas facil para ver que hace
        String recordActionsFile = "SalidaJuegos/Movimientos_" + games[gameIdx] + "_nivel" + levelIdx + "_" + seed + ".txt"; //where to record the actions executed. null if not to save.

        /*
         * Jugador: humano
         */
        // 1. This starts a game, in a level, played by two humans.
        //ArcadeMachine.playOneGameMulti(game, level1, recordActionsFile, seed);

        /* Jugador: indicado por controllers
         *
         * 2. This plays a game in a level by the controllers. If one of the players is human, change the playerID passed
         * to the runOneGame method to be that of the human player (0 or 1).
         * /
        //ArcadeMachine.runOneGame(game, level1, visuals, controllers, recordActionsFile, seed, 0);

        /*
         * Repeticion de jugadas almacenados
         */
        // 3. This replays a game from an action file previously recorded
        //String readActionsFile = recordActionsFile;
        //ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

        // Esto es para jugar una vez en todos los niveles
//        String level2 = gamesPath + games[gameIdx] + "_lvl" + 1 +".txt";
//        int M = 3;
//        for(int i = 0; i < games.length; i++){
//           	game = gamesPath + games[i] + ".txt";
//          	level1 = gamesPath + games[i] + "_lvl" + levelIdx +".txt";
//          	ArcadeMachine.runGames(game, new String[]{level1}, M, controllers, null);
//        }

        /* ESTE ES EL MAS INTERESANTE
         * - Juega N juegos (Maximo 40 juegos, que son los que hay)
         * - En los primero L niveles (Maximo 5 que son los que hay por juego)
         * - M veces cada uno (Aquí no hay limite, pero tener en cuenta que se hace largo)
         * Si poneis saveActions = true guardara la salida que se produce, se guardan
         * en la carpeta SalidaJuegos
         */
        int N = 2, L = 1, M = 1;
        boolean saveActions = true;
        String[] levels = new String[L];
        String[] actionFiles = new String[L*M];
        for(int i = 0; i < N; ++i)
        {
            int actionIdx = 0;
            game = gamesPath + games[i] + ".txt";
            for(int j = 0; j < L; ++j){
                levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
                if(saveActions)
                    for(int k = 0; k < M; ++k)
                        actionFiles[actionIdx++] = "SalidaJuegos/Movimientos_game" + i + "_nivel_" + j + "_partida_" + k + ".txt";
            }
            ArcadeMachine.runGames(game, levels, M, controllers, saveActions? actionFiles:null);
        }

        //6. This plays a round robin style tournament between multiple controllers, in N games, first L levels, M times each.
        // Controllers are swapped for each match as well. Actions to file optional (set saveActions to true).
//        int N = 20, L = 5, M = 2;
//        boolean saveActions = false;
//        String[] levels = new String[L];
//        String[] actionFiles = new String[L*M];
//
//        String[] cont = new String[]{doNothingController, randomController, oneStepController, sampleGAController,
//                sampleMCTSController, sampleOLMCTSController}; //add all controllers that should play in this array
//
//        for(int i = 0; i < N; ++i)
//        {
//            int actionIdx = 0;
//            game = gamesPath + games[i] + ".txt";
//            for (int k = 0; k < cont.length - 1; k++) {
//                for (int t = k + 1; t < cont.length; t++) {
//                    // set action files for the first controller order
//                    for(int j = 0; j < L; ++j){
//                        levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
//                        if(saveActions) for(int p = 0; p < M; ++p)
//                            actionFiles[actionIdx++] = "actions_" + cont[k] + "_" + cont[t] + "_game_" + i + "_level_" + j + "_" + p + ".txt";
//                    }
//                    controllers = cont[k] + " " + cont[t];
//                    System.out.println(controllers);
//                    ArcadeMachine.runGames(game, levels, M, controllers, saveActions ? actionFiles : null);
//
//                    // reset action files for the swapped controllers
//                    if (saveActions) {
//                        actionIdx = 0;
//                        for (int j = 0; j < L; ++j) {
//                            for (int p = 0; p < M; ++p)
//                                actionFiles[actionIdx++] = "actions_" + cont[t] + "_" + cont[k] + "_game_" + i + "_level_" + j + "_" + p + ".txt";
//                        }
//                    }
//                    controllers = cont[t] + " " + cont[k];
//                    System.out.println(controllers);
//                    ArcadeMachine.runGames(game, levels, M, controllers, saveActions ? actionFiles : null);
//                }
//            }
//        }
    }
}
