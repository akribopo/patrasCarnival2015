package gr.patras.carnival.game.data.repositories;

import gr.patras.carnival.game.data.model.UserAnswers;
import org.springframework.data.repository.CrudRepository;

public interface UserAnswersRepository
        extends CrudRepository<UserAnswers, Long> {

    UserAnswers findByUserId(Long userId);

}
