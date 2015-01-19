package gr.patras.carnival.game.data.repositories;

import gr.patras.carnival.game.data.model.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ichatz on 20/1/2015.
 */
public interface QuestionRepository
        extends CrudRepository<Question, Long> {

    List<Question> findById(Integer id);

    List<Question> findByWeek(Integer week);

}
