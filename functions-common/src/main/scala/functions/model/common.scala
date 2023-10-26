package functions.model

import java.util.function.BiFunction

type TransportFunction    = (Coordinates3, Array[Byte]) => Array[Byte]
type TransportFunctionRaw = BiFunction[String, Array[Byte], Array[Byte]]
