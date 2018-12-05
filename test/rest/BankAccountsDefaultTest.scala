package rest

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class BankAccountsDefaultTest extends PlaySpec with GuiceOneAppPerTest {

  "BankAccountsController" should {

    "render the default page" in {
      val request = FakeRequest(GET, "/postbankaccounts/default").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home = route(app, request).get

      contentAsString(home) must include ("{\"id\":\"1\",\"number\":\"1357756\",\"name\":\"Personal account\",\"creditcard\":false,\"synthetic\":false,\"balance\":1202.14}")
    }

  }

}