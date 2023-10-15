package httpclient

import play.api.Logging
import play.api.libs.json.{JsValue, Reads}
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequestFilter, WSResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ExternalClient @Inject()(ws: WSClient)(implicit ec: ExecutionContext)
  extends AbstractHttpClient(ws) with Logging {

  private def BASE_URL = "https://thanh-doan-1.free.beeceptor.com"

  override protected def provideAuth: (String, String, WSAuthScheme) = (null, null, WSAuthScheme.BASIC)

  override protected def provideRequestFilters: Seq[WSRequestFilter] = Seq(AhcCurlRequestLogger())

  def get[A <: Object](apiPath: String,
                       params: Seq[(String, String)] = Nil,
                       extraHeaders: Seq[(String, String)] = Nil)(implicit reads: Reads[A]): Future[A] =
    handleResponseEntity(super.get(BASE_URL.concat(apiPath), params, extraHeaders))

  private def handleResponseEntity[A](response: Future[WSResponse])(implicit reads: Reads[A]): Future[A] = {
    response.map { response =>
      if (response.status >= 400) {
        logger.info("External service exception: " + response.body)
        throw new ExternalServiceException(response.body)
      } else {
        response.body[JsValue].as[A]
      }
    }
  }

}
