package `packagename`

// = imports

class `functionsCaller`(toByteArray: `functionsMethodParams` => Array[Byte], callFunction: Array[Byte] => Any):

  // foreach functions
  def `functionN`(`params`: String): `resultN` =
    val c  = `caseClass`(`params`)
    val r1 = toByteArray(c)
    val r2 = callFunction(r1)
    r2.asInstanceOf[`resultN`]
  // end functions
