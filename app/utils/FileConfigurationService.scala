package utils

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigurationService {
  def get: ConfigData
}

object FileConfigurationService extends ConfigurationService {

  private val config: Config =  ConfigFactory.load()

  def get: ConfigData = {
    ConfigData(loadDBConfig)
  }

  private def loadDBConfig: DBConfig = {
    DBConfig(
      driver    = config.getString("db.default.driver"),
      url       = config.getString("db.default.url"),
      user      = config.getString("db.default.username"),
      password  = config.getString("db.default.password"),
      dbName    = config.getString("db.default.dbName")
    )
  }

}

final case class ConfigData(
  dbConfig: DBConfig
)

final case class DBConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  dbName: String
) {
  val fullUrl: String = url + "/" + dbName
}
