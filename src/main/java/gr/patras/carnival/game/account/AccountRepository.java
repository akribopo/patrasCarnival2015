package gr.patras.carnival.game.account;

/**
 * Created by akribopo on 18/1/2015.
 */

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByLastName(String lastName);

    List<Account> findByUsername(String username);
}
