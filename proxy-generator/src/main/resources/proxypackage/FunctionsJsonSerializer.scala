package {{proxypackage}}

import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
{{#imports}}
import {{.}}
{{/imports}}

class {{className}}:
  private def toJson[A](a: A, encoder: Encoder[A]) = encoder(a).noSpaces.getBytes("UTF-8")

  private def parseJson[A](data: Array[Byte],decoder: Decoder[A]):A =
    val j = new String(data,"UTF-8")
    parse(j).flatMap(decoder.decodeJson)
      .getOrElse(throw new IllegalArgumentException(s"Invalid json: $j"))
  {{#functions}}
  // ----------------------------------------------
  // Serializers for {{functionN}} function
  // ----------------------------------------------
  private val {{functionN}}Encoder = Encoder[{{caseClass}}]
  private val {{functionN}}Decoder = Decoder[{{caseClass}}]

  private val {{functionN}}ReturnTypeEncoder = Encoder[{{resultN}}]
  private val {{functionN}}ReturnTypeDecoder = Decoder[{{resultN}}]

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = toJson(value, {{functionN}}Encoder)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = parseJson(data, {{functionN}}Decoder)

  def {{functionN}}ReturnTypeSerializer(value: {{resultN}}): Array[Byte] = toJson(value, {{functionN}}ReturnTypeEncoder)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultN}} = parseJson(data, {{functionN}}ReturnTypeDecoder)

  {{/functions}}

