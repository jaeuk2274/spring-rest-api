package me.jaeuk.springrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        // 무제한인 경우 base 100, max 0 가능. 그런데 max가 0이 아니면.
        if (eventDto.getBasePrice() > eventDto.getMaxPrice()
                && eventDto.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong");
        }

        LocalDateTime endEventDataTime = eventDto.getEndEventDateTime();
        if (endEventDataTime.isBefore(eventDto.getBeginEventDateTime())
                || endEventDataTime.isBefore(eventDto.getCloseEnrollmentDateTime())
                || endEventDataTime.isBefore(eventDto.getBeginEnrollmentDateTime()))
        {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }
}
