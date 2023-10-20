package pl.edu.pw.ee.library.api.book.interfaces;

import org.mapstruct.Mapper;
import pl.edu.pw.ee.library.api.book.data.BookResponse;
import pl.edu.pw.ee.library.entities.book.Book;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toBookResponse(Book book);

    List<BookResponse> toBookResponseList(List<Book> list);
}
