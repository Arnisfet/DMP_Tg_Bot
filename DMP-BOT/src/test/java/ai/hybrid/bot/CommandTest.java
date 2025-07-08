package ai.hybrid.bot;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.YarnApp;
import ai.hybrid.bot.data.YarnAppListDTO;
import ai.hybrid.bot.service.dispatcher.RestartCommand;
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
public class CommandTest {
    @Autowired
    SshConfig config;
    @Autowired
    StopCommand stopCommand;
    @Autowired
    StartCommand startCommand;
    @Autowired
    RestartCommand restartCommand;
    private final RestTemplate restTemplate = new RestTemplate();
    @Test
    public void startApp_thenStopTest() throws InterruptedException {
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        startCommand.launch(cluster, "LoopLauncher");

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
        stopCommand.launch(cluster, "LoopLauncher");
    }
    @Test
    public void startApp_thenRestartTest() throws InterruptedException {
        SshConfig.ClusterConfig cluster = config.getClusters().get("RU");
        startCommand.launch(cluster, "LoopLauncher");

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
        restartCommand.launch(cluster, "LoopLauncher");
        Thread.sleep(20000);

        yarnAppList = Optional.ofNullable(response.getBody())
                .map(YarnAppListDTO::getApps)
                .map(YarnAppListDTO.AppContainer::getApp)
                .orElse(List.of());

        acceptedApp = yarnAppList.stream()
                .filter(app -> (app.getName().contains("LoopLauncher") && app.getState().equals("RUNNING"))
                        || app.getState().equals("ACCEPTED"))
                .findFirst();
        Assert.isTrue(acceptedApp.isPresent(), "App not found");
        response = restTemplate.getForEntity(query, YarnAppListDTO.class);
        stopCommand.launch(cluster, "LoopLauncher");
    }

}
