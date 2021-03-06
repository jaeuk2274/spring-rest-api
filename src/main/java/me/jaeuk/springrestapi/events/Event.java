package me.jaeuk.springrestapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jaeuk.springrestapi.accounts.Account;
import me.jaeuk.springrestapi.accounts.AccountSerializer;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @EqualsAndHashCode(of = "id") // 상호참조 문제. 다른 엔티티는 묶지 말것. (스택오버 플로우 발생 위험, 상호참조하며 계속 호출)
@Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING) // 나중에라도 순서 바뀔 수 있음.
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class) // event의 serialize 할 때는 구체적인 정보가 필요없기 때문에
    private Account manager;

    public void update(){
        if(this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }

        if(this.location == null || this.location.isBlank()){
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
