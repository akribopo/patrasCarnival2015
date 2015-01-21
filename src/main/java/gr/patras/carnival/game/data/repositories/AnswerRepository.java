package gr.patras.carnival.game.data.repositories;

import gr.patras.carnival.game.data.model.Answer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ichatz on 20/1/2015.
 */
public interface AnswerRepository
        extends CrudRepository<Answer, Long> {

    List<Answer> findById(Integer id);

    List<Answer> findByWeek(Integer week);

}
