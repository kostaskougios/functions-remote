import ls.LsFunctions
import ls.model.{LsOptions, LsResult}

class LsFunctionsImpl extends LsFunctions:
  override def fileSize(path: String): Long = ???

  override def ls(path: String, lsOptions: LsOptions): LsResult =
    println(s"ls $path")
    ???
