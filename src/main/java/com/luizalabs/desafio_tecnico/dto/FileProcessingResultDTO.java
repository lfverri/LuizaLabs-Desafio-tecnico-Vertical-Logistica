package com.luizalabs.desafio_tecnico.dto;

import java.util.List;
import lombok.Data;

public class FileProcessingResultDTO {
    private List<UserDTO> users;
    private List<String> errors;

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}


