package functions.helidon.transport

import functions.helidon.transport.exceptions.RequestFailedException
import functions.model.Serializer.{Avro, Json}
import functions.model.TransportInput
import io.helidon.http.{HeaderNames, Status}
import io.helidon.webclient.api.{HttpClientRequest, WebClient}

class HelidonTransport(client: WebClient):

  // WebClient:  https://helidon.io/docs/v4/#/se/webclient

  // override this to change the uri of a request
  protected def fullUri(input: TransportInput): String =
    val coordinates = input.coordinates4
    s"${coordinates.className}/${coordinates.method}/${coordinates.version}/${coordinates.serializer}"

  protected def args(input: TransportInput): String =
    if input.args.isEmpty then "" else "/" + input.args.mkString("/")

  /** override this to customize the http method
    */
  protected def method(in: TransportInput, uri: String): HttpClientRequest =
    in.coordinates4.properties.getOrElse("HTTP-METHOD", "PUT") match
      case "GET"     => client.get(uri)
      case "PUT"     => client.put(uri)
      case "POST"    => client.post(uri)
      case "HEAD"    => client.head(uri)
      case "DELETE"  => client.delete(uri)
      case "CONNECT" => ???
      case "OPTIONS" => client.options(uri)
      case "TRACE"   => client.trace(uri)
      case "PATCH"   => client.patch(uri)

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

    val u = fullUri(in) + args(in)
    val p = method(in, u)
    headers(p, in)
    val r = p.submit(in.data)
    try
      if r.status != Status.OK_200 then
        throw new RequestFailedException(r.status, s"Server responded with ${r.status()} for:\nuri = $u\ncoordinates = ${in.coordinates4}")
      val e = r.entity()
      if e.hasEntity then e.as(arrayOfBytes) else Array.emptyByteArray
    finally r.close()
