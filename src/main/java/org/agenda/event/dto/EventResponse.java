package org.agenda.event.dto;

import java.util.List;

public record EventResponse (boolean success, String message, List<String> warnings){

    public void addWarnings(String warning){
        if (!warnings.contains(warning)) warnings.add(warning);
    }
}
