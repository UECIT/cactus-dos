package uk.nhs.cdss.service;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.model.InputBundle;
import uk.nhs.cdss.model.ucdos.ClinicalTermSearch;
import uk.nhs.cdss.model.ucdos.ServiceTypeSearch;
import uk.nhs.cdss.model.ucdos.wsdl.CheckCapacitySummary;
import uk.nhs.cdss.registry.CheckSummaryResponseRegistry;
import uk.nhs.cdss.registry.DosRestResponseRegistry;
import uk.nhs.cdss.transform.ucdos.in.CheckCapacityResponseTransformer;
import uk.nhs.cdss.transform.ucdos.in.DosRestResponseTransformer;
import uk.nhs.cdss.transform.ucdos.out.CheckCapacitySearchTransformer;
import uk.nhs.cdss.transform.ucdos.out.ClinicalTermSearchTransformer;
import uk.nhs.cdss.transform.ucdos.out.ServiceTypeSearchTransformer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UCDOSService {

  private final ServiceTypeSearchTransformer serviceTypeSearchTransformer;
  private final ClinicalTermSearchTransformer clinicalTermSearchTransformer;
  private final CheckCapacitySearchTransformer checkCapacitySearchTransformer;
  private final CheckCapacityResponseTransformer checkCapacityResponseTransformer;
  private final DosRestResponseTransformer dosRestResponseTransformer;
  private final CheckSummaryResponseRegistry checkSummaryResponseRegistry;
  private final DosRestResponseRegistry dosRestResponseRegistry;
  private final IParser fhirParser;

  public void invokeUCDOS(InputBundle inputBundle) {

    ServiceTypeSearch serviceTypeSearch = serviceTypeSearchTransformer.transform(inputBundle);
    ClinicalTermSearch clinicalTermSearch = clinicalTermSearchTransformer.transform(inputBundle);
    CheckCapacitySummary checkCapacitySearch = checkCapacitySearchTransformer.transform(inputBundle);

    log.info("ServiceType Search: {}", serviceTypeSearch);
    log.info("ClinicalTerm Search: {}", clinicalTermSearch);
    log.info("CheckCapacitySummary Search: {}", serialiseXml(checkCapacitySearch));

    var dosRestResponse = dosRestResponseRegistry.get();
    Parameters restServices = dosRestResponseTransformer.transform(dosRestResponse);
    log.info("ServiceType/ClinicalTerm Response:");
    log.info("from: {}", dosRestResponse);
    log.info("to {}", fhirParser.encodeResourceToString(restServices));

    var checkCapacityResponse = checkSummaryResponseRegistry.get();
    Parameters soapServices = checkCapacityResponseTransformer.transform(checkCapacityResponse);
    log.info("from: {}", serialiseXml(checkCapacityResponse));
    log.info("to {}", fhirParser.encodeResourceToString(soapServices));
  }

  private static String serialiseXml(Object data) {
    try {
      return new XmlMapper().writeValueAsString(data);
    } catch (JsonProcessingException e) {
      return "[failed to serialise]";
    }
  }
}
