// Replaces search button and triggers the mod menu on select.

(() => {
  const CUSTOM_BUTTON_ID = 'custom-guide-entry-plus';

  const createCustomButton = () => {
    const originalButton = document.querySelector('ytlr-button.ytLrGuideEntryRendererButton');
    if (!originalButton) return null;
    const customButton = originalButton.cloneNode(true);
    customButton.id = CUSTOM_BUTTON_ID;
    customButton.ariaLabel = 'Plus';
    customButton.tabIndex = '0';
    customButton.removeAttribute('aria-hidden');
    customButton.querySelectorAll('[idomkey]').forEach(el => el.removeAttribute('idomkey'));
    const label = customButton.querySelector('yt-formatted-string');
    if (label) label.textContent = 'Plus';
    const icon = customButton.querySelector('yt-icon');
    if (icon) icon.className = 'ytContribIconBarsThree ytContribIconHost ytLrAvatarLockupIcon';
    return customButton;
  };

  let focusCount = 0;

  const simulateKeyPress = () => {
    focusCount++;
    if (focusCount % 2 === 1) {
      document.activeElement.dispatchEvent(new KeyboardEvent('keydown', {
        keyCode: 404,
        code: 'KeyNotFound',
        bubbles: true,
        cancelable: true
      }));
    }
  };

  const ensureCustomButton = () => {
    const container = document.querySelector('yt-focus-container.ytLrGuideResponseContainer');
    if (!container) return;
    let customButton = document.getElementById(CUSTOM_BUTTON_ID);
    const originalButton = container.querySelector('ytlr-button.ytLrGuideEntryRendererButton');
    if (!customButton && originalButton) {
      customButton = createCustomButton();
      if (customButton) {
        originalButton.replaceWith(customButton);
        customButton.addEventListener('focus', simulateKeyPress);
      }
    }
    if (customButton && originalButton && customButton !== originalButton) {
      originalButton.replaceWith(customButton);
      customButton.addEventListener('focus', simulateKeyPress);
    }
  };

  const monitorGuide = () => {
    const container = document.querySelector('yt-focus-container.ytLrGuideResponseContainer');
    if (!container) return;
    new MutationObserver((mutations) => {
      if (mutations.some(m => m.addedNodes.length || m.removedNodes.length ||
          m.target.matches('yt-focus-container.ytLrGuideResponseContainer, ytlr-button.ytLrGuideEntryRendererButton'))) {
        ensureCustomButton();
      }
    }).observe(container, { childList: true, subtree: true, attributes: true });
    const parent = container.parentElement;
    if (parent) {
      new MutationObserver(ensureCustomButton).observe(parent, { childList: true, subtree: true });
    }
    ensureCustomButton();
  };

  const interval = setInterval(() => {
    const container = document.querySelector('yt-focus-container.ytLrGuideResponseContainer');
    if (container) {
      clearInterval(interval);
      monitorGuide();
    }
  }, 300);
})();