package modules

import javax.inject.Inject
import javax.inject.Singleton
import org.flywaydb.core.Flyway
import play.api.Configuration

@Singleton
class FlywayMigrationRunner @Inject() (config: Configuration) {
  private val enabled = config.getOptional[Boolean]("flyway.enabled").getOrElse(true)

  if (enabled) {
    val locations = config.getOptional[Seq[String]]("flyway.locations").getOrElse(Seq("classpath:db/migration"))
    val flyway = Flyway
      .configure()
      .dataSource(
        config.get[String]("flyway.url"),
        config.get[String]("flyway.user"),
        config.get[String]("flyway.password")
      )
      .locations(locations: _*)
      .baselineOnMigrate(config.getOptional[Boolean]("flyway.baselineOnMigrate").getOrElse(false))
      .load()

    flyway.migrate()
  }
}
