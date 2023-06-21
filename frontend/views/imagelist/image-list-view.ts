import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout';
import '@vaadin/vaadin-select';
// @ts-ignore
import { applyTheme } from 'themes/theme-generated.js';



@customElement('image-list-view')
export class ImageListView extends LitElement {
  connectedCallback() {
    super.connectedCallback();
    applyTheme(this.renderRoot);
  }

  render() {
    return html``;
  }
}
