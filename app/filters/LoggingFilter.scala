package filters

import com.google.inject.Inject
import akka.stream.Materializer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.mvc._

class LoggingFilter @Inject() (implicit val mat: Materializer) extends Filter {

  private val accessLogger: Logger = Logger(getClass)

  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis
    val resultFuture = next(request)

    resultFuture.foreach(result => {
      val endTime     = System.currentTimeMillis
      val requestTime = endTime - startTime
      val msg = s"method=${request.method} uri=${request.uri} took ${requestTime}ms with status=${result.header.status}"
      accessLogger.info(msg)
    })
    resultFuture
  }
}
