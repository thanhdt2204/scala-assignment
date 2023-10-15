package exceptions

import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._

import javax.inject.Singleton
import scala.concurrent._

@Singleton
class CustomErrorHandler extends HttpErrorHandler {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(BadRequest("Client error: " + message))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case a: BadRequestException => Future.successful(BadRequest(a.message))
      case b: NotFoundException => Future.successful(NotFound(b.message))
      case _ => Future.successful(InternalServerError("A server error occurred: " + exception))
    }
  }
}

class BadRequestException(val message: String) extends Exception(message) {}

class NotFoundException(val message: String) extends Exception(message) {}
