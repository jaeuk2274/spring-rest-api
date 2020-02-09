package me.jaeuk.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired EventRepository eventRepository;

    @Test
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
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }


    // 이런 방법도 있고, 무시하는 방법도 있다.(위의 경우)
    @Test
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
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
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
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();


        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

}
