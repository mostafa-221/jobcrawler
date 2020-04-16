package nl.ordina.jobcrawler.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Maybe another name can be chosen as to not interfere
public interface Service<T> {
    //******** Adding to database ********//
    T add(T t);

    //******** Getting from database ********//
    List<T> getAll();

    Optional<T> getById(UUID id);


    //******** Deleting from database ********//
    void deleteById(UUID id);

    //******** Updating entries ********//
    T update(UUID id, T newT);
}
