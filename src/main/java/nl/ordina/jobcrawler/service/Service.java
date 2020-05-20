package nl.ordina.jobcrawler.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Maybe another name can be chosen as to not interfere
public abstract class Service<T> {
    //******** Adding to database ********//
    abstract T add(T t);

    //******** Getting from database ********//
    abstract List<T> getAll();

    abstract Optional<T> getById(UUID id);

    //******** Deleting from database ********//
    abstract void deleteById(UUID id);

    //******** Updating entries ********//
    abstract T update(UUID id, T newT);
}
