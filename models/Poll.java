package models;

import java.util.ArrayList;

public class Poll {
    private String title;
    private String startDate;
    private ArrayList<String> options;

    public Poll(String title, String startDate) {
        this.title = title;
        this.startDate = startDate;
        this.options = new ArrayList<>();
    }

    public void addOption(String option) {
        options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }
}
