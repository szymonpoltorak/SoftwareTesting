package pl.edu.pw.ee.cinemabackend.jmh;

import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.pw.ee.cinemabackend.api.movies.data.MovieRequest;
import pl.edu.pw.ee.cinemabackend.api.movies.interfaces.MovieService;
import pl.edu.pw.ee.cinemabackend.entities.movie.Movie;
import pl.edu.pw.ee.cinemabackend.entities.movie.interfaces.MovieRepository;
import pl.edu.pw.ee.cinemabackend.entities.user.User;
import pl.edu.pw.ee.cinemabackend.entities.user.UserRole;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkMovieRunner extends AbstractBenchmark {
    private static MovieService movieService;
    private static MovieRepository movieRepository;
    private MovieRequest movieRequest;
    private User admin;
    private long id;

    @Autowired
    public final void setContext(MovieService movieService, MovieRepository movieRepository) {
        BenchmarkMovieRunner.movieService = movieService;
        BenchmarkMovieRunner.movieRepository = movieRepository;
    }

    @Setup(Level.Trial)
    public final void setupBenchmark() {
        movieRequest = MovieRequest.builder()
                .title("title")
                .description("bench")
                .minimalAge(12)
                .timeDuration("2")
                .build();

        admin = User.builder()
                .name("Jakub")
                .password("kicia.?312312312As")
                .username("emailpl@gmail.com")
                .surname("Wysocki")
                .userRole(UserRole.ADMIN)
                .build();

        if (movieRepository.findById(1L).isEmpty()) {
            Movie movie = Movie.builder()
                    .movieId(1L)
                    .title("title")
                    .description("description")
                    .timeDuration("15")
                    .minimalAge(15)
                    .build();
            movieRepository.save(movie);
        }

        this.id = 1L;

    }

    @Benchmark
    public final void createMovieBenchmark() {
        movieService.createMovie(movieRequest, admin);
    }

    @Benchmark
    public final void getListOfMoviesOnPageBenchmark() {
        movieService.getListOfMoviesOnPage(1);
    }

    @Benchmark
    public final void getMovieDetailsBenchmark() {
        movieService.getMovieDetails(id);
    }

    @Benchmark
    public final void updateMovieBenchmark() {
        movieService.updateMovie(movieRequest, id, admin);
    }

}
