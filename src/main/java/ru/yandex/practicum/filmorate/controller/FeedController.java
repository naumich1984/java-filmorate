package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.FeedService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public ResponseEntity getUserFeed(@PathVariable long id) {
        log.debug("GET /users/{id}/feed request");

        return ResponseEntity.ok(feedService.getUserFeed(id));
    }
}
