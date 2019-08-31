package com.thaido.moviecatalogservice.controller;

import com.thaido.moviecatalogservice.model.CatalogItem;
import com.thaido.moviecatalogservice.model.Movie;
import com.thaido.moviecatalogservice.model.Rating;
import com.thaido.moviecatalogservice.model.UserRatings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog/")
public class MovieCatalogController {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private WebClient.Builder webClient;

  @RequestMapping("/{userId}")
  public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
    WebClient.Builder builder = WebClient.builder();

    UserRatings ratings = restTemplate.getForObject(
      "http://localhost:8082/rating/user/" + userId,
      UserRatings.class
    );

    return ratings.getUserRatings()
      .stream()
      .map(
        rating ->
        {
          Movie movie = restTemplate.getForObject(
            "http://localhost:8081/movies/" + rating.getMovieId(),
            Movie.class
          );
          return new CatalogItem(
            movie.getName(),
            "Test",
            rating.getRating()
          );
        }
      )
      .collect(Collectors.toList());
  }
}
