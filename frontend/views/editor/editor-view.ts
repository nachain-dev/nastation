import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-rich-text-editor';
import '@vaadin/vaadin-select';


@customElement('editor-view')
export class EditorView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html``;
  }
}
