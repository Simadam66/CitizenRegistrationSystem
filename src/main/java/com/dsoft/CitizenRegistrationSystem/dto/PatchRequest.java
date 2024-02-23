package com.dsoft.CitizenRegistrationSystem.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class PatchRequest {
    private HashMap<String, String> update;
    private HashMap<String, String> receive;


    public PatchRequest() {
        this.update = new HashMap<>();
        this.receive = new HashMap<>();
    }
}
