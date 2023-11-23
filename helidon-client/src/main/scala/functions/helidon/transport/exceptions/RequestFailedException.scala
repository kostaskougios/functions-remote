package functions.helidon.transport.exceptions

import io.helidon.http.Status

class RequestFailedException(status: Status, msg: String) extends RuntimeException(msg)
