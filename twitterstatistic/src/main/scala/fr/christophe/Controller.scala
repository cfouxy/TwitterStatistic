package fr.christophe

import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import scala.util.{Failure, Success, Try}

class StatisticController @Inject()(statisticService: StatisticService)
  extends Controller with SwaggerSupport {

  override implicit protected val swagger = TwitterStatisticSwagger

  get(s"/statistic/first10", swagger { o =>
    o.summary("Get the first ten words the most popular")
      .description("Get the first ten words the most popular")
      .tag("statistic")
      .produces("application/json")
      .responseWith[Words](200, "Ok")
      .responseWith[Unit](404, "Resource Not Found")
      .responseWith[Unit](500, "Internal Server Error")
  }) {
    request: Request =>
      Try(statisticService.getFirst10) match {
        case Success(words) =>

          if (words.nonEmpty) {
            response.ok.body(words)
          } else {
            response.notFound
          }

        case Failure(e) =>
          response.internalServerError
      }
  }

}