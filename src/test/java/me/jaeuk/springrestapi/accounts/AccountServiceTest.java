package me.jaeuk.springrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("text")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void findByUsername(){
        // Given
        String password ="jaeuk";
        String username ="jaeuk2274@gmail.com";

        Account account = Account.builder()
                    .email(username)
                    .password(password)
                    .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                    .build();
        this.accountRepository.save(account);
        // When
        UserDetailsService userDetailsService = (UserDetailsService)accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertEquals(userDetails.getPassword(), password);
    }

}