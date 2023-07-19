package `packagename`

// = imports

class `functionsCaller`(toByteArray: `functionsMethodParams` => Array[Byte], callFunction: Array[Byte] => Any):

  // foreach functions
  def `functionN`(`params`: String): `ResultN` =
    val c  = `CaseClass`(`params`)
    val r1 = toByteArray(c)
    val r2 = callFunction(r1)
    r2.asInstanceOf[`ResultN`]
  // end functions
