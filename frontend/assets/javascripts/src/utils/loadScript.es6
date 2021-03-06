
const loadScript = (src, props) => {
    // #? type checks like this should be redundant post es6/flow conversion
    if (typeof src !== 'string') {
        return Promise.reject(new Error('no src supplied'));
    }

    if (document.querySelector(`script[src="${src}"]`)) {
        return Promise.resolve();
    }

    return new Promise((resolve, reject) => {
        const ref = document.scripts[0];
        const script = document.createElement('script');
        script.src = src;
        if (props) {
            Object.assign(script, props);
        }
        script.onload = resolve;
        script.onerror = reject;
        if (ref.parentNode) {
            ref.parentNode.insertBefore(script, ref);
        }
    });
};

export { loadScript }
