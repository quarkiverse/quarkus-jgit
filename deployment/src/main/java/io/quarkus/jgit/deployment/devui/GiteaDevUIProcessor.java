package io.quarkus.jgit.deployment.devui;

import java.util.Optional;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.jgit.deployment.GiteaDevServiceInfoBuildItem;

public class GiteaDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createCard(Optional<GiteaDevServiceInfoBuildItem> info, BuildProducer<CardPageBuildItem> cardPage) {
        info.ifPresent(i -> {
            String url = "http://" + i.host() + ":" + i.httpPort();
            CardPageBuildItem card = new CardPageBuildItem();
            card.addPage(Page.externalPageBuilder("Gitea Dashboard")
                    .doNotEmbed()
                    .icon("font-awesome-solid:code-branch")
                    .url(url, url));
            cardPage.produce(card);
        });
    }
}
