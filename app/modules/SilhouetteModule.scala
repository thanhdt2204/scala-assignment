package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, HTTPLayer, IDGenerator, PasswordHasherRegistry, PasswordInfo, PlayHTTPLayer}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import controllers.auth.{DefaultSilhouetteControllerComponents, SilhouetteControllerComponents}
import domain.dao.{DbExecutionContext, PasswordInfoDao, UserDao}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import services.UserService
import utils.auth.{CustomSecuredErrorHandler, CustomUnsecuredErrorHandler, JWTEnvironment}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.ClassTag

class SilhouetteModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[Silhouette[JWTEnvironment]].to[SilhouetteProvider[JWTEnvironment]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /**
   * Provides the HTTP layer implementation.
   */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
   * Provides the Silhouette environment.
   */
  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus): Environment[JWTEnvironment] = {
    Environment[JWTEnvironment](userService, authenticatorService, Seq(), eventBus)
  }

  /**
   * Provides the crypter for the authenticator.
   */
  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    new JcaCrypter(JcaCrypterSettings(configuration.underlying.getString("play.http.secret.key")))
  }

  /**
   * The AuthenticatorService that should be used to create and manage authenticators.
   */
  @Provides
  def provideAuthenticatorService(crypter: Crypter,
                                  idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[JWTAuthenticator] = {

    val encoder = new CrypterAuthenticatorEncoder(crypter)
    val headerName = configuration.underlying.getString("silhouette.authenticator.headerName")
    val issuerClaim = configuration.underlying.getString("silhouette.authenticator.issuerClaim")
    val authenticatorExpiry = configuration.underlying.getString("silhouette.authenticator.authenticatorExpiry")
    val sharedSecret = configuration.underlying.getString("silhouette.authenticator.sharedSecret")

    new JWTAuthenticatorService(JWTAuthenticatorSettings(
      fieldName = headerName,
      issuerClaim = issuerClaim,
      authenticatorExpiry = Duration(authenticatorExpiry).asInstanceOf[FiniteDuration],
      sharedSecret = sharedSecret
    ), None, encoder, idGenerator, clock)
  }

  /**
   * Provides auth info delegable auth info repository.
   */
  @Provides
  def providePasswordDAO(userDao: UserDao, dbExContext: DbExecutionContext): DelegableAuthInfoDAO[PasswordInfo] =
    new PasswordInfoDao(userDao)(ClassTag.apply(classOf[PasswordInfo]), dbExContext)

  /**
   * Provides the auth info repository.
   */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /**
   * Provides the password hasher registry.
   */
  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))
  }

  /**
   * Provides the credentials provider.
   */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  /**
   * Provides silhouette components
   */
  @Provides
  def providesSilhouetteComponents(components: DefaultSilhouetteControllerComponents): SilhouetteControllerComponents = {
    components
  }

}
