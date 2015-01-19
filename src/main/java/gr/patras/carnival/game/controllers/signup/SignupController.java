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
package gr.patras.carnival.game.controllers.signup;

import gr.patras.carnival.game.controllers.signin.SignInUtils;
import gr.patras.carnival.game.data.model.Account;
import gr.patras.carnival.game.data.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
public class SignupController {


    private final ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(WebRequest request) {
        Connection<Facebook> connection = (Connection<Facebook>) providerSignInUtils.getConnectionFromSession(request);
        if (connection != null) {
            final FacebookProfile userProfile = connection.getApi().userOperations().getUserProfile();
            final String faceboooId = userProfile.getId();

            //Check if user is already registered
            final Account dbAccount = accountRepository.findByFacebookId(faceboooId);

            if (dbAccount != null) {
                //User already registered
                updateProfileImage(dbAccount, connection.getImageUrl());
                SignInUtils.signin(dbAccount.getUsername());
                providerSignInUtils.doPostSignUp(dbAccount.getUsername(), request);
                return "redirect:/";
            } else {
                final String imageUrl = connection.getImageUrl();
                final String profileUrl = connection.getProfileUrl();
                final Account account = createAccount(userProfile, imageUrl, profileUrl);
                SignInUtils.signin(account.getUsername());
                providerSignInUtils.doPostSignUp(account.getUsername(), request);
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    // internal helpers
    private Account createAccount(FacebookProfile fbProfile, final String imageUrl, final String profileUrl) {
        final Account newAccount = new Account();
        newAccount.setUsername(fbProfile.getId());
        newAccount.setFacebookId(fbProfile.getId());
        newAccount.setFirstName(fbProfile.getFirstName());
        newAccount.setLastName(fbProfile.getLastName());
        newAccount.setName(fbProfile.getName());
        newAccount.setImageUrl(imageUrl);
        newAccount.setProfileUrl(profileUrl);
        accountRepository.save(newAccount);
        return accountRepository.findByFacebookId(newAccount.getFacebookId());
    }

    //Update Profile Image if changed
    private void updateProfileImage(final Account account, final String imageUrl) {
        if (!account.getImageUrl().equals(imageUrl)) {
            final Account oldAccount = accountRepository.findByFacebookId(account.getFacebookId());
            oldAccount.setImageUrl(imageUrl);
            accountRepository.save(oldAccount);
        }
    }
}
