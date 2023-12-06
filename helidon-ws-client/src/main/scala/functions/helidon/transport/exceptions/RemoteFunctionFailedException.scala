package functions.helidon.transport.exceptions

class RemoteFunctionFailedException(remoteStacktrace: String) extends RuntimeException(s"Remote function failed with this exception: $remoteStacktrace")
