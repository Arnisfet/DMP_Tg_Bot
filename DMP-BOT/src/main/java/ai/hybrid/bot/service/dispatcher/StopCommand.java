package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnApp;
import ai.hybrid.bot.data.YarnAppListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StopCommand implements CommandInterface {
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * @return
     */
    @Override
    public String getCommand() {
        return "Stop";
    }

    /**
     * @param config
     */
    @Override
    public void launch(SshConfig.ClusterConfig config, String job) {
        String host = config.getHost();
        String query = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8088)
                .path("/ws/v1/cluster/apps")
                .queryParam("states", "RUNNING,ACCEPTED")
                .build()
                .toUriString();

        ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(query, YarnAppListDTO.class);

        List<YarnApp> yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        yarnAppList.stream()
                .filter(app -> app.getName().contains(job))
                .findFirst()
                .ifPresentOrElse(app -> {
                    String appId = app.getId();
                    String killUrl = UriComponentsBuilder.newInstance()
                            .scheme("http")
                            .host(config.getHost())
                            .port(8088)
                            .path("/ws/v1/cluster/apps/{appId}/state")
                            .buildAndExpand(appId)
                            .toUriString();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> request = new HttpEntity<>("{\"state\":\"KILLED\"}", headers);

                    try {
                        ResponseEntity<String> killResponse = restTemplate.exchange(killUrl, HttpMethod.PUT, request, String.class);

                        // If it got redirected
                        if (killResponse.getStatusCode().is3xxRedirection()) {
                            String redirectUrl = killResponse.getHeaders().getLocation().toString();
                            restTemplate.exchange(redirectUrl, HttpMethod.PUT, request, String.class);
                            log.info("Followed redirect and killed app: {}", appId);
                        } else {
                            log.info("Successfully killed app: {}", appId);
                        }
                    } catch (HttpStatusCodeException ex) {
                        log.error("Error killing app {}: {} {}", appId, ex.getStatusCode(), ex.getResponseBodyAsString());
                    }
                }, () -> log.warn("App with name '{}' not found", job));
    }
}
