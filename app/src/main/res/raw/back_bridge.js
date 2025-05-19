// Translates the native back press to "escape key".
// Patched with garbage events to make it work.

(function () {
  function dispatchKey(key, keyCode, code) {
    const downEvent = new KeyboardEvent('keydown', {
      key: key,
      keyCode: keyCode,
      code: code,
      which: keyCode,
      bubbles: true,
      cancelable: true
    });
    const upEvent = new KeyboardEvent('keyup', {
      key: key,
      keyCode: keyCode,
      code: code,
      which: keyCode,
      bubbles: true,
      cancelable: true
    });
    document.dispatchEvent(downEvent);
    document.dispatchEvent(upEvent);
  }


  dispatchKey('Escape', 27, 'Escape');
})();