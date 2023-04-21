package ru.flint.voteforlunch.util.exceptions;

import org.springframework.http.HttpStatus;

public class VoteTimeConstraintException extends ApplicationException{
    public VoteTimeConstraintException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
