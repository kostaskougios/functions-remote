package `packagename`

class FunctionsCaller(toByteArray: `FunctionsMethodParams` => Array[Byte], callFunction: Array[Byte] => Any):

  // = foreach functions
  def `functionN`(path: String): `ResultN` =
    val c  = `CaseClass`(path)
    val r1 = toByteArray(c)
    val r2 = callFunction(r1)
    r2.asInstanceOf[`ResultN`]
  // = end functions
