package repository

import model.Book

object BookRepository {
    private val books = mutableListOf<Book>()

    fun addBook(book: Book): Book {
        books.add(book)
        return book
    }

    fun getAllBooks(): List<Book> = books

    fun getBookById(id: String): Book? = books.find { it.id == id }

    fun updateBook(id: String, update: (Book) -> Unit): Boolean {
        val book = getBookById(id)
        return if (book != null) {
            update(book)
            true
        } else {
            false
        }
    }

    fun deleteBook(id: String): Boolean = books.removeIf { it.id == id }

    fun clear() = books.clear()
}
