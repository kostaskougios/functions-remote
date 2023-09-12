package {{proxypackage}}

{{#imports}}
import {{.}}
{{/imports}}

class {{functionsReceiver}}(
    fromLs: Array[Byte] => LsFunctionsMethods.Ls,
    lsResponse: LsResult => Array[Byte],
    fromFileSize: Array[Byte] => LsFunctionsMethods.FileSize,
    f: LsFunctions
):
  def invoke(method: LsFunctionsMethods.Methods, data: Array[Byte]): Array[Byte] =
    method match
      case LsFunctionsMethods.Methods.Ls => ls(data)

  def ls(data: Array[Byte]): Array[Byte] =
    val params = fromLs(data)
    val r      = f.ls(params.path, params.lsOptions)
    lsResponse(r)
