package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.dao.OrderDao
import domain.models.{Order, OrderDetail, OrderResponse}
import exceptions.{BadRequestException, NotFoundException}
import play.api.Logger
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import services.{OrderDetailService, OrderService, ProductService, UserService}
import utils.auth.{JWTEnvironment, WithRole, WithRoleOwnUser}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class OrderDetailFormInput(productId: Long, quantity: Int)

case class OrderFormInput(userId: Long, orderDetails: Seq[OrderDetailFormInput])

class OrderController @Inject()(cc: ControllerComponents,
                                orderService: OrderService,
                                orderDetailService: OrderDetailService,
                                userService: UserService,
                                orderDao: OrderDao,
                                productService: ProductService,
                                silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[OrderFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "userId" -> longNumber,
        "orderDetails" -> seq(mapping(
          "productId" -> longNumber,
          "quantity" -> number
        )(OrderDetailFormInput.apply)(OrderDetailFormInput.unapply))
      )(OrderFormInput.apply)(OrderFormInput.unapply)
    )
  }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
      var userId: Option[Long] = None
      if ("User".equals(request.identity.role)) {
        userId = request.identity.id
      }
      orderService.findAllByUserId(userId).map {
        orders => {
          if (orders.isEmpty)
            NoContent
          else
            Ok(Json.toJson(orders.map(order => formatResponse(order))))
        }
      }
    }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin") || WithRoleOwnUser(orderDao, id)).async { implicit request =>
      orderService.findById(id).map {
        case Some(order) => Ok(Json.toJson(formatResponse(order)))
        case None => throw new NotFoundException("Order not found with id " + id)
      }
    }

  def save: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
      if ("User".equals(request.identity.role)) {
        processJson(None, request.identity.id)
      } else {
        processJson(None, None)
      }
    }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin") || WithRoleOwnUser(orderDao, id)).async { implicit request =>
      processJson(Some(id), None)
    }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin") || WithRoleOwnUser(orderDao, id)).async { implicit request =>
      // Delete order detail
      orderDetailService.deleteByOrderId(id)
      // Delete order
      orderService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1)
          Ok(JsString(s"Delete order id $id successfully"))
        else
          throw new BadRequestException("Unable to delete order " + id)
      }
    }

  private def processJson[A](id: Option[Long], currentUserId: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[OrderFormInput]): Nothing = {
      throw new BadRequestException(badForm.toString)
    }

    def success(input: OrderFormInput): Future[Result] = {
      // Get user id from request body or current user id
      var correctUserId = 0L
      if (currentUserId.isDefined) {
        correctUserId = currentUserId.get
      } else {
        correctUserId = input.userId
      }

      // Validate user
      val user = Await.result(userService.findById(correctUserId), Duration.Inf)
      if (user.isEmpty) throw new NotFoundException("User not found with id " + correctUserId)

      // Save order
      var order = Order(id, correctUserId, LocalDateTime.now(), 0)
      id match {
        case None => order = Await.result(orderService.save(order), Duration.Inf)
        case Some(_) => order = Await.result(orderService.update(order), Duration.Inf)
      }
      logger.info("ORDER ID: " + order.id)

      // Save order detail
      var orderDetails = List[OrderDetail]()
      var price: Float = 0
      for (x <- input.orderDetails.indices) {
        val item = input.orderDetails(x)
        val product = Await.result(productService.findById(item.productId), Duration.Inf)
        if (product.isEmpty) {
          orderService.delete(order.id.get)
          throw new NotFoundException("Product not found with id " + item.productId)
        }
        orderDetails = orderDetails.appended(OrderDetail(None, order.id.get, item.productId, product.get.price, item.quantity))
        price += product.get.price * item.quantity
      }
      if (id.isDefined) {
        orderDetailService.deleteByOrderId(order.id.get)
      }
      orderDetailService.saveAll(orderDetails)

      // Update total price for order
      order = Await.result(orderService.update(Order(order.id, order.userId, LocalDateTime.now(), price)), Duration.Inf)

      Future.successful(Ok(Json.toJson(order)))
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def formatResponse(order: Order): OrderResponse = {
    val user = Await.result(userService.findById(order.userId), Duration.Inf).get
    val orderDetails = Await.result(orderDetailService.findAllByOrderId(order.id.get), Duration.Inf)
    OrderResponse(order.id.get, user.firstName + " " + user.lastName, order.orderDate, order.totalPrice, orderDetails)
  }

}
