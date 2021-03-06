/*
 * Copyright (C) 2018 Chan Chung Kwong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.fooledit.editor.email;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javax.mail.*;
import javax.mail.internet.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class EmailTest extends Application{
	private static final String from="admin@fooledit.cc";
	private static final String to="1m02math@126.com";
	private static final String password="";
	@Override
	public void start(Stage primaryStage) throws Exception{
		Properties props=new Properties();
		props.setProperty("mail.debug","true");
		props.setProperty("mail.imap.auth","true");
		props.setProperty("mail.imap.host","imap.mxhichina.com");
		props.setProperty("mail.store.protocol","imap");
		props.put("mail.imap.ssl.enable","true");
		Session session=Session.getInstance(props,new Authenticator(){
			@Override
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(from,password);
			}
		});
		primaryStage.setScene(new Scene(new MailBoxViewer(session)));
		primaryStage.show();
	}
	public static void main(String[] args) throws MessagingException{
		launch(args);
	}
	private static void send() throws MessagingException{
		Properties props=new Properties();
		props.setProperty("mail.debug","true");
		props.setProperty("mail.smtp.auth","true");
		props.setProperty("mail.smtp.host","smtp.mxhichina.com");
		props.setProperty("mail.transport.protocol","smtp");
		props.put("mail.smtp.ssl.enable","true");
		Session session=Session.getInstance(props);
		MimeMessage msg=new MimeMessage(session);
		msg.setFrom(from);
		msg.setSubject("Hello world");
		msg.setRecipients(Message.RecipientType.TO,to);
		msg.setText("javax.mail is Good");
		Transport.send(msg,from,password);
	}
}
