package gr.patras.carnival.game.account;

/**
 * Created by akribopo on 18/1/2015.
 */
import java.util.List;

import gr.patras.carnival.game.account.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountJPARepository extends CrudRepository<Account, Long> {

    List<Account> findByLastName(String lastName);

    List<Account> findByUsername(String username);
}
