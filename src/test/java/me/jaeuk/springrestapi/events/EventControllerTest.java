package me.jaeuk.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jaeuk.springrestapi.common.RestDocsConfiguration;
import me.jaeuk.springrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 등록되는 테스트")
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

        mockMvc.perform(post("/events")
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
                        // relaxedResponseFields(
                        responseFields(
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

        mockMvc.perform(post("/events")
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


        this.mockMvc.perform(post("/events")
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

}
