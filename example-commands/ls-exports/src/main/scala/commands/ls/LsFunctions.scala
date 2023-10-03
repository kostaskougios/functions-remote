package commands.ls

import commands.model.{LsOptions, LsResult}

/** The exported functions of ls module
  *
  * //> exported
  */
trait LsFunctions:
  /** help */
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long
