package me.jaeuk.springrestapi.events;

import lombok.RequiredArgsConstructor;
import me.jaeuk.springrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){ // id 가 들어있던 무시하고, dto에 있는 부분만 받아옴.
        if(errors.hasErrors()){
            System.out.println(errors);
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            System.out.println(errors);
            return badRequest(errors);
        }

        // eventDto 를 event로 옮겨주는 Model Mapper
        // 리플렉션을 사용해서 그냥 set/bulder 보다 속도가 느릴 순 있으나... 염려가 된다면..
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);

        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkTo(EventController.class).slash(newEvent.getId()).withRel("update-event"));
        eventResource.add(new Link("docs/index.html#resources-index").withRel("profile"));
        // EventResource 안으로 이동
        //eventResource.add(linkTo(EventController.class).slash(newEvent.getId()).withSelfRel());


        return ResponseEntity.created(createdUri).body(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
