package com.example.lso_project.Helpers;

import java.util.ArrayList;

public class Action {

    private final ArrayList<IEvent> events = new ArrayList<>();

    public void addListener(IEvent event)
    {
        events.add(event);
    }

    public void removeListener(IEvent event)
    {
        events.remove(event);
    }

    public void run()
    {
        for (IEvent event:
             events) {
            event.run();
        }
    }
}
