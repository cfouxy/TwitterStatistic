package fr.christophe

import com.github.racc.tscg.TypesafeConfigModule
import com.github.xiaodongw.swagger.finatra.SwaggerController
import com.google.inject.Module
import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.inject.{Logging, TwitterModule}
import com.typesafe.config.ConfigFactory
import io.swagger.models.{Info, Swagger}

object PostgresModule extends TwitterModule {

  val postgresHost: Flag[String] = flag(name = "postgres.host", default = "localhost", help = "The Postgres host")
  val postgresPort: Flag[Int] = flag(name = "postgres.port", default = 5432, help = "The Postgres port")
  val postgresUsername: Flag[String] = flag(name = "postgres.username", default = "", help = "The Postgres username")
  val postgresPassword: Flag[String] = flag(name = "postgres.password", default = "", help = "The Postgres password")
  val postgresDatabase: Flag[String] = flag(name = "postgres.database", default = "", help = "The Postgres database")
  val postgresSchema: Flag[String] = flag(name = "postgres.schema", default = "", help = "The Postgres schema")

/*
  @Singleton
  @Provides
  def providesPostgresClient: PostgresJdbcClient = {
    PostgresJdbcClient("org.postgresql.Driver",s"jdbc:postgresql://${postgresHost()}:${postgresPort()}/${postgresDatabase()}",postgresUsername(), postgresPassword())
  }
*/

}

object ConfigModule extends TwitterModule with Logging {
  override def configure() = {
    val config = ConfigFactory.load()
    install(TypesafeConfigModule.fromConfig(config))
  }
}

object TwitterStatisticSwagger extends Swagger

object StatisticApp extends StatisticServer

class StatisticServer extends HttpServer {

  override val modules: Seq[Module] = super.modules :+ PostgresModule
  val info: Info = new Info()
    .description("Statistic API")
    .version("1.0.0")
    .title("Statistic API")


  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[StatisticController]
      .add(new SwaggerController(swagger = TwitterStatisticSwagger))
  }

  override protected def defaultFinatraHttpPort: String = ":8889"
}