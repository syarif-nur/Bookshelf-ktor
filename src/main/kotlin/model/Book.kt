package model

import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var year: Int,
    var author: String,
    var summary: String,
    var publisher: String,
    var pageCount: Int,
    var readPage: Int,
    var finished: Boolean = false,
    var reading: Boolean,
    var insertedAt: String = LocalDateTime.now().toString(),
    var updatedAt: String = LocalDateTime.now().toString()
)

@Serializable
data class BookListItem(
    val id: String,
    val name: String,
    val publisher: String
)

@Serializable
data class BooksResponse(
    val status: String,
    val data: BooksData
)

@Serializable
data class BooksData(
    val books: List<BookListItem>
)

@Serializable
data class BookRequest(
    val name: String? = null,
    val year: Int? = null,
    val author: String? = null,
    val summary: String? = null,
    val publisher: String? = null,
    val pageCount: Int? = null,
    val readPage: Int? = null,
    val reading: Boolean? = null
)

@Serializable
data class BookCreateSuccessResponse(
    val status: String = "success",
    val message: String = "Buku berhasil ditambahkan",
    val data: BookIdData
)

@Serializable
data class BookIdData(
    val bookId: String
)

@Serializable
data class BookErrorResponse(
    val status: String = "fail",
    val message: String
)

@Serializable
data class BookDetailResponse(
    val status: String = "success",
    val data: BookDetailData
)

@Serializable
data class BookDetailData(
    val book: Book
)
