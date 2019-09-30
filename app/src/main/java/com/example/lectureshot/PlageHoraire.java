package com.example.lectureshot;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 *
 * @author Ami
 *
 */

public class PlageHoraire implements Serializable {
    long startDate;
    long endDate;

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    /**
     * @param startDate
     * @param endDate
     */
    public PlageHoraire(long startDate, long endDate) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @param date
     * @return
     */
    static String dateToString(long date) {
        GregorianCalendar d = new GregorianCalendar();
        d.setTimeInMillis(date);
        return d.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (d.get(GregorianCalendar.MONTH) + 1) + "/"
                + d.get(GregorianCalendar.YEAR) + "-" + d.get(GregorianCalendar.HOUR_OF_DAY) + ":"
                + d.get(GregorianCalendar.MINUTE);
    }

    @Override
    public String toString() {
        return "de " + dateToString(startDate) + " à " + dateToString(endDate);
    }
}
