package pl.edu.pw.ee.cinemabackend.api.screenings.interfaces;

import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.pw.ee.cinemabackend.api.screenings.data.ScreeningRequest;
import pl.edu.pw.ee.cinemabackend.api.screenings.data.ScreeningResponse;
import pl.edu.pw.ee.cinemabackend.entities.user.User;

import java.time.LocalDate;
import java.util.List;


public interface ScreeningController {

    List<ScreeningResponse> getScreeningsForGivenDay(LocalDate date);

    List<ScreeningResponse> getScreeningsForMovie(long movieId);

    ScreeningResponse getScreeningDetails(long screeningId);

    ScreeningResponse createScreening(ScreeningRequest screeningRequest, User user);
}
