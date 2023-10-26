package functions.utils

object ClassLoaderUtils:
  def withThreadContextClassLoader[R](cl: ClassLoader)(f: => R): R =
    val tcl = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(cl)
    try f
    finally Thread.currentThread().setContextClassLoader(tcl)
