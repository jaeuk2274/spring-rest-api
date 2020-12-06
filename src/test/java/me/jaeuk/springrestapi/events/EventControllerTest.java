package me.jaeuk.springrestapi.events;

import me.jaeuk.springrestapi.accounts.Account;
import me.jaeuk.springrestapi.accounts.AccountRepository;
import me.jaeuk.springrestapi.accounts.AccountRole;
import me.jaeuk.springrestapi.accounts.AccountService;
import me.jaeuk.springrestapi.common.AppProperties;
import me.jaeuk.springrestapi.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp(){
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    // @TestDescription("정상적으로 등록되는 테스트") 만들 수도 있다.
    @DisplayName("정상적으로 등록되는 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development practice")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 9, 12, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 3 , 1, 12 , 10 , 8))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 11, 30 ,0))
                .endEventDateTime(LocalDateTime.of(2020, 4, 10, 11, 30 ,0))
                .location("Startup Factory")
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                //.andExpect(status().is(201)); 동일

                .andExpect(jsonPath("id").exists())

                //.andExpect(header().exists("Location"))
                //.andExpect(header().string("Content-Type", "application/hal+json"));
                // 좀더 type safe 한 코드
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))

                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                // 링크
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                // rest docs
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query event"),
                                linkWithRel("update-event").description("link to update an existing envet"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                            fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                            fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                            fieldWithPath("location").description("location of new event"),
                            fieldWithPath("basePrice").description("basePrice of new event"),
                            fieldWithPath("maxPrice").description("maxPrice of new event"),
                            fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content header")
                        ),
                        //relaxed prefix 붙이면 문서의 일부분만 검증 가능. 단점은 정확한 문서를 만들지 못한다는 것.
                        relaxedResponseFields(
//                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))

        ;
    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        // Given
        Account jaeuk = Account.builder()
                .email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(jaeuk);

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getCliendId(), appProperties.getClientSecret()))
                .param("username", appProperties.getAdminUsername())
                .param("password", appProperties.getAdminPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    };

    // 이런 방법도 있고, 무시하는 방법도 있다.(위의 경우)
    @Test
    @DisplayName("필요 없는 입력값이 있을 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development practice")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 9, 12, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 3 , 1, 12 , 10 , 8))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 11, 30 ,0))
                .endEventDateTime(LocalDateTime.of(2020, 4, 10, 11, 30 ,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("Startup Factory")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development practice")
                // eventValidator 에 걸림. (커스텀한 validator)
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 2, 9, 12, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 3 , 1, 12 , 10 , 8))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 11, 30 ,0))
                .endEventDateTime(LocalDateTime.of(2020, 4, 10, 11, 30 ,0))
                .location("Startup Factory")
                .basePrice(300)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();


        this.mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                // 에러시 최초화면, EventController에러를 던질때부터 index link 넣기
                .andExpect(jsonPath("_links.index").exists())
                ;
    }

    @Test
    @DisplayName("인증 권한으로 create-event 링크 여부")
    public void queryEventsWithAuthentication() throws Exception{
        // Given
        IntStream.range(0,30).forEach(this::generateEvent);

        // When
        ResultActions perform = this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        );

        // Then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        // Given
//        IntStream.range(0,30).forEach(i -> {
//            this.generateEvent(i);
//        });
        // 동일
        IntStream.range(0,30).forEach(this::generateEvent);

        // When
        ResultActions perform = this.mockMvc.perform(get("/api/events")
                                            .param("page", "1")
                                            .param("size", "10")
                                            .param("sort", "name,DESC")
        );

        // Then
        perform.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.first").exists())
                .andExpect(jsonPath("_links.prev").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.next").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }


    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        // Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").exists())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("get-an-event"))
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvene404() throws Exception{
        //When & Then
        this.mockMvc.perform(get("/api/event/0"))
                    .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정한 경우")
    public void updateEvent() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updateName = "Updated Event";
        eventDto.setName(updateName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(updateName))
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("update-an-event"))
                    // Spring rest docs 문서화 방법 createEvent() 참조
        ;
    }
    @Test
    @DisplayName("수정하려는 이벤트가 없는 경우 404 NOT_FOUND")
    public void updateEventNotFound() throws Exception {
        // Given
        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", "-99999")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
    @Test
    @DisplayName("입력 데이터(데이터 바인딩)가 이상한 경우/(비어있는 경우) 400 BAD_REQUEST")
    public void updateEventDataBindError() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    @Test
    @DisplayName("도메인 로직으로 데이터 검증 실패하면 400 BAD_REQUEST, 권한 충분하지 않은 경우 403 FORBIDDEN")
    public void updateEventDataLogicError() throws Exception{
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = new EventDto();
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("evemt " + i)
                .description("test event" +i)
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 9, 12, 11, 9))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 3 , 1, 12 , 10 , 8))
                .beginEventDateTime(LocalDateTime.of(2020, 2, 10, 11, 30 ,0))
                .endEventDateTime(LocalDateTime.of(2020, 4, 10, 11, 30 ,0))
                .location("Startup Factory")
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }


}
