package edaii.simcovid.game;

import edaii.simcovid.entities.Person;
import java.util.List;
import java.util.stream.Stream;

public class CovidLogic {

    private final VirusParameters parameters;

    public CovidLogic(VirusParameters parameters) {
        this.parameters = parameters;
    }

    public List<List<Person>> advanceDay(List<List<Person>> grid) {
        final int probability = (int) (Math.random() * parameters.transmissionPercent +1);
        final int daysToImmune = parameters.daysToBeInmune;
        final double probabilityOfSuccessInfected = parameters.probabilityOfSuccessInfected;
        final double probabilityOfSuccessDead = parameters.probabilityOfSuccessDead;
        final double probabilityOfSuccessImmune = parameters.probabilityOfSuccessImmune;
        final double probabilityOfSuccessWithMasc = parameters.probabilityOfSuccessWithMasc;
        final double probabilityOfSuccessSurrounded = parameters.probabilityOfSuccessSurrounded;

        final List<List<Person>> advanceDayGrid = Stream.iterate(0, y -> y < grid.size(), y -> y + 1)
                .map(y -> Stream.iterate(0, n -> n < grid.get(y).size(), n -> n + 1)
                        .map(f -> updateStateColumnInfected(grid, y, f, probabilityOfSuccessInfected, probability ))
                        .toList())
                .map(y -> updateStateRowInfected(y, probability))
                .toList();

        final List<List<Person>> advanceDayGridAddDead = peopleDead(advanceDayGrid, probability, probabilityOfSuccessDead);
        final List<List<Person>> advanceDayGridAddImmune = peopleImmune(advanceDayGridAddDead, daysToImmune, probabilityOfSuccessImmune);
        final List<List<Person>> advanceDayGridAddMasc = peopleWithMasc(advanceDayGridAddImmune, probability, probabilityOfSuccessWithMasc);
        final List<List<Person>> advanceDayGridAddSurrounded = peopleSurrounded(advanceDayGridAddMasc, probabilityOfSuccessSurrounded);

        //==============================================================================================
        return Stream.iterate(0, y -> y < advanceDayGridAddSurrounded.size(), y -> y + 1) // Advance day
                .map(y -> Stream.iterate(0, n -> n < advanceDayGridAddSurrounded.get(y).size(), n -> n + 1)
                        .map(person -> new Person(advanceDayGridAddSurrounded.get(y).get(person)))
                        .toList()
                ).toList();

    }

    private static List<List<Person>> peopleDead(List<List<Person>> grid, int probability, double probabilityOfSuccessDead) {
        return Stream.iterate(0, y -> y < grid.size(), y -> y + 1)
                .map(y -> Stream.iterate(0, n -> n < grid.get(y).size(), n -> n + 1)
                        .map(f -> {
                            if (grid.lastIndexOf(grid.get(y)) < grid.size() - 1 && grid.lastIndexOf(grid.get(y)) > 0) {
                                if ((grid.get(y).get(f).getState() == 1 || grid.get(y).get(f).getState() == 3) && (Math.random() < probabilityOfSuccessDead && probability == 1)) { // people dead
                                    return new Person(5, grid.get(y).get(f).getDays());
                                }
                            }
                            return grid.get(y).get(f);
                        }).toList())
                .map(listGrid -> Stream
                        .iterate(0, y -> y < listGrid.size(), y -> y + 1)
                        .limit(listGrid.size())
                        .map(i -> {
                            if (listGrid.lastIndexOf(listGrid.get(i)) < listGrid.size() - 1 && listGrid.lastIndexOf(listGrid.get(i)) > 0) {
                                if ((listGrid.get(i).getState() == 1 || listGrid.get(i).getState() == 3) && (Math.random() < probabilityOfSuccessDead && probability == 1)) { // people dead
                                    return new Person(5, listGrid.get(i).getDays());
                                }
                            }
                            return listGrid.get(i);
                        }).toList()).toList();
    }
    private static List<List<Person>> peopleImmune(List<List<Person>> grid, int daysToInmune, double probabilityOfSuccessImmune) {
        return Stream.iterate(0, y -> y < grid.size(), y -> y + 1)
                .map(y -> Stream.iterate(0, n -> n < grid.get(y).size(), n -> n + 1)
                        .map(f -> {
                            if ((grid.get(y).get(f).getState() == 1 || grid.get(y).get(f).getState() == 3) && (Math.random() < probabilityOfSuccessImmune) ) { // people inmune
                                return new Person(2, grid.get(y).get(f).getDays());
                            }
                            else if (grid.get(0).get(f).getState() == 1 && (Math.random() < probabilityOfSuccessImmune) && grid.get(0).get(f).getDays() == daysToInmune) { // esquinas
                                return new Person(2, grid.get(grid.size() - 1).get(grid.size()-1).getDays());
                            }
                            else if (grid.get(grid.size() - 1).get(f).getState() == 1 && (Math.random() < probabilityOfSuccessImmune) && grid.get(grid.size() - 2).get(f).getDays() == daysToInmune) { // esquinas
                                return new Person(2, grid.get(grid.size() - 1).get(f).getDays());
                            }
                            return grid.get(y).get(f);
                        }).toList())
                .map(listGrid -> Stream
                        .iterate(0, y -> y < listGrid.size(), y -> y + 1)
                        .limit(listGrid.size())
                        .map(i -> {
                            if ((listGrid.get(i).getState() == 1 || listGrid.get(i).getState() == 3) && (Math.random() < probabilityOfSuccessImmune)) { // people inmune
                                return new Person(2, listGrid.get(i).getDays());
                            }
                            else if (listGrid.get(0).getState() == 1 && (Math.random() < probabilityOfSuccessImmune) && listGrid.get(0).getDays() == daysToInmune) { // esquinas
                                return new Person(2, listGrid.get(listGrid.size() - 1).getDays());
                            }
                            else if (listGrid.get(listGrid.size() - 1).getState() == 1 && (Math.random() < probabilityOfSuccessImmune) && listGrid.get(listGrid.size() - 2).getDays() == daysToInmune) { // esquinas
                                return new Person(2, listGrid.get(listGrid.size() - 1).getDays());
                            }
                            return listGrid.get(i);
                        }).toList()).toList();
    }
    private static List<List<Person>> peopleWithMasc(List<List<Person>> grid, int probability, double probabilityOfSuccessWithMasc) {
        return Stream.iterate(0, y -> y < grid.size(), y -> y + 1)
                .map(y -> Stream.iterate(0, n -> n < grid.get(y).size(), n -> n + 1)
                        .map(f -> {
                            if ((grid.get(y).get(f).getState() == 1 || grid.get(y).get(f).getState() == 0) && (Math.random() < probabilityOfSuccessWithMasc && probability == 1) ) { // people with masc
                                return new Person(4, grid.get(y).get(f).getDays());
                            }
                            return grid.get(y).get(f);
                        }).toList())
                .map(listGrid -> Stream
                        .iterate(0, y -> y < listGrid.size(), y -> y + 1)
                        .limit(listGrid.size())
                        .map(i -> {
                            if ((listGrid.get(i).getState() == 1 || listGrid.get(i).getState() == 0) && (Math.random() < probabilityOfSuccessWithMasc && probability == 1) ) { // people with masc
                                return new Person(4, listGrid.get(i).getDays());
                            }
                            return listGrid.get(i);
                        }).toList()).toList();
    }
    private static List<List<Person>> peopleSurrounded(List<List<Person>> grid, double probabilityOfSuccessSurrounded) {
        return Stream.iterate(0, y -> y < grid.size(), y -> y + 1)
                .map(y -> Stream.iterate(0, n -> n < grid.get(y).size(), n -> n + 1)
                        .map(f -> {
                            if (grid.lastIndexOf(grid.get(y)) < grid.size() - 1 && grid.lastIndexOf(grid.get(y)) > 0) {
                                if ((grid.get(y).get(f).getState() == 0) && (grid.get(y + 1).get(f).getState() == 1) && (grid.get(y - 1).get(f).getState() == 1) && (Math.random() < probabilityOfSuccessSurrounded)) { // rodeado
                                    return new Person(3, grid.get(y).get(f).getDays());
                                }
                            }
                            return grid.get(y).get(f);
                        }).toList())
                .map(listGrid -> Stream
                        .iterate(0, y -> y < listGrid.size(), y -> y + 1)
                        .limit(listGrid.size())
                        .map(i -> {
                            if (listGrid.lastIndexOf(listGrid.get(i)) < listGrid.size() - 1 && listGrid.lastIndexOf(listGrid.get(i)) > 0) {
                                if((listGrid.get(i).getState() == 0) && (listGrid.get(i + 1).getState() == 1) && (listGrid.get(i - 1).getState() == 1) && (Math.random() < probabilityOfSuccessSurrounded)) {
                                    return new Person(3, listGrid.get(i).getDays());
                                }
                            }
                            return listGrid.get(i);
                        }).toList()).toList();
    }
    private static Person updateStateColumnInfected(List<List<Person>> grid, Integer y, Integer f, double probabilityOfSuccessInfected, int probability ) {
        if (grid.lastIndexOf(grid.get(y)) < grid.size() - 1 && grid.lastIndexOf(grid.get(y)) > 0) {
            if ((grid.get(y).get(f).getState() == 0 || grid.get(y).get(f).getState() == 4) && (grid.get(y + 1).get(f).getState() == 1) && (Math.random() < probabilityOfSuccessInfected) && (probability <= 2)) { // +
                return new Person(1, grid.get(y).get(f).getDays());
            }
            else if ((grid.get(y).get(f).getState() == 0 || grid.get(y).get(f).getState() == 4) && grid.get(y - 1).get(f).getState() == 1 && (Math.random() < probabilityOfSuccessInfected) && probability <= 2) { // -
                return new Person(1, grid.get(y).get(f).getDays());
            }
        }
        else if ((grid.get(0).get(f).getState() == 0 || grid.get(0).get(f).getState() == 4) && grid.get(1).get(f).getState() == 1 && (Math.random() < probabilityOfSuccessInfected) && probability <= 2) { // first
            return new Person(1, grid.get(0).get(f).getDays());
        }
        else if ((grid.get(grid.size() - 1).get(f).getState() == 0 || grid.get(grid.size() - 1).get(f).getState() == 4) && grid.get(grid.size() - 2).get(f).getState() == 1 && (Math.random() < probabilityOfSuccessInfected) && probability <= 2) { // last
            return new Person(1, grid.get(grid.size() - 1).get(f).getDays());
        }
        return grid.get(y).get(f);
    }
    private static List<Person> updateStateRowInfected(List<Person> listGrid, int probability) {
        return Stream
                .iterate(0, y -> y < listGrid.size(), y -> y + 1)
                .limit(listGrid.size())
                .map(i -> {
                    if (listGrid.lastIndexOf(listGrid.get(i)) < listGrid.size() - 1 && listGrid.lastIndexOf(listGrid.get(i)) > 0) {
                        if ((listGrid.get(i).getState() == 0 || listGrid.get(i).getState() == 4) && (listGrid.get(i + 1).getState() == 1) && (Math.random() < 0.5) && (probability <= 2)) { // +
                            return new Person(1, listGrid.get(i).getDays());
                        }
                        else if ((listGrid.get(i).getState() == 0 || listGrid.get(i).getState() == 4) && listGrid.get(i - 1).getState() == 1 && (Math.random() < 0.5) && probability <= 2) { // -
                            return new Person(1, listGrid.get(i).getDays());
                        }
                    }
                    else if ((listGrid.get(0).getState() == 0 || listGrid.get(0).getState() == 4) && listGrid.get(1).getState() == 1 && (Math.random() < 0.5) && probability <= 2) { // first
                        return new Person(1, listGrid.get(0).getDays());
                    }
                    else if ((listGrid.get(listGrid.size() - 1).getState() == 0 || listGrid.get(listGrid.size() - 1).getState() == 4) && listGrid.get(listGrid.size() - 2).getState() == 1 && (Math.random() < 0.5) && probability <= 2) { // last
                        return new Person(1, listGrid.get(listGrid.size() - 1).getDays());
                    }
                    return listGrid.get(i);
                }).toList();
    }

}
