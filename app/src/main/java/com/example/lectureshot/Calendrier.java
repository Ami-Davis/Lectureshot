package com.example.lectureshot;

/**
 *
 * @author Ami
 *
 */

public class Calendrier {

    private boolean isSelected;
    public String name;
    public String id;
    public Calendrier(String _name, String _id) {
        name = _name;
        id = _id;
    }
    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;

    }

}
