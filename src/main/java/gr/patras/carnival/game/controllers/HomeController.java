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
package gr.patras.carnival.game.controllers;


import gr.patras.carnival.game.data.repositories.AccountRepository;
import gr.patras.carnival.game.data.repositories.QuestionRepository;
import gr.patras.carnival.game.data.repositories.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Provider;
import java.security.Principal;

@Controller
public class HomeController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private WeekRepository weekRepository;

	@Autowired
	private Provider<ConnectionRepository> connectionRepositoryProvider;

	@RequestMapping("/")
	public String home(final Principal currentUser, final Model model) {
		model.addAttribute("connectionsToProviders", connectionRepositoryProvider.get().findAllConnections());
		model.addAttribute(accountRepository.findByUsername(currentUser.getName()));

		// Retrieve week ID
		final long lastWeekId = weekRepository.findOne(new Long(1)).getWeek();
		model.addAttribute("lastWeekId", lastWeekId);

		model.addAttribute("questions", questionRepository.findByWeek(1));

		return "home";
	}

}
