import java.io.FileInputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class TestSOAP {
  public static void main(String args[]) throws Exception {
    URL url = new URL("http://api.google.com/GoogleSearch.wsdl");
    QName serviceName = new QName("urn:GoogleSearch", "GoogleSearchService");
    QName portName = new QName("urn:GoogleSearch", "GoogleSearchPort");
    Service service = Service.create(url, serviceName);
    Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class,
        Service.Mode.MESSAGE);

    SOAPMessage request = MessageFactory.newInstance().createMessage(null,
        new FileInputStream("yourGoogleKey.xml"));

    SOAPMessage response = dispatch.invoke(request);
    response.writeTo(System.out);
  }
}