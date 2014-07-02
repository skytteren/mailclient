Simple Scala wrapper for Java Mail
==========

Here is an signup confirm mail example from http://isitdown.no using GMail :


      import no.skytteren.mail._
      
      val mailSender = new GMailMailSender(mailer = "IsItDown", username = "userName", password = "not so s3cr3t")
      
      val form : String = "sk@IsItDown.NOw"
      
      def sendSignupMail(email: String, from: String, url: String, text: String, confirmUrl: String){
        val htmlContent = 
    			<div>
    				<h1>Thank you for signing up!</h1>
    				<p>
    					In order for us to tell you that {url}<br/> 
    					doesn't contain {text}<br/>
    					follow the link: <a href={confirmUrl}>{confirmUrl}</a><br/>
    				</p>
    				<br/>
    				<p>Stein Kåre</p>
    				<p>sk@IsItDown.NOw</p>
    			</div>
        logger.info(html.mkString)
        mailSender.send(EmailMessage(
        		to = EmailAddress(email) :: Nil,
        		from = EmailAddress(from),
        		subject = "Confirm signup",
        		html = htmlContent
        ))
      }
