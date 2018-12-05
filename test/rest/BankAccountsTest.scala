package rest

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class BankAccountsTest extends PlaySpec with GuiceOneAppPerTest {

  "BankAccountsController" should {

    "render the bank accounts page" in {
      val request = FakeRequest(GET, "/postbankaccounts").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home = route(app, request).get

      contentAsString(home) must include ("{\"id\":\"4\",\"number\":\"\",\"name\":\"Expense claims\",\"creditcard\":false,\"synthetic\":true,\"balance\":0}")
    }

  }

}