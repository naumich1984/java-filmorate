package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public ResponseEntity<List<Feed>> getUserFeed(@PathVariable long id) {
        log.debug("GET /users/{id}/feed request");

        return ResponseEntity.ok(feedService.getUserFeed(id));
    }
}
