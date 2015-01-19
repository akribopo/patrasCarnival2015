package gr.patras.carnival.game.data.repositories;

/**
 * Created by akribopo on 18/1/2015.
 */

import gr.patras.carnival.game.data.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByLastName(String lastName);

    Account findByUsername(String username);

    Account findByFacebookId(String id);
}
