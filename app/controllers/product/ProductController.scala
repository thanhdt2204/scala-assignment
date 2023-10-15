package controllers.product

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Product
import exceptions.{BadRequestException, NotFoundException}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import services.{ExternalProductService, ProductService}
import utils.auth.{JWTEnvironment, WithRole}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success, Try}

case class ProductFormInput(productName: String, price: Long)

class ProductController @Inject()(cc: ControllerComponents,
                                  productService: ProductService,
                                  externalService: ExternalProductService,
                                  silhouette: Silhouette[JWTEnvironment])
                                 (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val form: Form[ProductFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "productName" -> nonEmptyText(maxLength = 128),
        "price" -> longNumber
      )(ProductFormInput.apply)(ProductFormInput.unapply)
    )
  }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator", "User")).async { implicit request =>
      productService.findAll().map {
        products => {
          if (products.isEmpty)
            NoContent
          else
            Ok(Json.toJson(products))
        }
      }
    }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator", "User")).async { implicit request =>
      productService.findById(id).map {
        case Some(product) => Ok(Json.toJson(product))
        case None => throw new NotFoundException("Product not found with id " + id)
      }
    }

  def save: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      form.bindFromRequest.fold(
        errors => throw new BadRequestException(errors.toString),
        data => {
          val product = Product(None, data.productName, data.price, LocalDateTime.now().plusDays(10))
          productService.save(product).map { product =>
            Created(Json.toJson(product))
          }
        }
      )
    }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      form.bindFromRequest.fold(
        errors => throw new BadRequestException(errors.toString),
        data => {
          val product = Product(Some(id), data.productName, data.price, LocalDateTime.now().plusDays(10))
          productService.update(product).map { product =>
            Ok(Json.toJson(product))
          }
        }
      )
    }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      productService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1)
          Ok(JsString(s"Delete product id $id successfully"))
        else
          throw new BadRequestException("Unable to delete product " + id)
      }
    }

  def getProductExternals: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      externalService.findAll().transform {
        case Failure(exception) =>
          Try(BadRequest(exception.getMessage))
        case Success(products) =>
          val insertedProducts = Await.result(productService.saveAll(products), Duration.Inf)
          Try(Ok(Json.toJson(insertedProducts)))
      }
    }

}
