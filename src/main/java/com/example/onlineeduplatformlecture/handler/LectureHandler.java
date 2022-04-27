package com.example.onlineeduplatformlecture.handler;

import com.example.onlineeduplatformlecture.model.Content;
import com.example.onlineeduplatformlecture.model.Enrolment;
import com.example.onlineeduplatformlecture.model.Lecture;
import com.example.onlineeduplatformlecture.model.Score;
import com.example.onlineeduplatformlecture.repository.ScoreRepository;
import com.example.onlineeduplatformlecture.repository.EnrolmentRepository;
import com.example.onlineeduplatformlecture.service.ContentService;
import com.example.onlineeduplatformlecture.service.EnrolmentService;
import com.example.onlineeduplatformlecture.service.LectureService;
import com.example.onlineeduplatformlecture.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class LectureHandler {

    private final LectureService lectureService;
    private final EnrolmentService enrolmentService;
    private final ContentService contentService;
    private final ScoreService scoreService;
    private final EnrolmentRepository enrolmentRepository;
  
    public LectureHandler(
            LectureService lectureService,
            EnrolmentService enrolmentService,
            ContentService contentService,
            ScoreService scoreService,
            EnrolmentRepository enrolmentRepository) {
        this.lectureService = lectureService;
        this.enrolmentService = enrolmentService;
        this.contentService = contentService;
        this.scoreService = scoreService;
        this.enrolmentRepository = enrolmentRepository;
    }

    @Autowired
    ScoreRepository scoreRepository;
    public Mono<ServerResponse> getLectureList(ServerRequest serverRequest){
        Flux<Lecture> lectureList = lectureService.getLectureList();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(lectureList, Lecture.class);
    }
    public Mono<ServerResponse> getLecture(ServerRequest serverRequest) {

        Long lectureId = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        System.out.println(lectureId);
        Mono<Lecture> lectureMono = lectureService.getLecture(lectureId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(lectureMono, Lecture.class)
                .switchIfEmpty(notFound);
    }

//    Mono<ServerResponse> createLecture(ServerRequest serverRequest);
//    Mono<ServerResponse> changeExposeLecture(ServerRequest serverRequest);
//
    public Mono<ServerResponse> enrollLecture(ServerRequest serverRequest){
        Mono<Enrolment> enrolmentMono = serverRequest.bodyToMono(Enrolment.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
                });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                enrolmentMono.flatMap(this.enrolmentRepository::save), Enrolment.class);
    }
//
//    Mono<ServerResponse> matchTeacher(ServerRequest serverRequest);
//
    public Mono<ServerResponse> getContentList(ServerRequest serverRequest){

        Long lectureId  = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Flux<Content> contentFlux = contentService.getContentList(lectureId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contentFlux, Content.class)
                .switchIfEmpty(notFound);
    }
//    Mono<ServerResponse> uploadContent(ServerRequest serverRequest);
    public Mono<ServerResponse> getContent(ServerRequest serverRequest){

        Long lectureId  = Long.parseLong(serverRequest.pathVariable("lectureId"));
        Long contentId  = Long.parseLong(serverRequest.pathVariable("contentId"));

        System.out.println(lectureId);
        System.out.println(contentId);
        Mono<Content> contentMono = contentService.getContent(lectureId,contentId);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contentMono, Content.class)
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("데이터 오류"));

    }

    public Mono<ServerResponse> getScore(ServerRequest serverRequest) {
        int lectureId = Integer.parseInt(serverRequest.pathVariable("lectureId"));
        Mono<Score> scoreMono = scoreRepository.findScoreById(lectureId,1);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        scoreMono.subscribe(System.out::println);
        return scoreMono
                .flatMap(score -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(scoreMono, Score.class))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> setScore(ServerRequest serverRequest) {
        Mono<Score> scoreMono = serverRequest.bodyToMono(Score.class)
                .onErrorResume(throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.error(new RuntimeException(throwable));
        });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                scoreMono.flatMap(this.scoreRepository::save), Score.class);
    }

    public Mono<ServerResponse> changeExposeLecture(ServerRequest serverRequest) {
//        Lecture paramLecture = serverRequest.bodyToMono(Lecture.class).block();
//        Lecture lecture = lectureRepository.getById((long) paramLecture.getLectureId());
//        lecture.setExposedYn(1);
//        lectureRepository.saveAndFlush(lecture);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Lecture(), Lecture.class)
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("데이터 오류"));
    }
//
//    Mono<ServerResponse> getScore(ServerRequest serverRequest);
//    Mono<ServerResponse> setScore(ServerRequest serverRequest);

//    public Mono<ServerResponse> getRatingList(ServerRequest serverRequest){
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(fromObject(ratingService.getRatingList()));
//    };
//
//    public Mono<ServerResponse> getRating(ServerRequest serverRequest){
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ratingService.getRating());
//    };
//    public Mono<ServerResponse> setRating(ServerRequest serverRequest){
//        Rating rating = serverRequest.bodyToMono(Rating.class).block();
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ratingService.saveRating(rating));
//    };

}
