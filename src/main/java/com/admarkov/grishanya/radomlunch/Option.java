package com.admarkov.grishanya.radomlunch;

public class Option {

    public Option(String name, int distance, boolean nalunch) {
        this.name = name;
        this.distance = distance;
        this.nalunch = nalunch;
        this.votes = 0;
    }

    private String name;
    private int distance;
    private boolean nalunch;
    private int votes;

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isNalunch() {
        return nalunch;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote() {
        votes++;
    }

    public void clearVotes() {
        votes = 0;
    }
}
