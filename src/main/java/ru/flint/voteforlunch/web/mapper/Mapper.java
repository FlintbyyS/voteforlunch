package ru.flint.voteforlunch.web.mapper;

public interface Mapper<E, D> {

    E toEntity(D dto);

    D toDTO(E entity);
}
