package functions.model

import java.util.function.BiFunction

type TransportFunction    = TransportInput => Array[Byte]
type TransportFunctionRaw = BiFunction[String, Array[Byte], Array[Byte]]
