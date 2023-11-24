package functions.helidon.transport

import functions.helidon.transport.exceptions.RequestFailedException
import functions.model.Serializer.{Avro, Json}
import functions.model.TransportInput
import io.helidon.http.{HeaderNames, Status}
import io.helidon.webclient.api.{HttpClientRequest, WebClient}

class HelidonTransport(client: WebClient):

  // override this to change the uri of a request
  protected def fullUri(input: TransportInput): String =
    val coordinates = input.coordinates4
    s"${coordinates.className}/${coordinates.method}/${coordinates.version}/${coordinates.serializer}"

  protected def args(input: TransportInput): String =
    if input.args.isEmpty then "" else "/" + input.args.mkString("/")

  /** override this to customize the http method
    */
  protected def method(in: TransportInput): HttpClientRequest =
    in.coordinates4.properties.getOrElse("HTTP-METHOD", "PUT") match
      case "GET"     => client.get()
      case "PUT"     => client.put()
      case "POST"    => client.post()
      case "HEAD"    => client.head()
      case "DELETE"  => client.delete()
      case "CONNECT" => ???
      case "OPTIONS" => client.options()
      case "TRACE"   => client.trace()
      case "PATCH"   => client.patch()

  private val arrayOfBytes = classOf[Array[Byte]]

  /** Override this to customize http headers
    */
  protected def headers(req: HttpClientRequest, in: TransportInput): Unit =
    in.coordinates4.serializer match
      case Avro => req.header(HeaderNames.CONTENT_TYPE, "application/avro")
      case Json => req.header(HeaderNames.CONTENT_TYPE, "application/json")

  def transportFunction(in: TransportInput): Array[Byte] =
    if in.argsData.nonEmpty then
      throw new IllegalArgumentException("argsData has serialized data, did you use the correct helidon factory methods for the caller?")
    val m = method(in)
    val u = fullUri(in) + args(in)
    val p = m.path(u)
    headers(p, in)
    val r = p.submit(in.data)
    try
      if r.status != Status.OK_200 then
        throw new RequestFailedException(r.status, s"Server responded with ${r.status()} for:\nuri = $u\ncoordinates = ${in.coordinates4}")
      val e = r.entity()
      if e.hasEntity then e.as(arrayOfBytes) else Array.emptyByteArray
    finally r.close()
