package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnAppListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        String url = config.getHost();
        String queryUrl = url + "/ws/v1/cluster/apps?states=RUNNING";

        try {
            ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(queryUrl, YarnAppListDTO.class);
            YarnAppListDTO apps = response.getBody();
            if (apps == null || apps.getApps() == null) {
                log.warn("No applications found.");
                return;
            }
        } catch () {

        }
    }
}
