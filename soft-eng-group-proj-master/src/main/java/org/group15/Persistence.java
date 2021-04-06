package org.group15;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Persistence
{

    private String impressionLogPath, clickLogPath, serverLogPath;
    private int bounceMaxPagesViewed = 1;
    private int bounceMaxSecondsOnSite = 0;

    private ArrayList<Query> tabs = new ArrayList<>();

    public Persistence(String impressionLogPath, String clickLogPath, String serverLogPath)
    {
        this.impressionLogPath = impressionLogPath;
        this.clickLogPath = clickLogPath;
        this.serverLogPath = serverLogPath;
    }

    //Don't use this!
    public Persistence() { }

    public static Persistence fromFile(String filePath) throws IOException
    {
        try
        {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Persistence.class);
        }
        catch (IOException e)
        {
            ErrorHandling.displayError(e, false);
            throw e;
        }
    }

    public void addQuery(Query q)
    {
        tabs.add(q);
    }

    public void removeQuery(String name)
    {
        tabs.removeIf(x -> name.equals(x.tabName));
    }

    public Queryable<Query> procureSavedTabs()
    {
        return new Queryable<Query>()
        {
            @Override
            public Enumerator<Query> getEnumerator()
            {
                return new IterableEnumerator<>((ArrayList<Query>) tabs.clone());
            }
        };
    }

    public void save(String filePath)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(this);
            Files.write(Paths.get(filePath), value.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
        catch (Exception e)
        {
            ErrorHandling.displayError(e, false);
        }
    }

    public String getImpressionLogPath()
    {
        return impressionLogPath;
    }

    public void setImpressionLogPath(String impressionLogPath)
    {
        this.impressionLogPath = impressionLogPath;
    }

    public String getClickLogPath()
    {
        return clickLogPath;
    }

    public void setClickLogPath(String clickLogPath)
    {
        this.clickLogPath = clickLogPath;
    }

    public String getServerLogPath()
    {
        return serverLogPath;
    }

    public void setServerLogPath(String serverLogPath)
    {
        this.serverLogPath = serverLogPath;
    }

    public ArrayList<Query> getTabs()
    {
        return tabs;
    }

    public void setTabs(ArrayList<Query> tabs)
    {
        this.tabs = tabs;
    }

    public int getBounceMaxPagesViewed() {
        return bounceMaxPagesViewed;
    }

    public void setBounceMaxPagesViewed(int bounceMaxPagesViewed) {
        this.bounceMaxPagesViewed = bounceMaxPagesViewed;
    }

    public int getBounceMaxSecondsOnSite() {
        return bounceMaxSecondsOnSite;
    }

    public void setBounceMaxSecondsOnSite(int bounceMaxSecondsOnSite) {
        this.bounceMaxSecondsOnSite = bounceMaxSecondsOnSite;
    }


    // Because AddQueryView objects are constantly created and destroyed, maybe put the loading stuff
    // (calling createTab method from AddQueryView with the contents of each Query) in the constructor of Persistence, since it will only
    // need to be called once.
}
