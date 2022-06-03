package edaii.simcovid.game;

public class VirusParameters {
    final int transmissionPercent;
    final int lifetimeInDays;
    final int daysToBeInmune;
    final double probabilityOfSuccessInfected;
    final double probabilityOfSuccessDead;
    final double probabilityOfSuccessImmune;
    final double probabilityOfSuccessWithMasc;
    final double probabilityOfSuccessSurrounded;

    /**
     * Virus parameters
     *
     * @param transmissionPercent Percentage of transmissibility
     * @param lifetimeInDays      Life-time in a host until get immunity
     */
    public VirusParameters(int transmissionPercent, int lifetimeInDays,
                           int daysToBeInmune, double probabilityOfSuccessInfected,
                           double probabilityOfSuccessDead, double probabilityOfSuccessImmune,
                           double probabilityOfSuccessWithMasc, double probabilityOfSuccessSurrounded
    ) {
        this.transmissionPercent = transmissionPercent;
        this.lifetimeInDays = lifetimeInDays;
        this.daysToBeInmune = daysToBeInmune;
        this.probabilityOfSuccessInfected = probabilityOfSuccessInfected;
        this.probabilityOfSuccessDead = probabilityOfSuccessDead;
        this.probabilityOfSuccessImmune = probabilityOfSuccessImmune;
        this.probabilityOfSuccessWithMasc = probabilityOfSuccessWithMasc;
        this.probabilityOfSuccessSurrounded = probabilityOfSuccessSurrounded;
    }

}
