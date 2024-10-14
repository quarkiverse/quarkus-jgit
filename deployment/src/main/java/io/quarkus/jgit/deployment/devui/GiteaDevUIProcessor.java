package io.quarkus.jgit.deployment.devui;

import java.util.Optional;

import org.eclipse.jgit.api.Git;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.ExternalPageBuilder;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.jgit.deployment.GiteaDevServiceInfoBuildItem;

public class GiteaDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createCard(Optional<GiteaDevServiceInfoBuildItem> info, BuildProducer<CardPageBuildItem> cardPage) {
        CardPageBuildItem card = new CardPageBuildItem();

        info.ifPresent(i -> {
            String url = "http://" + i.host() + ":" + i.httpPort();
            card.addPage(Page.externalPageBuilder("Gitea Dashboard")
                    .doNotEmbed()
                    .icon("font-awesome-solid:code-branch")
                    .url(url, url));
        });

        final ExternalPageBuilder versionPage = Page.externalPageBuilder("JGit Version")
                .icon("font-awesome-solid:tag")
                .url("https://www.eclipse.org/jgit/")
                .doNotEmbed()
                .staticLabel(Git.class.getPackage().getImplementationVersion());

        card.addPage(versionPage);

        card.setCustomCard("qwc-jgit-card.js");
        cardPage.produce(card);
    }
}