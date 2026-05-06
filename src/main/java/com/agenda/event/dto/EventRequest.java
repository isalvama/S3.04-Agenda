package com.agenda.event.dto;

import java.io.Serializable;

public record EventRequest(String description, String repetition) { }