package edaii.simcovid.entities;

public class Person {

    public static final int STATE_NOT_INFECTED = 0;
    public static final int STATE_INFECTED = 1;
    public static final int STATE_IMMUNE = 2;
    public static final int STATE_SURROUNDED = 3;
    public static final int STATE_MASKED = 4;
    public static final int STATE_DEAD = 5;

    private final int state;
    private final int days;

    public Person(int state) {
        this.state = state;
        this.days = 0;
    }

    public Person(Person self) {
        this.state = self.getState();
        this.days = self.getDays() + 1;
    }

    public Person(int state, int day) {
        this.state = state;
        this.days = day;
    }

    public static Person infected() {
        return new Person(1);
    }

    public static Person notInfected() {
        return new Person(0);
    }

    public int getDays() {
        return this.days;
    }
    public int getState() {
        return this.state;
    }

    public Person setState(int infect) {
        return new Person(infect);
    }

}
