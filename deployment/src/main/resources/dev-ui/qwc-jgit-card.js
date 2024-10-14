import { LitElement, html, css} from 'lit';
import { pages } from 'build-time-data';
import 'qwc/qwc-extension-link.js';

const NAME = "JGit";
export class QwcJGitCard extends LitElement {

    static styles = css`
      .identity {
        display: flex;
        justify-content: flex-start;
      }

      .description {
        padding-bottom: 10px;
      }

      .logo {
        padding-bottom: 10px;
        margin-right: 5px;
      }

      .card-content {
        color: var(--lumo-contrast-90pct);
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        padding: 2px 2px;
        height: 100%;
      }

      .card-content slot {
        display: flex;
        flex-flow: column wrap;
        padding-top: 5px;
      }
    `;

    static properties = {
        description: {type: String}
    };

    constructor() {
        super();
    }

    connectedCallback() {
        super.connectedCallback();
    }

    render() {
        return html`<div class="card-content" slot="content">
            <div class="identity">
                <div class="logo">
                    <img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI5MnB0IiBoZWlnaHQ9IjkycHQiIHZpZXdCb3g9IjAgMCA5MiA5MiI+PGRlZnM+PGNsaXBQYXRoIGlkPSJhIj48cGF0aCBkPSJNMCAuMTEzaDkxLjg4N1Y5MkgwWm0wIDAiLz48L2NsaXBQYXRoPjwvZGVmcz48ZyBjbGlwLXBhdGg9InVybCgjYSkiPjxwYXRoIHN0eWxlPSJzdHJva2U6bm9uZTtmaWxsLXJ1bGU6bm9uemVybztmaWxsOiNmMDNjMmU7ZmlsbC1vcGFjaXR5OjEiIGQ9Ik05MC4xNTYgNDEuOTY1IDUwLjAzNiAxLjg0OGE1LjkxOCA1LjkxOCAwIDAgMC04LjM3MiAwbC04LjMyOCA4LjMzMiAxMC41NjYgMTAuNTY2YTcuMDMgNy4wMyAwIDAgMSA3LjIzIDEuNjg0IDcuMDM0IDcuMDM0IDAgMCAxIDEuNjY5IDcuMjc3bDEwLjE4NyAxMC4xODRhNy4wMjggNy4wMjggMCAwIDEgNy4yNzggMS42NzIgNy4wNCA3LjA0IDAgMCAxIDAgOS45NTcgNy4wNSA3LjA1IDAgMCAxLTkuOTY1IDAgNy4wNDQgNy4wNDQgMCAwIDEtMS41MjgtNy42NmwtOS41LTkuNDk3VjU5LjM2YTcuMDQgNy4wNCAwIDAgMSAxLjg2IDExLjI5IDcuMDQgNy4wNCAwIDAgMS05Ljk1NyAwIDcuMDQgNy4wNCAwIDAgMSAwLTkuOTU4IDcuMDYgNy4wNiAwIDAgMSAyLjMwNC0xLjUzOVYzMy45MjZhNy4wNDkgNy4wNDkgMCAwIDEtMy44Mi05LjIzNEwyOS4yNDIgMTQuMjcyIDEuNzMgNDEuNzc3YTUuOTI1IDUuOTI1IDAgMCAwIDAgOC4zNzFMNDEuODUyIDkwLjI3YTUuOTI1IDUuOTI1IDAgMCAwIDguMzcgMGwzOS45MzQtMzkuOTM0YTUuOTI1IDUuOTI1IDAgMCAwIDAtOC4zNzEiLz48L2c+PC9zdmc+"
                                       alt="${NAME}" 
                                       title="${NAME}"
                                       width="32" 
                                       height="32">
                </div>
                <div class="description">${this.description}</div>
            </div>
            ${this._renderCardLinks()}
        </div>
        `;
    }

    _renderCardLinks(){
        return html`${pages.map(page => html`
                            <qwc-extension-link slot="link"
                                extensionName="${NAME}"
                                iconName="${page.icon}"
                                displayName="${page.title}"
                                staticLabel="${page.staticLabel}"
                                dynamicLabel="${page.dynamicLabel}"
                                streamingLabel="${page.streamingLabel}"
                                path="${page.id}"
                                ?embed=${page.embed}
                                externalUrl="${page.metadata.externalUrl}"
                                webcomponent="${page.componentLink}" >
                            </qwc-extension-link>
                        `)}`;
    }

}
customElements.define('qwc-jgit-card', QwcJGitCard);