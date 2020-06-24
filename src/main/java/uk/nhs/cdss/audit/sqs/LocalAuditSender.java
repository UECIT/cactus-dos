package uk.nhs.cdss.audit.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.nhs.cdss.audit.model.AuditSession;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class LocalAuditSender implements AuditSender {

    private final ObjectMapper mapper;
    private final RestTemplate auditRestTemplate;

    @Value("${sqs.audit.queue}")
    private String loggingQueue;

    @Value("${service.name}")
    private String serviceName;

    @Override
    public void sendAudit(AuditSession audit) {
        if (StringUtils.isEmpty(loggingQueue)) {
            logAudit(audit, "No audit server configured");
            return;
        }

        try {
            var request = RequestEntity.post(URI.create(loggingQueue + "/send/" + serviceName))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(audit);

            auditRestTemplate.exchange(request, String.class);
        } catch (ResourceAccessException e) {
            logAudit(audit, "Audit server configured but cannot connect");
        }
    }

    @SneakyThrows
    private void logAudit(AuditSession audit, String message) {
        log.info(message + ": " + mapper.writeValueAsString(audit));
    }
}
