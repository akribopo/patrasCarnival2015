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


import gr.patras.carnival.game.data.model.*;
import gr.patras.carnival.game.data.repositories.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserAnswersRepository userAnswersRepository;

    @Autowired
    private Provider<ConnectionRepository> connectionRepositoryProvider;

    private void buildModel(final Account currentUser, final Model model) {
        if (currentUser.getHasPosted() == null) {
            currentUser.setHasPosted(false);
        }

        //Users will be asked to post on FB only once.
        if (currentUser.getHasPosted()) {
            model.addAttribute("askToPost", false);
        } else {
            model.addAttribute("askToPost", true);
        }

        // Retrieve week ID
        //final long lastWeekId = weekRepository.findOne(new Long(1)).getWeek();
        final long lastWeekId = 4;
        model.addAttribute("lastWeekId", lastWeekId);

        // Retrieve language
        final String language = getCurrentLanguage();
        model.addAttribute("lan", language);

        if (lastWeekId >= 5) {
            model.addAttribute("questions", new ArrayList<Question>());
            model.addAttribute("answers", new HashMap<Integer, List<Answer>>());
            model.addAttribute("userAnswers", new HashMap<Long, Long>());
            return;
        }

        // Retrieve all questions + answers up to last week
        final List<Question> lstQuestions = new ArrayList<Question>();
        final Map<Integer, List<Answer>> mapAnswers = new HashMap<Integer, List<Answer>>();
        for (int week = 1; week <= lastWeekId; week++) {
            final List<Question> thisWeekQuestions = questionRepository.findByWeek(week);
            boolean isFirst = true;
            if (language.equals("en")) {
                for (Question thisWeekQuestion : thisWeekQuestions) {
                    thisWeekQuestion.setText(thisWeekQuestion.getTextEn());
                    if (isFirst) {
                        thisWeekQuestion.setPhaseChange(true);
                        isFirst = false;
                    }
                }
            } else {
                for (Question thisWeekQuestion : thisWeekQuestions) {
                    thisWeekQuestion.setText(thisWeekQuestion.getTextGr());
                    if (isFirst) {
                        thisWeekQuestion.setPhaseChange(true);
                        isFirst = false;
                    }
                }
            }

            lstQuestions.addAll(thisWeekQuestions);
            LOGGER.debug("Questions for Week " + week + " found: " + thisWeekQuestions.size());

            final List<Answer> thisWeekAnswers = answerRepository.findByWeek(week);
            if (language.equals("en")) {
                for (Answer thisWeekAnswer : thisWeekAnswers) {
                    thisWeekAnswer.setText(thisWeekAnswer.getTextEn());
                }
            } else {
                for (Answer thisWeekAnswer : thisWeekAnswers) {
                    thisWeekAnswer.setText(thisWeekAnswer.getTextGr());
                }
            }

            mapAnswers.put(week, thisWeekAnswers);
            LOGGER.debug("Answers for Week " + week + " found: " + thisWeekAnswers.size());
        }

        // Check out if user has given answers
        final List<UserAnswers> userAnswers = userAnswersRepository.findByUserId(Long.valueOf(currentUser.getId()));
        final Map<Long, Long> mapUserAnswers = new HashMap<Long, Long>();
        for (final UserAnswers userAnswer : userAnswers) {
            mapUserAnswers.put(userAnswer.getQuestionId(), userAnswer.getAnswerId());
        }

        model.addAttribute("questions", lstQuestions);
        model.addAttribute("answers", mapAnswers);
        model.addAttribute("userAnswers", mapUserAnswers);
    }

    private void buildLastModel(final Account currentUser, final Model model) {
        if (currentUser.getHasPosted() == null) {
            currentUser.setHasPosted(false);
        }

        //Users will be asked to post on FB only once.
        if (currentUser.getHasPosted()) {
            model.addAttribute("askToPost", false);
        } else {
            model.addAttribute("askToPost", true);
        }

        // Retrieve language
        final String language = getCurrentLanguage();
        model.addAttribute("lan", language);

        // Retrieve week ID
        final long lastWeekId = weekRepository.findOne(new Long(1)).getWeek();
        if (lastWeekId == 26) {
            // the treasure is found!
            model.addAttribute("found", 1);
            model.addAttribute("wronganswer", 0);
            return;

        } else {
            model.addAttribute("found", 0);
        }

        // Identify in which step is the user
        int lastStepId = 20;
        final List<UserAnswers> thisUserAnswers = userAnswersRepository.findByUserId(currentUser.getId());
        for (UserAnswers userAnswer : thisUserAnswers) {
            if (userAnswer.getQuestionId() > lastStepId) {
                lastStepId = (int) userAnswer.getQuestionId();
            }
        }

        // Retrieve the question
        final List<Question> thisStepQuestions = questionRepository.findByWeek(lastStepId);

        final Question thisQuestion = thisStepQuestions.get(0);
        if (language.equals("en")) {
            thisQuestion.setText(thisQuestion.getTextEn());
        } else {
            thisQuestion.setText(thisQuestion.getTextGr());
        }

        model.addAttribute("question", thisQuestion);
        model.addAttribute("round", thisQuestion.getId());
        model.addAttribute("wronganswer", 0);
    }

    @RequestMapping("/final/round")
    public String home(final Model model) {
        model.addAttribute("connectionsToProviders", connectionRepositoryProvider.get().findAllConnections());
        final Account user = accountRepository.findByUsername(currentUser.getName());
        model.addAttribute(user);

        // Retrieve week ID
        //final long lastWeekId = weekRepository.findOne(new Long(1)).getWeek();

        buildLastModel(user, model);

        return "final";
    }

    /**
     * The controller used to sign users up using only the email address.
     *
     * @param model the map containing the model.
     * @return the corresponding view.
     */
    @RequestMapping(value = "/final/round", method = RequestMethod.POST)
    public String postCreateLast(final Principal currentUser, final Model model,
                                 final HttpServletRequest request) {
        model.addAttribute("connectionsToProviders", connectionRepositoryProvider.get().findAllConnections());
        final Account user = accountRepository.findByUsername(currentUser.getName());
        model.addAttribute(user);
        buildLastModel(user, model);

        user.setHasPosted(true);
        accountRepository.save(user);
        model.addAttribute("askToPost", false);

        // Retrieve language
        final String language = getCurrentLanguage();
        model.addAttribute("lan", language);

        // Identify in which step is the user
        int lastStepId = 20;
        final List<UserAnswers> thisUserAnswers = userAnswersRepository.findByUserId(user.getId());
        for (UserAnswers userAnswer : thisUserAnswers) {
            if (userAnswer.getQuestionId() > lastStepId) {
                lastStepId = (int) userAnswer.getQuestionId();
            }
        }

        // Retrieve the question
        final List<Question> thisStepQuestion = questionRepository.findByWeek(lastStepId);
        Question thisQuestion = thisStepQuestion.get(0);
        if (language.equals("en")) {
            thisQuestion.setText(thisQuestion.getTextEn());
        } else {
            thisQuestion.setText(thisQuestion.getTextGr());
        }
        model.addAttribute("question", thisQuestion);
        model.addAttribute("round", thisQuestion.getId());

        // Retrieve Answer
        final String value = request.getParameter("answer");

        // identify if answer is correct
        int wronganswer = 1;
        int found = 0;
        switch (lastStepId) {

            case 20: // 1st round
                if (value.equals("29")) {
                    // Correct answer
                    wronganswer = 0;

                    // Store new answer
                    final UserAnswers thisAnswer = new UserAnswers();
                    thisAnswer.setUserId(user.getId());
                    thisAnswer.setQuestionId(thisQuestion.getId());
                    thisAnswer.setAnswerId(1);
                    userAnswersRepository.save(thisAnswer);

                    // progress step counter
                    lastStepId = 21;
                }
                break;

            case 21: // 2nd round
                if (value.equals("49")) {
                    // Correct answer
                    wronganswer = 0;

                    // Store new answer
                    final UserAnswers thisAnswer = new UserAnswers();
                    thisAnswer.setUserId(user.getId());
                    thisAnswer.setQuestionId(thisQuestion.getId());
                    thisAnswer.setAnswerId(1);
                    userAnswersRepository.save(thisAnswer);

                    // progress step counter
                    lastStepId = 22;
                }
                break;

            case 22: // 3rd round
                if (value.equals("9")) {
                    // we need one more digit
                    final String value2 = request.getParameter("answer2");

                    if (value2.equals("7")) {
                        // Correct answer
                        wronganswer = 0;

                        // Store new answer
                        final UserAnswers thisAnswer = new UserAnswers();
                        thisAnswer.setUserId(user.getId());
                        thisAnswer.setQuestionId(thisQuestion.getId());
                        thisAnswer.setAnswerId(1);
                        userAnswersRepository.save(thisAnswer);

                        // progress step counter
                        lastStepId = 23;
                    }
                }
                break;

            case 23: // 4th round
                if (value.equals("8")) {
                    // we need one more digit
                    final String value2 = request.getParameter("answer2");

                    if (value2.equals("4")) {
                        // Correct answer
                        wronganswer = 0;

                        // Store new answer
                        final UserAnswers thisAnswer = new UserAnswers();
                        thisAnswer.setUserId(user.getId());
                        thisAnswer.setQuestionId(thisQuestion.getId());
                        thisAnswer.setAnswerId(1);
                        userAnswersRepository.save(thisAnswer);

                        // progress step counter
                        lastStepId = 24;
                    }
                }
                break;

            case 24: // 5th round
                if (value.equals("1")) {
                    // we need one more digit
                    final String value2 = request.getParameter("answer2");

                    if (value2.equals("1")) {

                        // we need one more digit
                        final String value3 = request.getParameter("answer3");

                        if (value3.equals("4")) {
                            // Correct answer
                            wronganswer = 0;

                            // Store new answer
                            final UserAnswers thisAnswer = new UserAnswers();
                            thisAnswer.setUserId(user.getId());
                            thisAnswer.setQuestionId(thisQuestion.getId());
                            thisAnswer.setAnswerId(1);
                            userAnswersRepository.save(thisAnswer);

                            // progress step counter
                            lastStepId = 25;
                        }
                    }
                }
                break;

            case 25: // 6th round
                if (value.toLowerCase().equals("speech")) {
                    // We have a winner !

                    // Store new answer
                    final UserAnswers thisAnswer = new UserAnswers();
                    thisAnswer.setUserId(user.getId());
                    thisAnswer.setQuestionId(thisQuestion.getId());
                    thisAnswer.setAnswerId(1);
                    userAnswersRepository.save(thisAnswer);

                    // progress step counter
                    lastStepId = 26;
                    found = 1;
                    wronganswer = 0;

                    // conclude the game - treasure found!
                    final List<Week> lstWeek = weekRepository.findById(new Long(1));
                    final Week week = lstWeek.get(0);
                    week.setWeek(26);
                    weekRepository.save(week);

                    model.addAttribute("found", 1);
                }
                break;
        }

        if (wronganswer == 0 && lastStepId < 26) {
            // retrieve next question
            final List<Question> nextStepQuestion = questionRepository.findByWeek(lastStepId);
            Question nextQuestion = nextStepQuestion.get(0);
            if (language.equals("en")) {
                nextQuestion.setText(nextQuestion.getTextEn());
            } else {
                nextQuestion.setText(nextQuestion.getTextGr());
            }
            model.addAttribute("question", nextQuestion);
            model.addAttribute("round", nextQuestion.getId());
        }

        model.addAttribute("wronganswer", wronganswer);
        model.addAttribute("found", found);

        return "final";
    }


    @RequestMapping("/privacy")
    public String privacy() {
        return "terms";
    }

    @RequestMapping("/terms")
    public String terms() {
        return "terms";
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

    @RequestMapping("/")
    public String home(final Principal currentUser, final Model model) {
        model.addAttribute("connectionsToProviders", connectionRepositoryProvider.get().findAllConnections());
        final Account user = accountRepository.findByUsername(currentUser.getName());
        model.addAttribute(user);
        buildModel(user, model);
        return "home";
    }

    /**
     * The controller used to sign users up using only the email address.
     *
     * @param model the map containing the model.
     * @return the corresponding view.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String postCreate(final Principal currentUser, final Model model,
                             final HttpServletRequest request) {
        model.addAttribute("connectionsToProviders", connectionRepositoryProvider.get().findAllConnections());

        final Account user = accountRepository.findByUsername(currentUser.getName());

        model.addAttribute(user);
        buildModel(user, model);

        user.setHasPosted(true);
        accountRepository.save(user);
        model.addAttribute("askToPost", false);

        // Delete any previous answers
        final List<UserAnswers> userAnswers = userAnswersRepository.findByUserId(user.getId());
        if (userAnswers != null) {
            for (final UserAnswers userAnswer : userAnswers) {
                userAnswersRepository.delete(userAnswer);
            }
        }

        // Save answers
        final Map<Long, Long> mapUserAnswers = new HashMap<Long, Long>();
        final List<Question> lstQuestions = (List<Question>) model.asMap().get("questions");
        for (final Question question : lstQuestions) {
            final String questionId = String.valueOf(question.getId());

            // Retrieve Answer
            final String value = request.getParameter(questionId);
            if (value != null) {
                // Store new answer
                final UserAnswers thisAnswer = new UserAnswers();
                thisAnswer.setUserId(user.getId());
                thisAnswer.setQuestionId(question.getId());
                thisAnswer.setAnswerId(Long.valueOf(value));
                userAnswersRepository.save(thisAnswer);

                mapUserAnswers.put(question.getId(), thisAnswer.getAnswerId());
            }
        }

        model.addAttribute("userAnswers", mapUserAnswers);

        return "home";
    }

    private String getCurrentLanguage() {
        final Locale locale = LocaleContextHolder.getLocale();
        final String language = locale.getLanguage();
        LOGGER.info("Current Language is: " + language);
        return language;
    }
}
