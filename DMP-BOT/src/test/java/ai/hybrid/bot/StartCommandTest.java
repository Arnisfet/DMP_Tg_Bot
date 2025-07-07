package ai.hybrid.bot;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnApp;
import ai.hybrid.bot.data.YarnAppListDTO;
import ai.hybrid.bot.service.dispatcher.StartCommand;
import ai.hybrid.bot.service.dispatcher.StopCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = "spring.profiles.active=dev")
public class StartCommandTest {
    @Autowired
    SshConfig config;
    @Autowired
    StopCommand stopCommand;
    private final RestTemplate restTemplate = new RestTemplate();
    @Test
    public void launchTest() throws InterruptedException {

        StartCommand startCommand = new StartCommand();
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        startCommand.launch(cluster, "Start", "LoopLauncher");

        String host = cluster.getHost();
        String query = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8088)
                .path("/ws/v1/cluster/apps?states=ACCEPTED")
                .build().toUriString();
            Thread.sleep(20000);

        ResponseEntity<YarnAppListDTO> response = restTemplate.getForEntity(query, YarnAppListDTO.class);

        List<YarnApp> yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        Optional<YarnApp> acceptedApp = yarnAppList.stream()
                .filter(app -> (app.getName().contains("LoopLauncher") && app.getState().equals("RUNNING"))
                        || app.getState().equals("ACCEPTED"))
                .findFirst();
        Assert.isTrue(acceptedApp.isPresent(), "App not found");
//        Thread.sleep(10000);
        stopCommand.launch(cluster, "Start", "LoopLauncher");
    }
}
