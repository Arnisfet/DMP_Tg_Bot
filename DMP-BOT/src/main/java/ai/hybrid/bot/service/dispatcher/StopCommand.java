package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnApp;
import ai.hybrid.bot.data.YarnAppListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
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
     * @param action
     */
    @Override
    public void launch(SshConfig.ClusterConfig config, String action, String job) {
        String host = config.getHost();
        String query = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8088)
                .path("/ws/v1/cluster/apps?states=RUNNING")
                .build().toUriString();

            ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(query, YarnAppListDTO.class);
            List<YarnApp> yarnAppList = Optional.ofNullable(response.getBody())
                    .map(YarnAppListDTO::getApps)
                    .map(YarnAppListDTO.AppContainer::getApp)
                    .orElse(List.of());

            yarnAppList.stream()
                    .filter(app -> app.getName().equals(job))
                    .findFirst()
                    .ifPresentOrElse(app -> {
                        String appId = app.getId();
                        String killUrl = UriComponentsBuilder.newInstance()
                                .scheme("http")
                                .host(host)
                                .port(8088)
                                .path("/ws/v1/cluster/apps/{appId}/state")
                                .buildAndExpand(appId)
                                .toUriString();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        String body = "{\"state\":\"KILLED\"}";
                        HttpEntity<String> request = new HttpEntity<>(body,headers);
                        restTemplate.exchange(killUrl, HttpMethod.PUT, request, String.class);
                        log.info("Successfully killed app: {}", appId);
                    }, () -> log.warn("App with name '{}' not found", job));

    }
}
