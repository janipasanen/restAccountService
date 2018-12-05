import javax.inject._

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

import v1.bank._
import bankaccounts._
import v1.post._
import v1.post2._

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure() = {
    //bind[BankRepository].to[BankRepositoryImpl].in[Singleton]
    bind[BankAccountsRepository].to[BankAccountsRepositoryImpl].in[Singleton]
    //bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
    //bind[PostRepository2].to[PostRepository2Impl].in[Singleton]
    // bind[PostRepository2].to[PostRepository2Impl].in[Singleton]
  }
}
