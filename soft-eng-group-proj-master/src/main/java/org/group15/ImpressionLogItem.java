package org.group15;

/**
 * Class for representing an item in impression log
 */
public class ImpressionLogItem {
    public final DateTime date;
    public final long id;
    public final Gender gender;
    public final Age age;
    public final Income income;
    public final Context context;
    public final double cost;

    public ImpressionLogItem(DateTime date, long id, Gender gender, Age age, Income income, Context context, double cost) {
        this.date = date;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.income = income;
        this.context = context;
        this.cost = cost;
    }

}
