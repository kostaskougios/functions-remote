package commands.ls

import commands.model.{LsOptions, LsResult}

/** The exported functions of ls module
  *
  * Mark it as exported: //> exported
  */
trait LsFunctions:
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long
