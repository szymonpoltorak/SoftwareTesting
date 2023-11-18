package pl.edu.pw.ee.cinemabackend.api.movies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.edu.pw.ee.cinemabackend.api.movies.data.MovieResponse;
import pl.edu.pw.ee.cinemabackend.api.movies.interfaces.MovieService;
import pl.edu.pw.ee.cinemabackend.entities.movie.Movie;
import pl.edu.pw.ee.cinemabackend.entities.movie.interfaces.MovieMapper;
import pl.edu.pw.ee.cinemabackend.entities.movie.interfaces.MovieRepository;
import pl.edu.pw.ee.cinemabackend.exceptions.movies.MovieNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveServiceImpl implements MovieService {
    private static final int PAGE_SIZE = 20;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public final List<MovieResponse> getListOfMoviesOnPage(int numOfPage) {
        Pageable pageable = PageRequest.of(numOfPage, PAGE_SIZE);

        log.info("Finding movies on page: {} with size: {}", numOfPage, PAGE_SIZE);

        Page<Movie> movies = movieRepository.findAllByMoveId(pageable);

        log.info("Number of movies on page: {}", movies.getTotalElements());

        return movies
                .stream()
                .map(movieMapper::mapToMovieResponse)
                .toList();
    }

    @Override
    public final MovieResponse getMovieDetails(long movieId) {
        log.info("Finding movie with id: {}", movieId);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(
                        () -> new MovieNotFoundException(
                                String.format("Movie with id: %d not found", movieId),
                                HttpStatus.NOT_FOUND
                        )
                );
        log.info("Found movie {}", movie);

        return movieMapper.mapToMovieResponse(movie);
    }
}