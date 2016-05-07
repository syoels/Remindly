package remindly.remindly;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by omer on 07/05/2016.
 */
public class Person {
    public int count;
    public String pid, name, pictureUrl, work;
    public ArrayList<String> notifications;
    public Date bDate, lastSeen;
    public boolean isFB;
    JSONArray wishList;

}
