package no.skytteren.mail

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll

class MailSenderSpec extends FunSpec with GivenWhenThen with BeforeAndAfterAll{

	val greenMail = new GreenMail(new ServerSetup(10587, null, ServerSetup.PROTOCOL_SMTP))
	greenMail.setUser("mailer", "s3cr3t")
  greenMail.start();
	
	override def afterAll(){
		greenMail.stop()
	}
	
	describe("TLSMailSender") {
		val sender = new TLSMailSender(host = "localhost", port = 10587, mailer = "TestMailer", username = "mailer", password = "s3cr3t")
		it("should be able to send mail to tls smtp server") {
			Given("An email")
			val email = EmailMessage(
					to = List(EmailAddress("test tested <test.tested@somewhere.com>")),
					from = EmailAddress("from someone <from@someone.com>"),
					subject = "Test subject",
					//altText: String,
					html = <html><body><h1>HELLO</h1></body></html>
			)
			When("sending mail by the tls mail client")
			sender.send(email)
			Then("should one should recieve mail")
			assert("Test subject" === greenMail.getReceivedMessages()(0).getSubject())
		}
	}

}