package ru.flint.voteforlunch.web.mapper;

public interface RequestResponseMapper<E, RequestType, ResponseType> {

    E toEntity(RequestType dto);

    ResponseType toDTO(E entity);
}