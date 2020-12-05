package me.jaeuk.springrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
/*
ResourceSupport changed to RepresentationModel
Resource changed to EntityModel
Resources changed to CollectionModel
PagedResources changed to PagedModel
ResourceAssembler changed to RepresentationModelAssembler
 */
public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links){
        super(event, links);
        //add(new Link("http://localhost:8080/api/event/"+event.getId()))
        // 좀 더 타입세이프한 코드.
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
