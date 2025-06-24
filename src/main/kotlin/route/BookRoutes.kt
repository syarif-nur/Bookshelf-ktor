package route

import handler.*
import io.ktor.server.routing.*

fun Route.bookRoutes() {
    route("/books") {
        post { addBookHandler(call) }
        get { getAllBooksHandler(call) }
        route("/{id}") {
            get { getBookDetailHandler(call) }
            put { updateBookHandler(call) }
            delete { deleteBookHandler(call) }
        }
    }
}

