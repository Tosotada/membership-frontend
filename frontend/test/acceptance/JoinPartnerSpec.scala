package acceptance

import acceptance.util.{Config, Acceptance, TestUser, Util}
import org.scalatest.selenium.WebBrowser
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.slf4j.LoggerFactory

class JoinPartnerSpec extends FeatureSpec
  with WebBrowser with Util with GivenWhenThen with BeforeAndAfter with BeforeAndAfterAll  {

  def logger = LoggerFactory.getLogger(this.getClass)

  before { // Before each test
    resetDriver()
  }

  override def beforeAll() = {
    Config.printSummary()
  }

  override def afterAll() = {
    Config.driver.quit()
  }

  feature("Become a Partner in UK") {
    scenario("I join as Partner by clicking 'Become a Partner' button on Membership homepage", Acceptance) {
      val testUser = new TestUser

      Given("I clicked 'Become a Partner' button on Membership homepage")

      When("I land on 'Identity Register' page")
      val register = new pages.Register(testUser)
      go.to(register)
      assert(register.pageHasLoaded())

      And("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my Identity account")
      register.submit()

      Then("I should land back on Membership page")
      val membership = new pages.Membership
      assert(membership.pageHasLoaded())

      And("I should have Identity cookies")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(cookiesSet.map(_.getName).contains(idCookie)) }

      And("I should be logged in with my newly created account.")
      assert(membership.userDisplayName == testUser.username.toLowerCase)

      When("I click on 'Become a Partner' button")
      membership.becomePartner

      Then("I should land on 'Enter Details' page")
      val enterDetails = new pages.EnterDetails
      assert(enterDetails.pageHasLoaded())

      And("I should still be logged in with my Identity account.")
      assert(enterDetails.userDisplayName == testUser.username.toLowerCase)

      When("I fill in delivery address details")
      enterDetails.fillInDeliveryAddress()

      And("I fill in card details")
      enterDetails.fillInCardDetails()

      And("I click 'Pay' button")
      enterDetails.pay()

      Then("I should land on 'Thank You' page")
      val thankYou = new pages.ThankYou
      assert(thankYou.pageHasLoaded())

      And("I should be signed in as Partner.")
      assert(thankYou.userDisplayName == testUser.username.toLowerCase)
      assert(thankYou.userTier == "Partner")
    }
  }

  feature("Credit card failures when becoming a Partner in UK") {
    scenario("Charge is declined with a card_declined code", Acceptance) {
      val testUser = new TestUser

      Given("I clicked 'Become a Partner' button on Membership homepage")

      When("I land on 'Identity Register' page")
      val register = new pages.Register(testUser)
      go.to(register)
      assert(register.pageHasLoaded())

      And("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my Identity account")
      register.submit()

      Then("I should land back on Membership page")
      val membership = new pages.Membership
      assert(membership.pageHasLoaded())

      And("I should have Identity cookies")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(cookiesSet.map(_.getName).contains(idCookie)) }

      And("I should be logged in with my newly created account.")
      assert(membership.userDisplayName == testUser.username.toLowerCase)

      When("I click on 'Become a Partner' button")
      membership.becomePartner

      Then("I should land on 'Enter Details' page")
      val enterDetails = new pages.EnterDetails
      assert(enterDetails.pageHasLoaded())

      And("I should still be logged in with my Identity account.")
      assert(enterDetails.userDisplayName == testUser.username.toLowerCase)

      When("I fill in delivery address details")
      enterDetails.fillInDeliveryAddress()

      And("I fill in card details (card_declined)")
      enterDetails.fillInCardDeclined()

      And("I click 'Pay' button")
      enterDetails.pay()

      Then("the credit card should be declined")
      assert(pageHasElement(id("cc-cvc")))

      And("I remain on 'Enter Details' page")
      assert(currentUrl(Config.driver) == enterDetails.url)
    }

    scenario("Charge will be declined with an incorrect_cvc code", Acceptance) {
      val testUser = new TestUser

      Given("I clicked 'Become a Partner' button on Membership homepage")

      When("I land on 'Identity Register' page")
      val register = new pages.Register(testUser)
      go.to(register)
      assert(register.pageHasLoaded())

      And("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my Identity account")
      register.submit()

      Then("I should land back on Membership page")
      val membership = new pages.Membership
      assert(membership.pageHasLoaded())

      And("I should have Identity cookies")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(cookiesSet.map(_.getName).contains(idCookie)) }

      And("I should be logged in with my newly created account.")
      assert(membership.userDisplayName == testUser.username.toLowerCase)

      When("I click on 'Become a Partner' button")
      membership.becomePartner

      Then("I should land on 'Enter Details' page")
      val enterDetails = new pages.EnterDetails
      assert(enterDetails.pageHasLoaded())

      And("I should still be logged in with my Identity account.")
      assert(enterDetails.userDisplayName == testUser.username.toLowerCase)

      When("I fill in delivery address details")
      enterDetails.fillInDeliveryAddress()

      And("I fill in credit card payment details (incorrect_cvc)")
      enterDetails.fillInCardDeclinedCvc()

      And("I click 'Pay' button")
      enterDetails.pay()

      Then("the credit card should be declined")
      assert(pageHasElement(id("cc-cvc")))

      And("I remain on 'Enter Details' page")
      assert(currentUrl(Config.driver) == enterDetails.url)
    }

    scenario("Charge will be declined with an expired_card code.", Acceptance) {
      val testUser = new TestUser

      Given("I clicked 'Become a Partner' button on Membership homepage")

      When("I land on 'Identity Register' page")
      val register = new pages.Register(testUser)
      go.to(register)
      assert(register.pageHasLoaded())

      And("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my Identity account")
      register.submit()

      Then("I should land back on Membership page")
      val membership = new pages.Membership
      assert(membership.pageHasLoaded())

      And("I should have Identity cookies")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(cookiesSet.map(_.getName).contains(idCookie)) }

      And("I should be logged in with my newly created account.")
      assert(membership.userDisplayName == testUser.username.toLowerCase)

      When("I click on 'Become a Partner' button")
      membership.becomePartner

      Then("I should land on 'Enter Details' page")
      val enterDetails = new pages.EnterDetails
      assert(enterDetails.pageHasLoaded())

      And("I should still be logged in with my Identity account.")
      assert(enterDetails.userDisplayName == testUser.username.toLowerCase)

      When("I fill in delivery address details")
      enterDetails.fillInDeliveryAddress()

      And("I fill in credit card payment details (expired_card)")
      enterDetails.fillInCardDeclinedExpired()

      And("I click 'Pay' button")
      enterDetails.pay()

      Then("the credit card should be declined")
      assert(pageHasElement(id("cc-cvc")))

      And("I remain on 'Enter Details' page")
      assert(currentUrl(Config.driver) == enterDetails.url)
    }

    scenario("Charge will be declined with a processing_error code.", Acceptance) {
      val testUser = new TestUser

      Given("I clicked 'Become a Partner' button on Membership homepage")

      When("I land on 'Identity Register' page")
      val register = new pages.Register(testUser)
      go.to(register)
      assert(register.pageHasLoaded())

      And("I fill in personal details")
      register.fillInPersonalDetails()

      And("I submit the form to create my Identity account")
      register.submit()

      Then("I should land back on Membership page")
      val membership = new pages.Membership
      assert(membership.pageHasLoaded())

      And("I should have Identity cookies")
      Seq("GU_U", "SC_GU_U", "SC_GU_LA").foreach { idCookie =>
        assert(cookiesSet.map(_.getName).contains(idCookie)) }

      And("I should be logged in with my newly created account.")
      assert(membership.userDisplayName == testUser.username.toLowerCase)

      When("I click on 'Become a Partner' button")
      membership.becomePartner

      Then("I should land on 'Enter Details' page")
      val enterDetails = new pages.EnterDetails
      assert(enterDetails.pageHasLoaded())

      And("I should still be logged in with my Identity account.")
      assert(enterDetails.userDisplayName == testUser.username.toLowerCase)

      When("I fill in delivery address details")
      enterDetails.fillInDeliveryAddress()

      And("I fill in credit card payment details (processing_error)")
      enterDetails.fillInCardDeclinedProcessError()

      And("I click 'Pay' button")
      enterDetails.pay()

      Then("the credit card should be declined")
      assert(pageHasElement(id("cc-cvc")))

      And("I remain on 'Enter Details' page")
      assert(currentUrl(Config.driver) == enterDetails.url)
    }
  }
}

