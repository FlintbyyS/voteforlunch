package ru.flint.voteforlunch.mapper;

public interface Mapper<E, D> {

    E toEntity(D dto);

    D toDTO(E entity);
}
