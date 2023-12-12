package functions.model

type InvokerMap = Map[Coordinates4, ReceiverInput => Array[Byte]]

import java.util.function.BiFunction

type TransportFunction    = TransportInput => Array[Byte]
type TransportFunctionRaw = BiFunction[String, Array[Byte], Array[Byte]]
