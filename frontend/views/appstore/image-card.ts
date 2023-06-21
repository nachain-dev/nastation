import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout';
import '@vaadin/vaadin-select';

@customElement('appstore-image-card')
export class ImageCard extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<li class="bg-contrast-5 flex flex-col items-start p-m rounded-l">
      <div
        class="bg-contrast flex items-center justify-center mb-m overflow-hidden rounded-m w-full"
        style="height: 160px;"
      >
        <img id="image" class="w-full" style="cursor: pointer" />
      </div>
      <span class="text-xl font-semibold" id="innerHeader" style="cursor: pointer"></span>
      <span theme="badge" id="badge"></span>
    </li> `;
  }
}
