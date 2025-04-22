class Logger {
    static info(message, ...args) {
        console.log(`[${new Date().toISOString()}] INFO: ${message}`, ...args);
    }

    static error(message, ...args) {
        console.error(`[${new Date().toISOString()}] ERROR: ${message}`, ...args);
    }

    static warn(message, ...args) {
        console.warn(`[${new Date().toISOString()}] WARN: ${message}`, ...args);
    }
}

export default Logger;