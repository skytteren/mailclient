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

case class EmailAddress(value: String) extends AnyVal

case class EmailMessage(
	to: Seq[EmailAddress],
	cc: Seq[EmailAddress] = Nil,
	bcc: Seq[EmailAddress] = Nil,
	from: EmailAddress,
	subject: String,
	//altText: String,
	html: NodeSeq)

abstract class MailSender(mailer: String) {

	val props = new Properties();
	props.put("mail.smtp.auth", "true");

	val session = Session.getInstance(props)
	
	def send(emailMessage: EmailMessage): Unit = {
		import emailMessage._

		val msg = new MimeMessage(session)

		def toAddress(emailAddress: EmailAddress): Address = InternetAddress.parse(emailAddress.value, false).head
		
		import Message.RecipientType._
		
		val toAddresses = to.map(toAddress)
		toAddresses.foreach(msg.addRecipient(TO, _))

		val ccAddresses = cc.map(toAddress)
		ccAddresses.foreach(msg.addRecipient(CC, _))
		
		val bccAddresses = bcc.map(toAddress)
		bccAddresses.foreach(msg.addRecipient(BCC, _))
		
		msg.setFrom(toAddress(from))
		
		msg.setSubject(subject)

		// TODO Add alternative text for mail
		//msg.setText(altText)
		
		collect(html, msg)
		
		msg.setHeader("X-Mailer", mailer)
		msg.setSentDate(new Date())
		
		sendMessage(msg, toAddresses ++ ccAddresses ++ bccAddresses )
	}
	
	protected def sendMessage(msg: MimeMessage, toCcBccAddresses: Seq[Address]): Unit

	private def collect(in: NodeSeq, msg: Message): Unit = {
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(in.mkString, "text/html")))
	}
}

class TLSMailSender(host: String, port: Int = 587, mailer: String, username: String, password: String) 
	extends MailSender(mailer){
	
	props.put("mail.smtp.starttls.enable", "true");
	
	protected def sendMessage(msg: MimeMessage, toCcBccAddresses: Seq[Address]): Unit = {
		val transport = session.getTransport("smtp")
		transport.connect(host, port, username, password)
		transport.sendMessage(msg, toCcBccAddresses.toArray )
		transport.close()
	}
	
}

class GMailMailSender(mailer: String, username: String, password: String) 
	extends TLSMailSender(host = "smtp.gmail.com", mailer = mailer, username = username, password = password)
