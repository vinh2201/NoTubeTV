// Enables 4K resolution tricking youtube into thinking that we are on a 4K TV
(function() {

        var existing = document.querySelector('meta[name="viewport"]');
        if (existing) {
            existing.setAttribute('content', 'width=3840, height=2160, initial-scale=1.0');
        } else {
            var meta = document.createElement('meta');
            meta.name = 'viewport';
            meta.content = 'width=3840, height=2160, initial-scale=1.0';
            document.head.appendChild(meta);
        }
})();