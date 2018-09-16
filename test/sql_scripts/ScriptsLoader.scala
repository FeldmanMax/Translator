package sql_scripts
import java.io.File

import doobie.util.fragment
import doobie.util.query.Query
import utils.database.DBActions.FragmentFunction

import scala.io.Source
import scala.language.postfixOps
import utils.errors.NotFound

object ScriptsLoader {

  private val currentPath: String = new File(".").getCanonicalPath

  private val scripts: Set[(String, String)] = Set(
    "create_translator_language.sql"          -> "drop_translator_language.sql",
    "create_translator_translations_key.sql"  -> "drop_translator_translations_key.sql"
  )

  def creators(dbName: String): Set[FragmentFunction] = {
    scripts.map { case (scriptLocation, _) =>
      creators(dbName, scriptLocation)
    }
  }

  def creators(dbName: String, script: String): FragmentFunction = {
    getFragmentFunction(dbName, script)
  }

  def getFragmentFunction(dbName: String, script: String): FragmentFunction = {
    () => {
      getSql(script) match {
        case None       =>  Left(NotFound(script))
        case Some(sql)  =>  Right(getFragment(sql mkString "\n|", dbName))
      }
    }
  }

  private def getFragment(sqlCommands: String, dbName: String): fragment.Fragment = {
    val sql = s"""|${sqlCommands.replace("[DATABASE_NAME]", dbName)}""".stripMargin
    Query(sql).toFragment()
  }

  private def getSql(script: String): Option[Iterator[String]] = {
    scripts.find { case (creator, distructor) => creator == script || distructor == script}
            .map { _ =>
              val path: String = currentPath + s"/test/sql_scripts/$script"
              Source.fromFile(path)("UTF-8").getLines()
            }
  }
}
