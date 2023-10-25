package functions.model

import java.util.function.BiFunction

type TransportFunction    = (Coordinates2, Array[Byte]) => Array[Byte]
type TransportFunctionRaw = BiFunction[String, Array[Byte], Array[Byte]]
