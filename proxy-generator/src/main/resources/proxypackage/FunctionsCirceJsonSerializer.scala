package {{proxypackage}}

import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
{{#imports}}
import {{.}}
{{/imports}}

class {{className}}:
  protected def toJson[A](a: A, encoder: Encoder[A]): Array[Byte] = encoder(a).noSpaces.getBytes("UTF-8")

  protected def parseJson[A](data: Array[Byte], decoder: Decoder[A]): A =
    val j = new String(data,"UTF-8")
    parse(j).flatMap(decoder.decodeJson)
      .getOrElse(throw new IllegalArgumentException(s"Invalid json: $j"))
  {{#functions}}
  // ----------------------------------------------
  // Serializers for {{functionN}} function
  // ----------------------------------------------
  val {{functionN}}Encoder = Encoder[{{caseClass}}]
  val {{functionN}}Decoder = Decoder[{{caseClass}}]
  {{^firstParamsRaw.isEmpty}}
  val {{functionN}}ArgsEncoder = Encoder[{{caseClass}}Args]
  val {{functionN}}ArgsDecoder = Decoder[{{caseClass}}Args]
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  val {{functionN}}ReturnTypeEncoder = Encoder[{{resultNNoFramework}}]
  val {{functionN}}ReturnTypeDecoder = Decoder[{{resultNNoFramework}}]
  {{/isUnitReturnType}}

  def {{functionN}}Serializer(value: {{caseClass}}): Array[Byte] = toJson(value, {{functionN}}Encoder)
  def {{functionN}}Deserializer(data: Array[Byte]): {{caseClass}} = parseJson(data, {{functionN}}Decoder)
  {{^firstParamsRaw.isEmpty}}
  def {{functionN}}ArgsSerializer(value: {{caseClass}}Args): Array[Byte] = toJson(value, {{functionN}}ArgsEncoder)
  def {{functionN}}ArgsDeserializer(data: Array[Byte]): {{caseClass}}Args = parseJson(data, {{functionN}}ArgsDecoder)
  {{/firstParamsRaw.isEmpty}}

  {{^isUnitReturnType}}
  def {{functionN}}ReturnTypeSerializer(value: {{resultNNoFramework}}): Array[Byte] = toJson(value, {{functionN}}ReturnTypeEncoder)
  def {{functionN}}ReturnTypeDeserializer(data: Array[Byte]): {{resultNNoFramework}} = parseJson(data, {{functionN}}ReturnTypeDecoder)
  {{/isUnitReturnType}}
  {{/functions}}

