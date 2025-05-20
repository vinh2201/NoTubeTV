// Add a "button" to fool you...
(function () {
  function addMenuButton() {

    const searchBar = document.querySelector('ytlr-search-text-box');
    if (!searchBar) return;

    const parent = searchBar.parentNode;
    if (parent.querySelector('button[data-notubetv="menu"]'))
      return // already exists

    // Align horizontally to the search box
    parent.style.display = 'flex';
    parent.style.flexDirection = 'row';
    parent.style.alignItems = 'center';

    // Create the NoTUbeTV Menu button
    const menuButton = document.createElement('button');
    menuButton.setAttribute('data-notubetv', 'menu');
    menuButton.textContent = 'NoTUbeTV Menu';
    menuButton.style.marginLeft = '60px';
    menuButton.style.padding = '16px 32px';
    menuButton.style.background = 'linear-gradient(90deg, #ff0000, #cc0000, #000000)';
    menuButton.style.color = '#fff';
    menuButton.style.border = 'none';
    menuButton.style.borderRadius = '22px';
    menuButton.style.fontSize = '60px';
    menuButton.style.fontWeight = 'bold';
    menuButton.style.height = '120px';

    // Insert right next the search box
    parent.insertBefore(menuButton, searchBar.nextSibling);
  }

  addMenuButton();

  const observer = new MutationObserver((mutations) => {
    if (window.location.pathname.endsWith('/search') || window.location.href.endsWith('/search?')) {
      return;
    }

    const searchBar = document.querySelector('ytlr-search-text-box');
    if (searchBar && !searchBar.parentNode.querySelector('[data-notubetv="menu"]')) {
      addMenuButton(); // Re-add if missing
    }
  });

  observer.observe(document.body, {
    childList: true,
    subtree: true,
  });
})();

// Here the fooling part begins.
// If the search tab is focused and the 'right arrow" is pressed, open up the menu.
(function () {
  document.addEventListener('keydown', function (event) {
    if (event.key === 'ArrowRight') {
     const searchBar = document.querySelector('ytlr-search-text-box');
     const isFocused = searchBar?.classList?.contains('ytLrSearchTextBoxFocused');
     if (searchBar && isFocused) {
        const event = new KeyboardEvent('keydown', {
            keyCode: 404,
            which: 404,
            bubbles: true,
            cancelable: true
        });
        document.dispatchEvent(event);
        menuButton = document.querySelector('button[data-notubetv="menu"]');
        menuButton.style.background = 'white'
     }
    }
  });
})();
