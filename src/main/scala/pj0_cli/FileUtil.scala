package pj0_cli

import scala.io.{BufferedSource, Source}

object FileUtil {

  def getDiceRolls(filename: String): List[String] = {

    var openedFile: BufferedSource = null
    try {
      openedFile = Source.fromFile(filename)

      //return
      openedFile.getLines().mkString(",").split(",").toList
    } finally {
      if (openedFile != null) openedFile.close
    }
  }
}
