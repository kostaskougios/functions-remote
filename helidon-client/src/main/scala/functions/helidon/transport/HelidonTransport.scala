package functions.helidon.transport

import functions.model.{Coordinates4, TransportInput}
import io.helidon.webclient.api.{HttpClientRequest, WebClient}

class HelidonTransport(client: WebClient):

  // override this to change the uri of a request
  protected def fullUri(input: TransportInput): String =
    val coordinates = input.coordinates4
    s"${coordinates.className}/${coordinates.method}/${coordinates.version}/${coordinates.serializer}"

  protected def method(coords: Coordinates4): HttpClientRequest =
    coords.properties.getOrElse("HTTP-METHOD", "PUT") match
      case "GET"     => client.get()
      case "PUT"     => client.put()
      case "POST"    => client.post()
      case "HEAD"    => client.head()
      case "DELETE"  => client.delete()
      case "CONNECT" => ???
      case "OPTIONS" => client.options()
      case "TRACE"   => client.trace()
      case "PATCH"   => client.patch()

  def transportFunction(in: TransportInput): Array[Byte] =
    val m = method(in.coordinates4)
    val u = fullUri(in)
    val r = m.path(u).request(classOf[Array[Byte]])
    try
      r.entity()
    finally r.close()
