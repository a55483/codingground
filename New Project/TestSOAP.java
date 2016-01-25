import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;

import javax.xml.soap.SOAPElement;


//import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.jws.soap.SOAPBinding.Style;

public class TestSOAP{

     public static void main(String []args){
        System.out.println("test SOAP");
        
        try { 
            MessageFactory messageFactory = MessageFactory.newInstance(); 
            SOAPMessage message = messageFactory.createMessage(); 

            //Create objects for the message parts
            SOAPPart     part     = message.getSOAPPart(); 
            SOAPEnvelope envelope = part.getEnvelope(); 
            SOAPBody     body     = envelope.getBody(); 
            
            
            String testWsdl = "https://fps.amazonaws.com/doc/2008-09-17/AmazonFPS.wsdl" ;
            SOAPElement bodyElement = body.addChildElement(envelope.createName("echo", "req",testWsdl));
            
            /*
          
            bodyElement.addChildElement("category") .addTextNode("classifieds");
            message.saveChanges(); 
            SOAPPart SOAPpartbefore = message.getSOAPPart();
            SOAPEnvelope reqenv = SOAPpartbefore.getEnvelope();
            
            System.out.println("REQUEST:"); 
            System.out.println(reqenv.toString()); 
            
            */
            
            //Now create the connection SOAPConnectionFactory 
            /*
            SOAPConnFactory = SOAPConnectionFactory.newInstance(); 
            SOAPConnection connection = SOAPConnFactory.createConnection(); 
            SOAPMessage reply = connection.call(message, testWsdl); 
            SOAPPart SOAPpart = reply.getSOAPPart(); 
            SOAPEnvelope replyenv = SOAPpart.getEnvelope();
            System.out.println("\nRESPONSE:"); 
            System.out.println(replyenv.toString());
            connection.close(); 
            */
            
            
        } catch (Exception e){ 
            System.out.println(e.getMessage()); 
        }
    } 
}



