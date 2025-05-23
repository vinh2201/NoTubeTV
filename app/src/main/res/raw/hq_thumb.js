// ==UserScript==
// @name         YouTube Leanback HQ Thumbnail
// @namespace    https://sudomaker.com/
// @version      2025-04-30
// @description  Replace YouTube TV's default thumbnails with really HQ ones
// @author       ClassicOldSong
// @match        https://www.youtube.com/tv
// @icon         https://www.google.com/s2/favicons?sz=64&domain=youtube.com
// @grant        none
// ==/UserScript==

const hook = (target, prop, handler) => {
    const oldDescriptor = Object.getOwnPropertyDescriptor(target, prop)
    const newDescriptor = handler(oldDescriptor)
    Object.defineProperty(target, prop, newDescriptor)
}

(function() {
    'use strict';

    const {HTMLElement} = document.defaultView

    const proxyHandler = {
        set(target, prop, value) {
            if (prop === 'cssText' && !value.startsWith('background-image:url("data:')) {
                value = value.replace('hqdefault', 'hq720')
            }

            return Reflect.set(target, prop, value)
        }
    }

    hook(HTMLElement.prototype, 'style', ({get, set}) => {
        return {
            get() {
                const _style = get.call(this)
                if (this.tagName === 'YTLR-THUMBNAIL-DETAILS') {
                    return new Proxy(_style, proxyHandler)
                }
                return _style
            },
            set(val) {
                console.log('Setting style of', this.tagName, ':', val)
                set.call(this, val)
            }
        }
    })
})();