declare module '*.css' {
  // @ts-ignore
  import { CSSResult } from 'lit-element';
  const content: CSSResult;
  export default content;
}
