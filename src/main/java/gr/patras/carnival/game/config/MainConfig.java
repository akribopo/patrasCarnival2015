/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.patras.carnival.game.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main configuration class for the application.
 * Turns on @Component scanning, loads externalized application.properties, and sets up the database.
 * @author Craig Walls
 */
@Configuration
@ComponentScan(basePackages = "gr.patras.carnival.game.", excludeFilters = { @Filter(Configuration.class) })
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class MainConfig {

//	@Bean(destroyMethod = "shutdown")
//	public DataSource dataSource() {
//		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
//		factory.setDatabaseName("spring-social-showcase");
//		factory.setDatabaseType(EmbeddedDatabaseType.H2);
//		factory.setDatabasePopulator(databasePopulator());
//		return factory.getDatabase();
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager() {
//		return new DataSourceTransactionManager(dataSource());
//	}
//
//	@Bean
//	public JdbcTemplate jdbcTemplate() {
//		return new JdbcTemplate(dataSource());
//	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
//	// internal helpers
//	private DatabasePopulator databasePopulator() {
//		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//		populator.addScript(new ClassPathResource("JdbcUsersConnectionRepository.sql", JdbcUsersConnectionRepository.class));
//		populator.addScript(new ClassPathResource("Account.sql", JdbcAccountRepository.class));
//		populator.addScript(new ClassPathResource("data.sql", JdbcAccountRepository.class));
//		return populator;
//	}
}
