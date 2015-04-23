package no.skytteren.mail

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import javax.mail.internet.MimeMultipart
import scala.util.Success

class MailSenderSpec extends FunSpec with GivenWhenThen with BeforeAndAfterAll{

	val greenMail = new GreenMail(new ServerSetup(10587, null, ServerSetup.PROTOCOL_SMTP))
	greenMail.setUser("mailer", "s3cr3t")
  greenMail.start();
	
	override def afterAll(): Unit = {
		greenMail.stop()
	}
	describe("TLSMailSender") {
		val sender = new TLSMailSender(host = "localhost", port = 10587, mailer = "TestMailer", username = "mailer", password = "s3cr3t")
		it("should be able to send multipart mail to tls smtp server") {
			val html = <html><body><h1>HELLO</h1></body></html>
			Given("An email")
			val email = EmailMessage(
					to = List(EmailAddress("test tested <test.tested@somewhere.com>")),
					from = EmailAddress("from someone <from@someone.com>"),
					subject = "Test subject",
					altText = Some("HELLO"),
					html = html
			)
			When("sending mail by the tls mail client")
			assert(Success({}) === sender.send(email))
			Then("should one should recieve mail")
			val msg = greenMail.getReceivedMessages()(0)
			assert("Test subject" === msg.getSubject())
			msg.getContent() match {
				case mmp: MimeMultipart => 
					assert("HELLO" === mmp.getBodyPart(0).getContent)
					assert(html.toString() === mmp.getBodyPart(1).getContent)
				case _ => fail()
			}
		}
		
		it("should be able to send html mail to tls smtp server") {
			val html = <html><body><h1>HELLO</h1></body></html>
			Given("An email")
			val email = EmailMessage(
					to = List(EmailAddress("test tested <test.tested@somewhere.com>")),
					from = EmailAddress("from someone <from@someone.com>"),
					subject = "Test subject",
					altText = None,
					html = html
			)
			When("sending mail by the tls mail client")
			sender.send(email)
			Then("should one should recieve mail")
			val msg = greenMail.getReceivedMessages()(0)
			assert("Test subject" === msg.getSubject())
			msg.getContent() match {
				case mmp: MimeMultipart => 
					assert("HELLO" === mmp.getBodyPart(0).getContent)
				case _ => fail()
			}
		}
	}

}