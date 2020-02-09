package me.jaeuk.springrestapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("spring rest api")
                .description("rest api developement with spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        Event event = new Event();
        String name = "Event";
        String dsecription = "Spring";

        event.setName(name);
        event.setDescription(dsecription);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(dsecription);
    }

}