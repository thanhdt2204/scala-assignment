package controllers.product

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.{Inject, Singleton}

@Singleton
class ProductRouter @Inject()(controller: ProductController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/") =>
      controller.getAll

    case GET(p"/$id") =>
      controller.getById(id.toLong)

    case POST(p"/") =>
      controller.save

    case PUT(p"/$id") =>
      controller.update(id.toLong)

    case DELETE(p"/$id") =>
      controller.delete(id.toLong)
  }

}
