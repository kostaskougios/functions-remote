package ls

import ls.model.{LsOptions, LsResult}

/** Some comments //> exported
  */
trait LsFunctions:
  /** help */
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long
