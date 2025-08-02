package com.jobpulse.auth_service.service.module.event.broker.model;

import java.util.List;

import com.jobpulse.auth_service.model.event.DomainEventInterface;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
@AllArgsConstructor
public class PublishEventsCommand<E, P> {
    private final List<DomainEventInterface<E, P>> events;
}
