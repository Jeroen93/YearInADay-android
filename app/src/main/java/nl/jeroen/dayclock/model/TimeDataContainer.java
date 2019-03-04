package nl.jeroen.dayclock.model;

public class TimeDataContainer {
    public long hours;
    public long minutes;
    public long seconds;
    public long millis;

    public TimeDataContainer(long hour, long minute, long second, long milli) {
        this.hours = hour;
        this.minutes = minute;
        this.seconds = second;
        this.millis = milli;
    }
}
