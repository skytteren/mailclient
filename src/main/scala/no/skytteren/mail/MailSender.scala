package no.skytteren.mail

import java.io._
import java.util.Properties
import java.util.Date
import javax.mail._
import javax.activation._
import javax.mail.util._
import scala.xml.NodeSeq
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import scala.util.Try
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeBodyPart

case class EmailAddress(value: String) extends AnyVal

case class EmailMessage(
	to: Seq[EmailAddress],
	cc: Seq[EmailAddress] = Nil,
	bcc: Seq[EmailAddress] = Nil,
	from: EmailAddress,
	subject: String,
	altText: Option[String],
	html: NodeSeq)

abstract class MailSender(mailer: String) {

	val props = new Properties();
	props.put("mail.smtp.auth", "true");

	val session = Session.getInstance(props)
	
	def send(emailMessage: EmailMessage): Try[Unit] = {
		import emailMessage._
		
		def toAddress(emailAddress: EmailAddress): Address = InternetAddress.parse(emailAddress.value, false).head

		val toAddresses = to.map(toAddress)
		val ccAddresses = cc.map(toAddress)
		val bccAddresses = bcc.map(toAddress)
		
		val tried = Try{
			val msg = new MimeMessage(session)

			import Message.RecipientType._
			toAddresses.foreach(msg.addRecipient(TO, _))
			ccAddresses.foreach(msg.addRecipient(CC, _))
			bccAddresses.foreach(msg.addRecipient(BCC, _))
			msg.setFrom(toAddress(from))
			
			msg.setSubject(subject)
			msg.setSentDate(new Date());
			msg.setHeader("X-Mailer", mailer)
	
			altText match {
				case Some(txt) => 
					val textPart = new MimeBodyPart();
			    textPart.setContent(txt, "text/plain")
					
			    val htmlPart = new MimeBodyPart();
			    htmlPart.setDataHandler(new DataHandler(new ByteArrayDataSource(html.mkString, "text/html")))
			    
			    val mp = new MimeMultipart("alternative");
					mp.addBodyPart(textPart);
					mp.addBodyPart(htmlPart);
			    msg.setContent(mp);
				case None => 
					msg.setDataHandler(new DataHandler(new ByteArrayDataSource(html.mkString, "text/html")))
			}
			
	    msg.saveChanges();
	    msg
		}
		tried.flatMap(msg => sendMessage(msg, toAddresses ++ ccAddresses ++ bccAddresses ))
	}
	
   protected def sendMessage(msg: MimeMessage, toCcBccAddresses: Seq[Address]): Try[Unit]

}

class TLSMailSender(host: String, port: Int = 587, mailer: String, username: String, password: String) 
	extends MailSender(mailer){
	
	props.put("mail.smtp.starttls.enable", "true");
	
	protected def sendMessage(msg: MimeMessage, toCcBccAddresses: Seq[Address]): Try[Unit] = Try{
		val transport = session.getTransport("smtp")
		transport.connect(host, port, username, password)
		transport.sendMessage(msg, toCcBccAddresses.toArray )
		transport.close()
	}
	
}

class GMailMailSender(mailer: String, username: String, password: String) 
	extends TLSMailSender(host = "smtp.gmail.com", mailer = mailer, username = username, password = password)
