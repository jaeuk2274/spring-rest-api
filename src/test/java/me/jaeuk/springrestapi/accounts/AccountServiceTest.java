package me.jaeuk.springrestapi.accounts;

import me.jaeuk.springrestapi.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;


public class AccountServiceTest extends BaseControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("스프링 시큐리티 학습테스트")
    public void findByUsername(){
        // Given
        String password ="choi";
        String username ="choi@gmail.com";

        Account account = Account.builder()
                    .email(username)
                    .password(password)
                    .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                    .build();
        this.accountService.saveAccount(account);
        // When
        UserDetailsService userDetailsService = (UserDetailsService)accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        System.out.println("userDetails.getPassword() : " + userDetails.getPassword());
        assertTrue(this.passwordEncoder.matches(password, userDetails.getPassword()));
    }

    @Test
    @DisplayName("방법1. 유저네임 없을 경우")
    public void findByUsernameFail(){
        String username = "notpeople@eamil.com";
        try{
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
        }
    }

    @Test
    @DisplayName("방법2. 유저네임 없을 경우")
    public void findByUsernameFail2(){
        // Expected, 예상되는 예외를 미리 적어줘야 한다.
        String username = "notpeople@eamil.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When, 해당 예외가 발생하지 않으면 에러
        //accountService.loadUserByUsername(username);
    }


}