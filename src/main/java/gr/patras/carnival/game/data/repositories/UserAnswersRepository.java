package gr.patras.carnival.game.data.repositories;

import gr.patras.carnival.game.data.model.UserAnswers;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserAnswersRepository
        extends CrudRepository<UserAnswers, Long> {

    List<UserAnswers> findByUserId(Long userId);

}
