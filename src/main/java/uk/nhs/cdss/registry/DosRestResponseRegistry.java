package uk.nhs.cdss.registry;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.model.enums.Rag;
import uk.nhs.cdss.model.ucdos.CapacityStatus;
import uk.nhs.cdss.model.ucdos.DosRestResponse;
import uk.nhs.cdss.model.ucdos.OpeningTime;
import uk.nhs.cdss.model.ucdos.OpeningTimeRota;
import uk.nhs.cdss.model.ucdos.OpeningTimeSession;
import uk.nhs.cdss.model.ucdos.PhoneContactPoints;
import uk.nhs.cdss.model.ucdos.ReferralInstructions;
import uk.nhs.cdss.model.ucdos.Service;
import uk.nhs.cdss.model.ucdos.ServiceType;

@Component
public class DosRestResponseRegistry implements Supplier<DosRestResponse> {

  public DosRestResponse get() {
    var response = new DosRestResponse();

    response.setCatchAll(true);
    response.setCode("hardCode");
    response.setServiceCount(2);
    response.setStatus("success");
    response.setTransactionId("hardId");

    response.setServices(Arrays.asList(getService(1), getService(2)));

    return response;
  }

  private Service getService(int i) {
    var service = new Service();

    service.setServiceId("service#" + i);
    service.setServiceName("service number " + i);
    var type = new ServiceType();
    type.setId("type#" + i);
    type.setName("service type number " + i);
    service.setServiceType(type);

    service.setOdsCode("ods=" + i);
    service.setAddress(i + " Health St");
    service.setPostcode("SE" + i + " 0RV");
    service.setEasting(i * 10);
    service.setNorthing(i * 100);

    var phone = new PhoneContactPoints();
    var s = Integer.toString(i).repeat(3);
    phone.setPublicPhone(String.format("+44 1111 %s %s", s, s));
    phone.setNonPublicPhone(String.format("+44 2222 %s %s", s, s));
    phone.setFax(String.format("+44 3333 %s %s", s, s));
    service.setPhone(phone);
    service.setWebsite("http://services.com/api/Service/" + i);

    var referralInstructions = new ReferralInstructions();
    referralInstructions.setPublicInformation("You can refer to this service if " + i + " == " + i);
    referralInstructions.setReferralInformation("You can also refer if " + i * 10 + " == " + i * 10);
    service.setReferralInstructions(referralInstructions);

    var availableTimes = Stream.of("Monday", "Tuesday", "Friday", "Sunday").map(getTimes(i));
    var exceptions = Stream.of(getBankHolidayTimes(i));
    var rotas = Stream.concat(availableTimes, exceptions).collect(Collectors.toUnmodifiableList());
    service.setOpeningTimeRotas(rotas);

    service.setEndpoints(Stream.of(1, 10, 100, 1000)
        .map(factor -> factor * i)
        .map(n -> "http://invoke.service#" + n)
        .collect(Collectors.toUnmodifiableList()));

    service.setCapacityStatus(getCapacity(i));
    service.setDistanceFromPatient(i);

    return service;
  }

  private CapacityStatus getCapacity(int i) {
    var capacity = new CapacityStatus();
    capacity.setRag(Arrays.asList(Rag.GREEN, Rag.AMBER).get((i - 1) % 2));
    capacity.setHuman(Arrays.asList("HIGH", "LOW").get((i - 1) % 2));
    capacity.setHex("idk");
    return capacity;
  }

  private Function<String, OpeningTimeRota> getTimes(int i) {
    var session = new OpeningTimeSession(
        new OpeningTime(i, i),
        new OpeningTime(i + 10, i + 10));
    return day -> new OpeningTimeRota(true, day, session, null);
  }

  private OpeningTimeRota getBankHolidayTimes(int i) {
    return new OpeningTimeRota(
        false,
        null,
        new OpeningTimeSession(
            new OpeningTime(i + 2, i + 2),
            new OpeningTime(i + 3, i + 3)),
        "bank holidays only");
  }
}
