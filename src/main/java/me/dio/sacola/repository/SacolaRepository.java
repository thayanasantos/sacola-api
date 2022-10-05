package me.dio.sacola.repository;

import me.dio.sacola.model.Sacola;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository

public interface SacolaRepository  extends JpaRepositoryImplementation<Sacola, Long> {

}
