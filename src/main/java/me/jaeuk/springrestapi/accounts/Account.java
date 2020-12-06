package me.jaeuk.springrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Integer id;
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}


/*

1. 웹 시큐리티
- 웝 요청에 보안 인증
- Fillter 기반

2. 메서드 시큐리티
- 웹과 상관없이 어떤 메소드가 호출 되었을 때, 인증, 권한 확인
- AOP / 프록시 만들어서



스프링 5 부터는
웹플럭스 / 서블릭 기반의 웹


 */
