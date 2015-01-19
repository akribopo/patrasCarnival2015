package gr.patras.carnival.game.data.repositories;

import gr.patras.carnival.game.data.model.Week;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WeekRepository
        extends CrudRepository<Week, Long> {

    List<Week> findById(Integer id);

}
