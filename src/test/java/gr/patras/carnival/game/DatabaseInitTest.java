package gr.patras.carnival.game;

import gr.patras.carnival.game.data.repositories.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by akribopo on 21/1/2015.
 *
 * The specific unit test will be used as tool to insert new Questions/Answers to the DB.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DatabaseInitTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    AccountRepository accountRepository;

    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();

    }

    @Test
    public void testFindAll() throws Exception {
        this.accountRepository.findAll();
    }

}
