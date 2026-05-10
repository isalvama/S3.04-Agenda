package org.agenda.event.dto;

import java.util.List;

public record EventResponse (int id, String title, List<String> warnings){
}
