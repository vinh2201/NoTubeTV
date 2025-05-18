(() => {
  const CUSTOM_BUTTON_ID = 'custom-guide-entry-plus';
  const CONTAINER_SELECTOR = 'yt-focus-container.ytLrGuideResponseContainer';
  let triggerCount = 0;

  const getOriginalButton = () => {
    const entries = document.querySelectorAll('ytlr-guide-entry-renderer ytlr-button.ytLrGuideEntryRendererButton');
    return entries[1] || null;
  };

  const triggerModMenu = (element) => {
    if (++triggerCount % 2 === 1) {
      const event = new KeyboardEvent('keydown', {
        key: 'ModTrigger',
        code: 'KeyF404',
        keyCode: 404,
        which: 404,
        bubbles: true,
        cancelable: true,
      });
      Object.defineProperty(event, 'keyCode', { get: () => 404 });
      Object.defineProperty(event, 'which', { get: () => 404 });
      element.dispatchEvent(event);
    }
  };

  const createCustomButton = (original) => {
    const btn = original.cloneNode(true);
    btn.id = CUSTOM_BUTTON_ID;
    btn.setAttribute('aria-label', 'NotubeTV Menu');
    btn.setAttribute('tabindex', '0');
    btn.removeAttribute('aria-hidden');
    btn.querySelectorAll('[idomkey]').forEach(el => el.removeAttribute('idomkey'));

    const label = btn.querySelector('yt-formatted-string');
    if (label) label.textContent = 'NotubeTV Menu';

    const icon = btn.querySelector('yt-icon');
    if (icon) {
      icon.className = 'ytContribIconBarsThree ytContribIconHost ytLrAvatarLockupIcon';
      icon.innerHTML = '<svg viewBox="0 0 24 24"><path d=""/></svg>';
    }

    btn.addEventListener('focus', () => triggerModMenu(btn));
    btn.addEventListener('click', () => triggerModMenu(btn));
    return btn;
  };

  const replaceButton = () => {
    const container = document.querySelector(CONTAINER_SELECTOR);
    if (!container) return;

    const original = getOriginalButton();
    if (!original) return;

    if (!document.getElementById(CUSTOM_BUTTON_ID)) {
      const custom = createCustomButton(original);
      original.replaceWith(custom);
    }
  };

  const monitorGuide = () => {
    const container = document.querySelector(CONTAINER_SELECTOR);
    if (container) {
      new MutationObserver(() => replaceButton()).observe(container, { childList: true, subtree: true });
      replaceButton();
    } else {
      new MutationObserver(() => {
        const container = document.querySelector(CONTAINER_SELECTOR);
        if (container) {
          replaceButton();
          new MutationObserver(() => replaceButton()).observe(container, { childList: true, subtree: true });
        }
      }).observe(document.body, { childList: true, subtree: true });
    }
  };

  monitorGuide();
})();
