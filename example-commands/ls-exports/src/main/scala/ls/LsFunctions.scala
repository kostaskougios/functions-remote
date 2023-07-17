package ls

import ls.model.{LsOptions, LsResult}

trait LsFunctions:
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long
