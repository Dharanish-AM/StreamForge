package modules

import com.google.inject.AbstractModule

class FlywayModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[FlywayMigrationRunner]).asEagerSingleton()
  }
}
