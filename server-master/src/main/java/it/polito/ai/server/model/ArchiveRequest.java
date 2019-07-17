package it.polito.ai.server.model;

import java.util.ArrayList;

public class ArchiveRequest {
    private ArrayList<String> ids;

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}
