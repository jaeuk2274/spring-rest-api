package me.jaeuk.springrestapi.events;

import junitparams.JUnitParamsRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnitParamsRunner.class)
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


    // Junit 5 기준
    @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
    /*@CsvSource({
            "0, 0, true",
            "100, 0, falae",
            "0, 1000, falae"
    })*/
    @MethodSource("testFreeObject")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        System.out.println(basePrice);
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        //When
        event.update();
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    // static 있어야 동작
    private static Object[] testFreeObject() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false}
        };
    }


    @Test
    public void testOffline(){
        // Given
        Event event = Event.builder()
                .location("네이버 D2 스타트업 팩토리")
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();

        // Given
        event = Event.builder()
                .build();

        //When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse();
    }
}