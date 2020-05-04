package uk.nhs.cdss.registry;

import java.util.function.Supplier;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLInputFactory;
import lombok.SneakyThrows;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.ucdos.wsdl.CheckCapacitySummaryResponse;

@Component
public class CheckSummaryResponseRegistry implements Supplier<CheckCapacitySummaryResponse> {

  @SneakyThrows
  public CheckCapacitySummaryResponse get() {
    // read file
    var resolver = new PathMatchingResourcePatternResolver();
    var response = resolver.getResource("/dosResponses/soap.xml");
    var xmlStream = XMLInputFactory.newFactory().createXMLStreamReader(response.getInputStream());

    // unwrap SOAP envelope
    xmlStream.nextTag(); // advance to <Envelope>
    xmlStream.nextTag(); // advance to <Body>
    xmlStream.nextTag(); // advance to <CheckCapacitySummaryResponse>

    // convert it to our POJO type
    final var responseType = CheckCapacitySummaryResponse.class;
    var unmarshaller = JAXBContext.newInstance(responseType).createUnmarshaller();
    return unmarshaller.unmarshal(xmlStream, responseType).getValue();
  }

}
