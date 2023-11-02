package com.example.realtimeusage.repository;

import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.domain.QEvent;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends
        JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event>,
        QuerydslBinderCustomizer<QEvent> {

    @Override
    default void customize(QuerydslBindings bindings, QEvent root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.place.id, root.name, root.status, root.startDateTime, root.endDateTime);
        bindings.bind(root.place.id).as("placeId").first(SimpleExpression::eq);
        bindings.bind(root.name).as("name").first(StringExpression::containsIgnoreCase);
        bindings.bind(root.startDateTime).first(ComparableExpression::goe);
        bindings.bind(root.endDateTime).first(ComparableExpression::loe);
    }

    Page<Event> findByPlace(Place place, Pageable pageable);
}
