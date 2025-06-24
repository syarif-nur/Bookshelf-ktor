package handler

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import model.Book
import model.BookListItem
import model.BooksData
import model.BooksResponse
import repository.BookRepository
import java.time.LocalDateTime

suspend fun addBookHandler(call: ApplicationCall) {
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    val request = try {
        call.receive<model.BookRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Invalid request body"))
        return
    }

    val name = request.name
    val year = request.year
    val author = request.author
    val summary = request.summary
    val publisher = request.publisher
    val pageCount = request.pageCount
    val readPage = request.readPage
    val reading = request.reading

    if (name.isNullOrBlank()) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Gagal menambahkan buku. Mohon isi nama buku"))
        return
    }
    if (pageCount == null || readPage == null || readPage > pageCount) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Gagal menambahkan buku. readPage tidak boleh lebih besar dari pageCount"))
        return
    }
    if (reading == null) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Invalid request body"))
        return
    }

    val finished = readPage == pageCount
    val now = LocalDateTime.now().toString()
    val book = Book(
        name = name,
        year = year ?: 0,
        author = author ?: "",
        summary = summary ?: "",
        publisher = publisher ?: "",
        pageCount = pageCount,
        readPage = readPage,
        finished = finished,
        reading = reading,
        insertedAt = now,
        updatedAt = now
    )
    BookRepository.addBook(book)
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    call.respond(HttpStatusCode.Created, model.BookCreateSuccessResponse(data = model.BookIdData(bookId = book.id)))
}

suspend fun getAllBooksHandler(call: ApplicationCall) {
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    val query = call.request.queryParameters
    val nameQuery = query["name"]?.lowercase()
    val readingQuery = query["reading"]
    val finishedQuery = query["finished"]

    var books = BookRepository.getAllBooks()
    if (nameQuery != null) {
        books = books.filter { it.name.lowercase().contains(nameQuery) }
    }
    if (readingQuery != null) {
        val reading = readingQuery == "1"
        books = books.filter { it.reading == reading }
    }
    if (finishedQuery != null) {
        val finished = finishedQuery == "1"
        books = books.filter { it.finished == finished }
    }
    val result = books.map { BookListItem(
        id = it.id,
        name = it.name,
        publisher = it.publisher
    ) }
    call.respond(
        BooksResponse(
            status = "success",
            data = BooksData(books = result)
        )
    )
}

suspend fun getBookDetailHandler(call: ApplicationCall) {
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    val id = call.parameters["id"]
    val book = id?.let { BookRepository.getBookById(it) }
    if (book == null) {
        call.respond(HttpStatusCode.NotFound, model.BookErrorResponse(message = "Buku tidak ditemukan"))
        return
    }
    call.respond(HttpStatusCode.OK, model.BookDetailResponse(data = model.BookDetailData(book = book)))
}

suspend fun updateBookHandler(call: ApplicationCall) {
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    val id = call.parameters["id"]
    val request = try {
        call.receive<model.BookRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Invalid request body"))
        return
    }
    val name = request.name
    val year = request.year
    val author = request.author
    val summary = request.summary
    val publisher = request.publisher
    val pageCount = request.pageCount
    val readPage = request.readPage
    val reading = request.reading

    if (name.isNullOrBlank()) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Gagal memperbarui buku. Mohon isi nama buku"))
        return
    }
    if (pageCount == null || readPage == null || readPage > pageCount) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Gagal memperbarui buku. readPage tidak boleh lebih besar dari pageCount"))
        return
    }
    if (reading == null) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.BadRequest, model.BookErrorResponse(message = "Invalid request body"))
        return
    }
    val updated = id?.let {
        BookRepository.updateBook(it) { book ->
            book.name = name
            book.year = year ?: 0
            book.author = author ?: ""
            book.summary = summary ?: ""
            book.publisher = publisher ?: ""
            book.pageCount = pageCount
            book.readPage = readPage
            book.finished = readPage == pageCount
            book.reading = reading
            book.updatedAt = LocalDateTime.now().toString()
        }
    } ?: false
    if (!updated) {
        call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
        call.respond(HttpStatusCode.NotFound, model.BookErrorResponse(message = "Gagal memperbarui buku. Id tidak ditemukan"))
        return
    }
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    call.respond(HttpStatusCode.OK, mapOf(
        "status" to "success",
        "message" to "Buku berhasil diperbarui"
    ))
}

suspend fun deleteBookHandler(call: ApplicationCall) {
    call.response.headers.append(HttpHeaders.ContentType, "application/json; charset=utf-8", safeOnly = false)
    val id = call.parameters["id"]
    val deleted = id?.let { BookRepository.deleteBook(it) } ?: false
    if (!deleted) {
        call.respond(HttpStatusCode.NotFound, mapOf(
            "status" to "fail",
            "message" to "Buku gagal dihapus. Id tidak ditemukan"
        ))
        return
    }
    call.respond(HttpStatusCode.OK, mapOf(
        "status" to "success",
        "message" to "Buku berhasil dihapus"
    ))
}
