package ls
import ls.model.LsOptions

class UnimplementedLsFunctions extends LsFunctions:
  override def ls(path: String, lsOptions: LsOptions) = ???
  override def fileSize(path: String)                 = ???
