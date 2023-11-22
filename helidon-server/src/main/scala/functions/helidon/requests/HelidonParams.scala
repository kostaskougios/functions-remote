package functions.helidon.requests

import io.helidon.common.parameters.Parameters

class HelidonParams(params: Parameters):
  def get[A](name: String)(using converter: ParamConverter[A]): A =
    val v = params.get(name)
    converter(v)
