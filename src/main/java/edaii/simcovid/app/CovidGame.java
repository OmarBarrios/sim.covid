package edaii.simcovid.app;

import edaii.simcovid.entities.Person;
import edaii.simcovid.game.CovidLogic;
import edaii.simcovid.game.VirusParameters;
import edaii.simcovid.ui.CovidGameWindow;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class CovidGame {

    public static final int ROWS = 50;
    public static final int COLUMNS = 50;
    public static final int MSECONDS_PER_DAY = 300;
    public static final int VIRUS_TRANSMISSION_PERCENT = 5;               // percentage of transmitting the virus
    public static final int VIRUS_TIMELIFE_DAYS = 250;                    // number of iterations
    public static final int DAYS_TO_BE_INMUNE = 30;                       // number of days that have to pass to become immune
    public static final double PROBABILITY_OF_SUCCESS_INFECTED = 0.5;     // 0.0 to 0.9
    public static final double PROBABILITY_OF_SUCCESS_DEAD = 0.01;        // 0.00 to 0.09
    public static final double PROBABILITY_OF_SUCCESS_IMMUNE = 0.02;      // 0.00 to 0.09
    public static final double PROBABILITY_OF_SUCCESS_WITH_MASC = 0.01;   // 0.00 to 0.09
    public static final double PROBABILITY_OF_SUCCESS_SURROUNDED = 0.5;   // 0.0 to 0.9

    public static void main(String[] args) throws InterruptedException {

        final CovidGameWindow game = new CovidGameWindow();
        game.setRowsAndColumns(ROWS, COLUMNS);

        final VirusParameters virusParameters = new VirusParameters(
                VIRUS_TRANSMISSION_PERCENT,
                VIRUS_TIMELIFE_DAYS,
                DAYS_TO_BE_INMUNE,
                PROBABILITY_OF_SUCCESS_INFECTED,
                PROBABILITY_OF_SUCCESS_DEAD,
                PROBABILITY_OF_SUCCESS_IMMUNE,
                PROBABILITY_OF_SUCCESS_WITH_MASC,
                PROBABILITY_OF_SUCCESS_SURROUNDED
        );

        final CovidLogic covidLogic = new CovidLogic(virusParameters);

        // Inicializa la población
        List<List<Person>> population = initializePopulation(ROWS, COLUMNS);

        do {
            // Avanza el día
            population = covidLogic.advanceDay(population);

            // Convierte la población en una lista de enteros
            // representando estados validos para edaii.simcovid.ui.Cell
            List<List<Person>> finalPopulation = population;

            List<Integer> cellStates = Stream.iterate(0, y -> y < finalPopulation.size(), y -> y+1)
                    .map(y -> Stream.iterate(0, x -> x < finalPopulation.get(y).size(), x -> x+1)
                            .map(x -> finalPopulation.get(y).get(x).getState())
                    ).flatMap(row -> row).toList();

            // Repesenta el estado
            game.setCellStates(cellStates);

            // Resumen del dia
            final List<Integer> totalHealthy = cellStates.stream()
                    .filter(element -> Objects.equals(element, 0)).toList();
            final List<Integer> totalPatients = cellStates.stream()
                    .filter(element -> Objects.equals(element, 1)).toList();
            final List<Integer> totalImmune = cellStates.stream()
                    .filter(element -> Objects.equals(element, 2)).toList();
            final List<Integer> totalSurrounded = cellStates.stream()
                    .filter(element -> Objects.equals(element, 3)).toList();
            final List<Integer> totalMasc = cellStates.stream()
                    .filter(element -> Objects.equals(element, 4)).toList();
            final List<Integer> totalDead = cellStates.stream()
                    .filter(element -> Objects.equals(element, 5)).toList();

            // Pasa el día
            Thread.sleep(MSECONDS_PER_DAY);
            System.out.println("______________________________________________________________________________________");
            System.out.println("Total:  [" + cellStates.size() + "] Dias: [" +finalPopulation.get(0).get(0).getDays() + "]");
            System.out.println("Sanos: [" + totalHealthy.size() +
                    "] Enfermos: [" + totalPatients.size() +
                    "] Inmunes: [" + totalImmune.size() +
                    "] Muertos: [" + totalDead.size() +
                    "] Mascarillas: [" + totalMasc.size() +
                    "] Rodeados: [" + totalSurrounded.size() + "]");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            if ((totalPatients.size() == 0 && totalSurrounded.size() == 0) || (finalPopulation.get(0).get(0).getDays() == VIRUS_TIMELIFE_DAYS)) {
                break;
            }
        } while (true);

    }

    private static List<List<Person>> initializePopulation(int rows, int columns) {
        return Stream.iterate(0, i -> i < rows, i -> i + 1)
                .map(i ->
                        Stream.iterate(0, j -> j < columns, j -> j + 1)
                                .map(j -> ((j == columns / 2) && (i == rows / 4)) || ((j == columns / 4) && (i == rows / 2)))
                                .map(infected -> infected ? Person.infected() : Person.notInfected()).toList()
                ).toList();
    }

}
