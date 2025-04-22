module.exports = {
    outputDir: 'dist',
    assetsDir: 'static',
    devServer: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
}