package ru.flint.voteforlunch.util.exceptions;

import org.springframework.http.HttpStatus;

public class IllegalRequestDataException extends ApplicationException{
    public IllegalRequestDataException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
